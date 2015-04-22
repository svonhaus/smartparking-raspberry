package controller

import Actor.{VibrationActor, TouchActor, MagneticActor, TempActor}
import akka.actor.{Props, ActorSystem}
import com.phidgets.event._
import com.phidgets.{ InterfaceKitPhidget, PhidgetException }
import config.Config
import data.DataAdd

import scala.util.{ Failure, Try }

/**
 * Classe controlleur de l'interfaceKit
 */
class InterfaceKit
{
  private val interfaceKit: InterfaceKitPhidget = new InterfaceKitPhidget()
  addAttachListener
  addDetachListener
  openAny
  waitForAttachment
  changeRate
  addSensorChangeListener

  val systemTemp = ActorSystem("TempActor")
  val TempActor = systemTemp.actorOf(Props[TempActor])
  val systemMagn = ActorSystem("MagneticActor")
  val MagneticActor = systemMagn.actorOf(Props[MagneticActor])
  val systemTouch = ActorSystem("TouchActor")
  val TouchActor = systemTouch.actorOf(Props[TouchActor])
  val systemVibr = ActorSystem("VibrationActor")
  val VibrationActor = systemVibr.actorOf(Props[VibrationActor])

  for(i <- 4 to 7) allumer_led(i)

  def getStreamForValuesFromSensor(index: Int, oldValue:Option[Int]): Stream[Option[Int]] =
  {       
    var newValue:Option[Int] = None
    
    if (isAttached)
    {
      Sensors.listAnalogSensors.filter(x => x._1 == index).head match
      {
        case ((indexSensor:Int, (functionToUse:(Int => Option[Int]), (treshold:Int, interval:Long))))  =>
        {
          newValue = functionToUse(getSensorValue(index))
        }
        case _ => 
      } 
    } 
    
    newValue #:: getStreamForValuesFromSensor(index, newValue)
  }
  
  def waitForAttachment =
    {
      println("wait for attachment")

      Try(interfaceKit.waitForAttachment()) match {
        case Failure(ex: PhidgetException) => println(ex.getDescription)
        case _                             =>
      }
    }

  def addAttachListener =
    {
      interfaceKit.addAttachListener(new AttachListener {
        override def attached(attachEvent: AttachEvent) = println("attachment of " + attachEvent)
      })
    }

  def addDetachListener =
    {
      interfaceKit.addDetachListener(new DetachListener {
        override def detached(detachEvent: DetachEvent) =
          {
            println("detachment of " + detachEvent)
          }
      })
    }

  def addErrorListener =
    {
      interfaceKit.addErrorListener(new ErrorListener {
        override def error(errorEvent: ErrorEvent) = println(errorEvent)
      })
    }

  def openAny = interfaceKit.openAny
  def isAttached = interfaceKit.isAttached
  def getSensorValue(index: Int) = interfaceKit.getSensorValue(index)
  def close = interfaceKit.close

  def changeRate = {
    interfaceKit.setSensorChangeTrigger(Config.SHARP_SENSOR_1, 15)
    interfaceKit.setSensorChangeTrigger(Config.SHARP_SENSOR_2, 15)
    interfaceKit.setSensorChangeTrigger(Config.TEMP_SENSOR, 5)
    interfaceKit.setSensorChangeTrigger(Config.MAGNETIC_SENSOR, 5)
    interfaceKit.setSensorChangeTrigger(Config.TOUCH_SENSOR, 15)
    interfaceKit.setSensorChangeTrigger(Config.VIBRATION_SENSOR, 10)
  }

  /**
   * Détection de changement pour les capteurs et actions résultantes
   */
  def addSensorChangeListener =
  {
    Try(interfaceKit.addSensorChangeListener(new SensorChangeListener {
      override def sensorChanged(sensorChangeEvent: SensorChangeEvent) = {
        val indexSensor = sensorChangeEvent.getIndex
        indexSensor match {
          case Config.TEMP_SENSOR => {
            TempActor ! getSensorValue(indexSensor)
          }
          case Config.MAGNETIC_SENSOR => {
            MagneticActor ! getSensorValue(indexSensor)
          }
          case Config.TOUCH_SENSOR => {
            TouchActor ! getSensorValue(indexSensor)
          }
          case Config.VIBRATION_SENSOR => {
            VibrationActor ! getSensorValue(indexSensor)
          }
          case _ => //println("changement autre")
        }

      }
    })) match {
      case Failure(exc : PhidgetException) => println(exc.getDescription)
      case _                               =>
    }
  }


  /**
   * Appel à l'interface kit permettant d'attendre qu'une voiture passe devant les capteurs de distances, laissant la barrière ouverte.
   * @return true si la voiture a pu passer, false sinon.
   */
  def carPassed () : Boolean =
  {
    if (isAttached)
    {
      val interfaceKitWaitCar = new InterfaceKitWaitCar()
      interfaceKitWaitCar.waitForCarToPassBarrier()
    }
    else
    {
      println("You must attach the interfaceKit")
      false
    }
  }

  def allumer_led(num: Int) {
    interfaceKit.setOutputState(num, true)
  }

  def eteindre_led(num: Int) {
    interfaceKit.setOutputState(num, false)
  }

  def check_problem_temp(temp : Double) {
    if (temp >= 40.0) {
      faire_clignoter(Config.LED_TEMP_PROBLEM)
    } else {
      eteindre_led(Config.LED_TEMP_PROBLEM)
    }
  }

  def check_problem_vibration(vibration : Double) {
    if (vibration >= 3000) { //TODO
      DataAdd.updateVibration(vibration)
      faire_clignoter(Config.LED_VIBRATION_PROBLEM)
    } else {
      eteindre_led(Config.LED_VIBRATION_PROBLEM)
    }
  }

  def faire_clignoter(i: Int) {
    while (true) {
      println("Clignotement car problème pouvant survenir dans le parking ...")
      allumer_led(i)
      Thread.sleep(500)
      eteindre_led(i)
      Thread.sleep(500)
    }
  }

  def go_to_floor(num: Int) {
    {
      var i: Int = 0
      while (i < 5) {
        {
          println(i)
          this.allumer_led(num)
          Thread.sleep(500)
          this.eteindre_led(num)
          Thread.sleep(500)
        }
        ({
          i += 1; i - 1
        })
      }
    }
  }

}
