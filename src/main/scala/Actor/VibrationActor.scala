package Actor

import akka.actor.Actor
import config.Config
import data.{DataAdd, DataGet}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur de vibration et les envoie sur la couche Data
 */
class VibrationActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, Config.INTERVAL_CHECK_VIBR, self, "checkVibr")

  /*
    action et envoi sur webservice si changement et valeur trop élevée
   */
  def receive = {
    case value: Int => {
      println("Changement de vibration : " + value)
      DataAdd.updateVibration(value)
    }
    /* Procédure d'alerte si problème de trembement de terre */
    case "checkVibr" => {
      val vibration = DataGet.getVibr()
      vibration match {
        case Success(vibration) => {
          println("Niveau de vibration du parking : " + vibration)
          if (vibration >= Config.MAX_VIBR) {
            Config.IK.faire_clignoter(Config.LED_VIBRATION_PROBLEM)
          } else {
            Config.IK.eteindre_led(Config.LED_VIBRATION_PROBLEM)
          }
        }
        case Failure(exc) => println("Problème réseau")
      }
    }
    case _     => println("problem from " + Config.VIBRATION_SENSOR)
  }
}
