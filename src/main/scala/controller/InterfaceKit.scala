package controller

import Actor._
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
  /*
    Initialisation de l'interface kit
   */
  private val interfaceKit: InterfaceKitPhidget = new InterfaceKitPhidget()
  addAttachListener
  addDetachListener
  openAny
  waitForAttachment
  changeSensibility
  addSensorChangeListener
  addOutputChangeListener

  /*
    initialisation des Actors
   */
  val systemTemp = ActorSystem("TempActor")
  val TempActor = systemTemp.actorOf(Props[TempActor])
  /*val systemMagn = ActorSystem("MagneticActor")
  val MagneticActor = systemMagn.actorOf(Props[MagneticActor])*/
  val systemIR = ActorSystem("IRActor")
  val IRActor = systemIR.actorOf(Props[IRActor])
  val systemTouch = ActorSystem("TouchActor")
  val TouchActor = systemTouch.actorOf(Props[TouchActor])
  val systemVibr = ActorSystem("VibrationActor")
  val VibrationActor = systemVibr.actorOf(Props[VibrationActor])

  //allume les leds au démarrage
  for(i <- 4 to 7) allumer_led(i)

  /*
    Permet de générer un stream de valeurs provenant d'un capteur
   */
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

  /**
   * modifie la sensibilité des capteurs
   */
  def changeSensibility = {
    interfaceKit.setSensorChangeTrigger(Config.SHARP_SENSOR_1, 15)
    interfaceKit.setSensorChangeTrigger(Config.SHARP_SENSOR_2, 15)
    interfaceKit.setSensorChangeTrigger(Config.TEMP_SENSOR, 5)
    interfaceKit.setSensorChangeTrigger(Config.MAGNETIC_SENSOR, 5)
    interfaceKit.setSensorChangeTrigger(Config.TOUCH_SENSOR, 15)
    interfaceKit.setSensorChangeTrigger(Config.VIBRATION_SENSOR, 10)
  }

  /**
   * Détection de changement pour les capteurs et traitement des valeurs sur des actors
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
          /*case Config.MAGNETIC_SENSOR => {
            MagneticActor ! getSensorValue(indexSensor)
          }*/
          case Config.IR_SENSOR => {
            IRActor ! getSensorValue(indexSensor)
          }
          case Config.TOUCH_SENSOR => {
            TouchActor ! getSensorValue(indexSensor)
          }
          case Config.VIBRATION_SENSOR => {
            VibrationActor ! getSensorValue(indexSensor)
          }
          case _ =>
        }
      }
    })) match {
      case Failure(exc : PhidgetException) => println(exc.getDescription)
      case _                               =>
    }
  }

  /**
   * Détection d'un changement d'output (led)
   */
  def addOutputChangeListener()
  {
    interfaceKit.addOutputChangeListener(new OutputChangeListener() {
      def outputChanged(oe: OutputChangeEvent) {
        println(oe.getIndex + " change to " + oe.getState)
      }
    })
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
      /*val systemSharp = ActorSystem("SharpActor")
      val SharpActor = systemTemp.actorOf(Props[SharpSensorActor])
      SharpActor ! "tick"
      true*/
    }
    else
    {
      println("You must attach the interfaceKit")
      false
    }
  }

  /**
   * allume la led numéro num
   * @param num emplacement de la led
   */
  def allumer_led(num: Int) {
    interfaceKit.setOutputState(num, true)
  }

  /**
   * éteint la led numéro num
   * @param num emplacement de la led
   */
  def eteindre_led(num: Int) {
    interfaceKit.setOutputState(num, false)
  }

  /* Clignotement de la led i */
  def faire_clignoter(i: Int) {
    for(ind <- 0 to 3) {
      println("clignotement")
      allumer_led(i)
      Thread.sleep(500)
      eteindre_led(i)
      Thread.sleep(500)
    }
  }

  /* Procédure pour allumer les leds pour aller à l'étage num */
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
