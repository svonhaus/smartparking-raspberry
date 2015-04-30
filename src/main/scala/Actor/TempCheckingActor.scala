package Actor

import akka.actor.Actor
import config.{MyProperties, Config}
import data.DataGet
import view.UtilConsole
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur de température et les envoie sur la couche Data
 */
class TempCheckingActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, MyProperties.INTERVAL_CHECK_TEMP, self, "checkTemp")

  //si changement de température, envoi de celle-ci sur le webservice et action si elle est trop élevée.
  def receive = {

    case "checkTemp" =>
    {
      /* Procédure d'alerte si problème de température */
      val temp = DataGet.getTemp()

      temp match
      {
        case Success(temperature) =>
        {
          UtilConsole.showMessage("Température du parking : " + temperature, getClass.getName, "INFORMATION_MESSAGE")

          if (temperature >= MyProperties.MAX_TEMP) {
            Config.IK.faire _clignoter(MyProperties.LED_TEMP_PROBLEM)
          } else {
            Config.IK.eteindre_led(MyProperties.LED_TEMP_PROBLEM)
          }
        }
        case Failure(exc) => UtilConsole.showMessage("Error with webservice", getClass.getName, "ERROR_MESSAGE")
      }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }

}
