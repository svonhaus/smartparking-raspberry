package Actor

import java.lang.Double

import akka.actor.Actor
import config.Config
import data.{DataGet, DataAdd}

import scala.util.{Failure, Success}
import scala.concurrent.duration._

/**
 * Classe Actor qui reçoit les valeurs du capteur de température et les envoie sur la couche Data
 */
class TempActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, Config.INTERVAL_CHECK_TEMP, self, "checkTemp")

  //si changement de température, envoi de celle-ci sur le webservice et action si elle est trop élevée.
  def receive = {
    case value: Int => {
          val temp = (value * 0.2222) - 61.111
          println("Changement de température : " + temp)
          DataAdd.updateTemp(temp) match {
            case Success(rep) => println(rep)
            case Failure(exc) => println(exc)
          }
    }
    case "checkTemp" => {
      /* Procédure d'alerte si problème de température */
      val temp = DataGet.getTemp()
      temp match {
        case Success(temp) => {
          println("Température du parking : " + temp)
          if (temp >= Config.MAX_TEMP) {
            Config.IK.faire_clignoter(Config.LED_TEMP_PROBLEM)
          } else {
            Config.IK.eteindre_led(Config.LED_TEMP_PROBLEM)
          }
        }
        case Failure(exc) => println("Problème réseau")
      }
    }
    case _     => println("problem from " + Config.TEMP_SENSOR)
  }

}
