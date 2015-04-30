package Actor

import akka.actor.Actor
import config.{Config, MyProperties}
import data.DataGet
import org.json.{JSONObject, JSONArray}
import view.UtilConsole

import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur infrarouge et les envoie sur la couche Data
 */
class IRCheckingActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, MyProperties.INTERVAL_CHECK_PLACES, self, "checkPlaces")

  /*
  Calcule le nombre de places pour un étage
   */
  def nbrPlaceAvailable(places : String, floor : String): Int = {
    new JSONObject(places).getJSONObject(floor).getInt("available")
  }

  /*
   si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place
  */
  def receive = {

    case "checkPlaces" => {
      /* Reconfiguration des itinéraires pour choisir une place libre */
      val places = DataGet.getPlaces()

      places match {
        case Success(places) => {
          val nbrPlaceAvailableF0 = nbrPlaceAvailable(places, "f0")
          val nbrPlaceAvailableF1 = nbrPlaceAvailable(places, "f1")
          val nbrPlaceAvailableTot = nbrPlaceAvailableF0 + nbrPlaceAvailableF1
          UtilConsole.showMessage("Places du parking disponibles : " + nbrPlaceAvailableTot, getClass.getName ,"INFORMATION_MESSAGE")
          UtilConsole.showMessage("Etage 0 : Places du parking disponibles : " + nbrPlaceAvailableF0, getClass.getName ,"INFORMATION_MESSAGE")
          UtilConsole.showMessage("1er étage : Places du parking disponibles : " + nbrPlaceAvailableF1, getClass.getName ,"INFORMATION_MESSAGE")
          if (nbrPlaceAvailableTot == 0)
            Config.IK.eteindre_led(MyProperties.LED_TOT)
          else
            Config.IK.allumer_led(MyProperties.LED_TOT)
          if (nbrPlaceAvailableF0 == 0)
            Config.IK.eteindre_led(MyProperties.LED_F0)
          else
            Config.IK.allumer_led(MyProperties.LED_TOT)
          if (nbrPlaceAvailableF1 == 0)
            Config.IK.eteindre_led(MyProperties.LED_F1)
          else
            Config.IK.allumer_led(MyProperties.LED_TOT)
        }
        case Failure(exc) => UtilConsole.showMessage("Error with webservice", getClass.getName, "ERROR_MESSAGE")
      }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
