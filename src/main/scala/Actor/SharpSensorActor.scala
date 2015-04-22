package Actor

import akka.actor.Actor
import config.Config

import scala.concurrent.duration._
import scala.concurrent.{Await, Promise, Future}
import scala.util.{Try, Success}

/**
 * Created by timmy_machine on 22/04/15.
 */
class SharpSensorActor extends Actor
{
  import context.dispatcher
  var nbOccurenceWithoutCar = 0
  var lastCaptorWithInformation = 0

  val functionSharpSensor = (x:Int) =>
  {
    if(x!=20)
    {
      val result = 4800 / (x - 20)
      if(result > 10 && result < 100) Some(result) else None
    }
    else None
  }

  val tick = context.system.scheduler.schedule(0 millis, 250 millis, self, "tick")

  override def postStop() = tick.cancel()

  def receive =
  {
    case "tick" =>
    {
      println(nbOccurenceWithoutCar)

      val result1 = Future(Config.IK.getSensorValue(Config.SHARP_SENSOR_1))
      val result2 = Future(Config.IK.getSensorValue(Config.SHARP_SENSOR_2))


      val promise = Promise[(Option[Int], Option[Int])]()

      promise.future.onComplete
      {
        case Success((Some(result1: Int), Some(result2: Int))) =>
        {
          println("Value from sensor 0 (cm): " + result1 + " AND Value from sensor1 :" + result2)
        }

        case Success((Some(result1: Int), None)) =>
        {
          println("Value from sensor 0 (cm): " + result1)
          lastCaptorWithInformation = 0
          nbOccurenceWithoutCar = 0
        }

        case Success((None, Some(result2: Int))) =>
        {
          println("Value from sensor1 :" + result2)
          lastCaptorWithInformation = 1
          nbOccurenceWithoutCar = 0
        }

        case Success((None, None)) =>
        {
          nbOccurenceWithoutCar += 1
        }

        case _ => println("no result")
      }

      val promiseFuture: Future[Promise[(Option[Int], Option[Int])]] = for
      {
        part1 <- result1
        part2 <- result2

        result = promise.complete(Try(Some(part1), Some(part2)))
      } yield result

      Await.ready(promiseFuture, 3 seconds)

      if (nbOccurenceWithoutCar > 20 && lastCaptorWithInformation == 0)
      {
        println("The car has not passed the barrier.")
        /* SOMETHING TO DO */
        context.stop(self)
      }

      if (nbOccurenceWithoutCar > 10 && lastCaptorWithInformation == 1)
      {
        println("The car has passed the barrier.")
        /* SOMETHING TO DO */
        context.stop(self)
      }
    }
  }
}
