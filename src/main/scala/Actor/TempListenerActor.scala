package Actor

import akka.actor.Actor
import config.Config
import data.{DataAdd, DataGet}
import view.UtilConsole

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur de température et les envoie sur la couche Data
 */
class TempListenerActor extends Actor {

  //si changement de température, envoi de celle-ci sur le webservice et action si elle est trop élevée.
  def receive =
  {
    case value: Int =>
    {
      val temp = (value * 0.2222) - 61.111
      UtilConsole.showMessage("Changement de température : " + temp, getClass.getName, "INFORMATION_MESSAGE")


      DataAdd.updateTemp(temp) match {
            case Success(rep) => UtilConsole.showMessage(rep, getClass.getName, "INFORMATION_MESSAGE")

            case Failure(exc) => UtilConsole.showMessage("Error with webservice", getClass.getName, "ERROR_MESSAGE")

          }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
