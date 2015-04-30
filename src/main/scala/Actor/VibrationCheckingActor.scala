package Actor

import akka.actor.Actor
import config.{MyProperties, Config}
import data.{DataAdd, DataGet}
import view.UtilConsole
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur de vibration et les envoie sur la couche Data
 */
class VibrationCheckingActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, MyProperties.INTERVAL_CHECK_VIBR, self, "checkVibr")

  /*
    action et envoi sur webservice si changement et valeur trop élevée
   */
  def receive =
  {
    /* Procédure d'alerte si problème de trembement de terre */
    case "checkVibr" =>
    {
      val vibration = DataGet.getVibr()

      vibration match
      {
        case Success(vibration) =>
        {
          UtilConsole.showMessage("Niveau de vibration du parking : " + vibration, getClass.getName, "INFORMATION_MESSAGE")

          if (vibration >= MyProperties.MAX_VIBR)
          {
            Config.IK.faire_clignoter(MyProperties.LED_VIBRATION_PROBLEM)
            ActorManager.synVocActor ! vibration
          } else {
            Config.IK.eteindre_led(MyProperties.LED_VIBRATION_PROBLEM)
          }
        }
        case Failure(exc) => UtilConsole.showMessage("Error with webservice", getClass.getName, "ERROR_MESSAGE")
      }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
