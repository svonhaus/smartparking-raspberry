package Actor

import akka.actor.Actor
import config.Config
import data.{DataAdd, DataGet}
import view.UtilConsole

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur de vibration et les envoie sur la couche Data
 */
class VibrationListenerActor extends Actor {

  /*
    action et envoi sur webservice si changement et valeur trop élevée
   */
  def receive =
  {
    case value: Int =>
    {
      UtilConsole.showMessage("Changement de vibration : " + value, getClass.getName, "INFORMATION_MESSAGE")
      DataAdd.updateVibration(value)
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
