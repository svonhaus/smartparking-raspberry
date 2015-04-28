package controller

import config.Config
import observable.ObservableSensors
import rx.lang.scala.schedulers.NewThreadScheduler
import view.UtilConsole

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise, _}

class InterfaceKitOneSensor(val indexSensor:Int)
{
  val endFuture = Promise[Boolean]()
  var touch = false
  
  def startSensor(duration:Long)
  {
    Future(launchOneSensor(duration))
  }
  
  def stopSensor()
  {
    endFuture.success(true)
  }
  
  def launchOneSensor(duration:Long) 
  {
    val myStream = Config.IK.getStreamForValuesFromSensor(indexSensor, None)

    val observableSensor = ObservableSensors.observableSimple(myStream, duration)
    val subscriptionCarComeIn = observableSensor.subscribeOn(NewThreadScheduler()).subscribe(onNextValueSensor, ObservableSensors.errorWhatToDo)  
    
    Await.ready(endFuture.future, Duration.Inf)
    subscriptionCarComeIn.unsubscribe()
  }

  val onNextValueSensor: (Option[Int]) => Unit =
  {
    case Some(result) =>  UtilConsole.showMessage("Value from Sensor " + indexSensor + " = " + result, getClass.getName, "INFORMATION_MESSAGE")
    case None =>
  }


}