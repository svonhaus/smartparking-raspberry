package interfaceKit.controller

import com.phidgets.event._
import com.phidgets.{ InterfaceKitPhidget, PhidgetException }

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
}
