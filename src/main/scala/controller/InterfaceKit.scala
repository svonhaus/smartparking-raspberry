package controller

import Actor._
import akka.actor.{Props, ActorSystem}
import com.phidgets.event._
import com.phidgets.{ InterfaceKitPhidget, PhidgetException }
import config.{MyProperties, Config}
import data.DataAdd
import view.UtilConsole

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
  ActorManager.initialize()

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
      UtilConsole.showMessage("Wait for attachment.", getClass.getName, "INFORMATION_MESSAGE")

      Try(interfaceKit.waitForAttachment(1000)) match {
        case Failure(ex: PhidgetException) => UtilConsole.showMessage("Timeout has occured while waiting for interface kit during initialization, the program will still wait for it.", getClass.getName, "ERROR_MESSAGE")
        case _                             =>
      }
    }

  def addAttachListener =
    {
      interfaceKit.addAttachListener(new AttachListener {
        override def attached(attachEvent: AttachEvent) = UtilConsole.showMessage("Attachment of " + attachEvent, getClass.getName, "INFORMATION_MESSAGE")
      })
    }

  def addDetachListener =
    {
      interfaceKit.addDetachListener(new DetachListener {
        override def detached(detachEvent: DetachEvent) = UtilConsole.showMessage("Detachment of " + detachEvent, getClass.getName, "INFORMATION_MESSAGE")
      })
    }

  def addErrorListener =
    {
      interfaceKit.addErrorListener(new ErrorListener {
        override def error(errorEvent: ErrorEvent) = UtilConsole.showMessage(errorEvent.getException.getMessage, getClass.getName, "ERROR_MESSAGE")
      })
    }

  def openAny = interfaceKit.open(MyProperties.IK_SERIAL_NUMBER, MyProperties.PHIDGET_SERVER,MyProperties.PORT_PHIGET_SERVER)
  def isAttached = interfaceKit.isAttached
  def getSensorValue(index: Int) = interfaceKit.getSensorValue(index)
  def close = interfaceKit.close

  /**
   * modifie la sensibilité des capteurs
   */
  def changeSensibility = {
    interfaceKit.setSensorChangeTrigger(MyProperties.SHARP_SENSOR_1, 15)
    interfaceKit.setSensorChangeTrigger(MyProperties.SHARP_SENSOR_2, 15)
    interfaceKit.setSensorChangeTrigger(MyProperties.TEMP_SENSOR, 5)
    interfaceKit.setSensorChangeTrigger(MyProperties.MAGNETIC_SENSOR, 5)
    interfaceKit.setSensorChangeTrigger(MyProperties.TOUCH_SENSOR, 15)
    interfaceKit.setSensorChangeTrigger(MyProperties.VIBRATION_SENSOR, 10)
    interfaceKit.setSensorChangeTrigger(MyProperties.IR_SENSOR, 15)
  }

  /**
   * Détection de changement pour les capteurs et traitement des valeurs sur des actors
   */
  def addSensorChangeListener =
  {
    Try(interfaceKit.addSensorChangeListener(new SensorChangeListener
    {
      override def sensorChanged(sensorChangeEvent: SensorChangeEvent) = {
        val indexSensor = sensorChangeEvent.getIndex
        indexSensor match {
          case MyProperties.TEMP_SENSOR => {
            ActorManager.tempListenerActor ! getSensorValue(indexSensor)
          }
          /*case Config.MAGNETIC_SENSOR => {
            MagneticActor ! getSensorValue(indexSensor)
          }*/
          case MyProperties.IR_SENSOR => {
            ActorManager.iRListenerActor ! getSensorValue(indexSensor)
          }
          case MyProperties.TOUCH_SENSOR => {
            ActorManager.touchListenerActor ! getSensorValue(indexSensor)
          }
          case MyProperties.VIBRATION_SENSOR => {
            ActorManager.vibrationListenerActor ! getSensorValue(indexSensor)
          }
          case _ =>
        }
      }
    }))
    match
    {
      case Failure(exc : PhidgetException) => UtilConsole.showMessage(exc.getMessage, getClass.getName, "ERROR_MESSAGE")
      case _                               =>
    }
  }

  /**
   * Détection d'un changement d'output (led)
   */
  def addOutputChangeListener()
  {
    interfaceKit.addOutputChangeListener(new OutputChangeListener()
    {
      def outputChanged(oe: OutputChangeEvent)
      {
        UtilConsole.showMessage("Led n°" + oe.getIndex + " has changed to " + oe.getState, getClass.getName, "INFORMATION_MESSAGE")
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
      true
    }
    else
    {
      UtilConsole.showMessage("You must attach the interface kit to check if a car is passing the barrier!", getClass.getName, "WARNING_MESSAGE")
      false
    }
  }

  /**
   * allume la led numéro num
   * @param num emplacement de la led
   */
  def allumer_led(num: Int) {
    if(isAttached) interfaceKit.setOutputState(num, true)
  }

  /**
   * éteint la led numéro num
   * @param num emplacement de la led
   */
  def eteindre_led(num: Int) {
    if(isAttached) interfaceKit.setOutputState(num, false)
  }

  /* Clignotement de la led i */
  def faire_clignoter(i: Int)
  {
    for(ind <- 0 to 3)
    {
      UtilConsole.showMessage("Blinking of the led n° " + i, getClass.getName, "INFORMATION_MESSAGE")
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
      while (i < 5)
      {
        {
          UtilConsole.showMessage("Étage n° " + i, getClass.getName, "INFORMATION_MESSAGE")
          this.allumer_led(num)
          Thread.sleep(500)
          this.eteindre_led(num)
          Thread.sleep(500)
        }
        {
          i += 1;
          i - 1
        }
      }
    }
  }
}
