package Actor

import akka.actor.Actor
import com.phidgets.InterfaceKitPhidget
import config.Config
import controller.{RFID, SharpSensorForCar}
import data.DataAdd
import view.UtilConsole

import scala.concurrent.duration._
import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success, Try}

/**
 * Created by timmy_machine on 22/04/15.
 */
class ManagerSharpSensorActor extends Actor
{
  import context.dispatcher

  val ik = new InterfaceKitPhidget()

  val functionSharpSensor = (x:Int) =>
  {
    if(x!=20)
    {
      val result = 4800 / (x - 20)
      if(result > 10 && result < 100) Some(result) else None
    }
    else None
  }

  override def preStart()
  {
    ik.open(319197,"laptop-steven.local",5001)
    ik.waitForAttachment()
  }

  def receive =
  {
    case List(tag:String, action:String) =>
    {
      Try(SharpSensorForCar.initializationBeforeCarComeIn(tag, action)) match
      {
        case Success(false) =>

        // Message are already provided by the above function.
        case Success(true) =>
        {
          if(waitCarToPass())
          {
            UtilConsole.showMessage(tag + " has passed the barrier.", getClass.getName, "INFORMATION_MESSAGE")
            DataAdd.addFlowParking(tag, action)
            Config.barriere.fermeture
            RFID.ledGreenOff()
          }
          else
          {
            UtilConsole.showMessage(tag + " has not passed the barrier.", getClass.getName, "INFORMATION_MESSAGE")
            Config.barriere.fermeture
            RFID.ledGreenOff()
          }
        }

        case Failure(ex:Throwable) =>
        {
          RFID.ledRedOn()
          UtilConsole.showMessage(ex.getMessage, "Scan", "ERROR_MESSAGE")
          RFID.ledRedOff()
        }
      }
    }

    case action:String =>
    {
      if(waitCarToPass())
      {
        UtilConsole.showMessage("Guest has passed the barrier.", getClass.getName, "INFORMATION_MESSAGE")
        Config.barriere.fermeture
        RFID.ledGreenOff()
      }
      else
      {
        UtilConsole.showMessage("Guest has not passed the barrier.", getClass.getName, "INFORMATION_MESSAGE")
        Config.barriere.fermeture
        RFID.ledGreenOff()
      }

    }
  }

  def waitCarToPass(): Boolean =
  {
    var nbOccurenceWithoutCar = 0
    var lastCaptorWithInformation = 0

    while(nbOccurenceWithoutCar < 10)
    {
      Thread.sleep(250)
      val result1 = Future(functionSharpSensor(ik.getSensorValue(0)))
      val result2 = Future(functionSharpSensor(ik.getSensorValue(1)))

      val promise = Promise[(Option[Int], Option[Int])]()

      promise.future.onComplete
      {
        case Success((Some(result1: Int), Some(result2: Int))) =>
        {
          UtilConsole.showMessage("Value from sensor 0 (cm): " + result1 + " AND Value from sensor1 :" + result2, getClass.getName, "INFORMATION_MESSAGE")
          nbOccurenceWithoutCar = 0
        }

        case Success((Some(result1: Int), None)) =>
        {
          UtilConsole.showMessage("Value from sensor 0 (cm): " + result1, getClass.getName, "INFORMATION_MESSAGE")
          lastCaptorWithInformation = 0
          nbOccurenceWithoutCar = 0
        }

        case Success((None, Some(result2: Int))) =>
        {
          UtilConsole.showMessage("Value from sensor1 :" + result2, getClass.getName, "INFORMATION_MESSAGE")
          lastCaptorWithInformation = 1
          nbOccurenceWithoutCar = 0
        }

        case Success((None, None)) =>
        {
          UtilConsole.showMessage("Occurence(s) without car : " + nbOccurenceWithoutCar, getClass.getName, "INFORMATION_MESSAGE")
          nbOccurenceWithoutCar += 1
        }

        case _ => UtilConsole.showMessage("Failure has occured.", getClass.getName, "ERROR_MESSAGE")
      }

      val promiseFuture: Future[Promise[(Option[Int], Option[Int])]] = for
      {
        part1 <- result1
        part2 <- result2

        result = promise.complete(Try(part1, part2))
      } yield result

      Await.ready(promise.future, 10 seconds)
    }

    return lastCaptorWithInformation == 1
  }
}