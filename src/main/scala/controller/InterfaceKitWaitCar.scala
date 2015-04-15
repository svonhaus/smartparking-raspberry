package controller

import data.DataAdd
import observable.ObservableSensors
import rx.lang.scala.Observable
import rx.lang.scala.schedulers.NewThreadScheduler
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._
import scala.util.{Failure, Success, Try}

class InterfaceKitWaitCar 
{
  def waitForCarToPassBarrier():Boolean =
  {
    val observableTupleWithInterval: Observable[(Option[Int], Option[Int])] = ObservableSensors.observableTuple(
      InterfaceKit.getStreamForValuesFromSensor(0, None), InterfaceKit.getStreamForValuesFromSensor(1, None), 500)

    if(waitCar(observableTupleWithInterval))
    {
      if(waitCarToComeIn(observableTupleWithInterval))
      {
        true
      }
      else
      {
        false
      }
    }
    else
    {
      false
    }
  }
  
  def waitCar(observableTupleWithInterval: Observable[(Option[Int], Option[Int])]):Boolean  =
  {
    /*
     * When someone has scanned its RFID. The driver has 60 secondes to reach the barrier.
     * If the car don't come in front of the sensors, we don't update the number of places available.
     */
    var carIsInFrontOfTheBarrier = false
    val endWaitCar = Promise[Boolean]()
    val onNextCoupleWaitCar: ((Option[Int], Option[Int])) => Unit =
    {
      case (Some(result1), Some(result2)) => if(!endWaitCar.isCompleted) endWaitCar.success(true)
      case _ =>
    }

    val subscriptionWaitCar = observableTupleWithInterval.subscribeOn(NewThreadScheduler()).subscribe(onNextCoupleWaitCar, ObservableSensors.errorWhatToDo)

    Try(Await.ready(endWaitCar.future, 10 seconds)) match
    {
      case Success(x) => println("One car is passing."); carIsInFrontOfTheBarrier = true
      case Failure(x) => println("Nobody enters in the parking.")
    }

    subscriptionWaitCar.unsubscribe()
    carIsInFrontOfTheBarrier
  }

  def waitCarToComeIn(observableTupleWithInterval: Observable[(Option[Int], Option[Int])]): Boolean =
  {
    /*
     * Afther the attachement has been done. We create 1 data stream for each sensor.
     * Then we create an observable over the values of the sensor 0 and the sensor 1
     */
    val endComeIn = Promise[Boolean]()
    var nbOccurrence = 0
    var nbOccurenceWithoutCar = 0
    var lastCaptorWithInformation = 0

    val onNextCouple: ((Option[Int], Option[Int])) => Unit =
    {
      case (Some(result1), Some(result2)) =>
      {
        println("Value from sensor 0 (cm): " + result1 +" AND Value from sensor1 :" + result2)
        nbOccurrence += 1
      }

      case (Some(result1), None) =>
      {
        println("Value from sensor 0 (cm): " + result1)
        lastCaptorWithInformation = 0
        nbOccurenceWithoutCar = 0
      }

      case (None, Some(result2)) =>
      {
        println("Value from sensor1 :" + result2)
        lastCaptorWithInformation = 1
        nbOccurenceWithoutCar = 0
      }

      case (None, None) =>
      {
        nbOccurenceWithoutCar += 1

        if(nbOccurenceWithoutCar > 50 && !endComeIn.isCompleted)
          endComeIn.success(true) // The car has finished.
      }

      case _ => println("no result")
    }

    val subscriptionCarComeIn = observableTupleWithInterval.subscribeOn(NewThreadScheduler()).subscribe(onNextCouple, ObservableSensors.errorWhatToDo)

    Await.ready(endComeIn.future, Duration.Inf)

    subscriptionCarComeIn.unsubscribe()

    if(nbOccurrence < 10 || lastCaptorWithInformation == 0)
    {
      println("The car didn't pass the barrier.")
      false
    }
    else
    {
      print("The car has passed the barrier.")
      true
    }
  }
}