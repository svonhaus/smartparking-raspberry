package Actor

import akka.actor.Actor
import config.MyProperties
import data.DataGet
import view.UtilConsole

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur infrarouge et les envoie sur la couche Data
 */
class IRCheckingActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, MyProperties.INTERVAL_CHECK_PLACES, self, "checkPlaces")

  /*si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place
  */
  def receive = {

    case "checkPlaces" => {
      /* Reconfiguration des itinéraires pour choisir une place libre */
      val places = DataGet.getPlaces()

      places match {
        case Success(places) => UtilConsole.showMessage("Places du parking : " + places, getClass.getName ,"INFORMATION_MESSAGE")
        case Failure(exc) => UtilConsole.showMessage("Error with webservice", getClass.getName, "ERROR_MESSAGE")
      }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
