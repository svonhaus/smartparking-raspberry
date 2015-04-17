package controller

import java.io.{InputStreamReader, BufferedReader, DataOutputStream, IOException}
import java.net.{HttpURLConnection, URL}
import java.util.Random

import com.phidgets.event._
import com.phidgets.{ InterfaceKitPhidget, PhidgetException }
import config.Config
import data.DataAdd

import scala.util.{ Failure, Try }

/**
 * Created by Steven on 18-03-15.
 */
object InterfaceKit 
{
  private val interfaceKit: InterfaceKitPhidget = new InterfaceKitPhidget()
  addAttachListener
  addDetachListener
  openAny

  for(i <- 4 to 7) allumer_led(i)

  def getStreamForValuesFromSensor(index: Int, oldValue:Option[Int]): Stream[Option[Int]] =
  {       
    var newValue:Option[Int] = None
    
    if (interfaceKit.isAttached) 
    {
      Sensors.listAnalogSensors.filter(x => x._1 == index).head match
      {
        case ((indexSensor:Int, (functionToUse:(Int => Option[Int]), (treshold:Int, interval:Long))))  =>
        {
          newValue = functionToUse(interfaceKit.getSensorValue(index))
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

  def openAny = interfaceKit.openAny()
  def isAttached = interfaceKit.isAttached
  def getSensorValue(index: Int) = interfaceKit.getSensorValue(index)
  def close = interfaceKit.close()

  //true si un touché a déjà été fait, false sinon.
  private var touch = false

  /**
   * Détection de changement pour les capteurs et actions résultantes
   */
  def addSensorChangeListener =
  {
    Try(interfaceKit.addSensorChangeListener(new SensorChangeListener {
      override def sensorChanged(sensorChangeEvent: SensorChangeEvent) = {
        sensorChangeEvent.getIndex match {
            case Config.TEMP_SENSOR => {
              //si changement de température, envoi de celle-ci sur le webservice et action si elle est trop élevée.
              println("Changement de température")
              val temp = (getSensorValue(Config.TEMP_SENSOR) * 0.2222) - 61.111
              println(temp)
              DataAdd.updateTemp(temp)
              check_problem_temp(temp)
            }
            case Config.MAGNETIC_SENSOR => {
              //si changement, envoi de la place et si elle est prise ou non, allume la led de la place et recalcule les trajets
              println("Changement de magnetisme")
              val result = getSensorValue(Config.MAGNETIC_SENSOR)
              DataAdd.updateParkingSpace(Config.PLACE_NUM, true) //TODO
              allumer_led(Config.LED_PLACE)
            }
            case Config.TOUCH_SENSOR => {
              //si changement, envoi de la place et si elle est prise ou non, allume la led de la place et recalcule les trajets
              println("Changement de touché")
              val touchResult = getSensorValue(Config.TOUCH_SENSOR)
              if(!touch && touchResult != 0) {
                TouchSensor.touchControl(interfaceKit)
                touch = true
              } else if (touchResult == 0) touch = false
            }
            case Config.VIBRATION_SENSOR => {
              //action et envoi sur webservice si changement et valeur trop élevée
              println("Changement de vibration")
              val vibration = getSensorValue(Config.VIBRATION_SENSOR)
              println(vibration)
              check_problem_temp(vibration)
            }
        }
      }
    })) match {
      case Failure(exc : PhidgetException) => println(exc.getDescription)
      case _                               =>
    }
  }

  def allumer_led(num: Int) {
    interfaceKit.setOutputState(num, true)
  }

  def eteindre_led(num: Int) {
    interfaceKit.setOutputState(num, false)
  }

  def check_problem_temp(temp : Double) {
    if (temp >= 30) {
      faire_clignoter(Config.LED_TEMP_PROBLEM)
    } else {
      eteindre_led(Config.LED_TEMP_PROBLEM)
    }
  }

  def check_problem_vibration(vibration : Double) {
    if (vibration >= 100) { //TODO
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
