package Actor

import akka.actor.Actor
import config.Config
import data.{DataGet, DataAdd}
import scala.concurrent.duration._
import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur infrarouge et les envoie sur la couche Data
 */
class IRActor extends Actor {

  import context.dispatcher
  val tick = context.system.scheduler.schedule(0 millis, Config.INTERVAL_CHECK_PLACES, self, "checkPlaces")

  /*si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place
  */
  def receive = {
    case value: Int => {
      println("Changement de réflexion infra-rouge : " + value)
      if(value == 0) {
        DataAdd.updateParkingSpace(Config.PLACE_NUM, true)
        Config.IK.allumer_led(Config.LED_PLACE)
      } else {
        DataAdd.updateParkingSpace(Config.PLACE_NUM, false)
        Config.IK.eteindre_led(Config.LED_PLACE)
      }
    }
    case "checkPlaces" => {
      /* Reconfiguration des itinéraires pour choisir une place libre */
      val places = DataGet.getPlaces()
      places match {
        case Success(places) => {
          println("Places du parking : " + places)
        }
        case Failure(exc) => println("Problème réseau")
      }
    }
    case _     => println("problem from " + Config.IR_SENSOR)
  }

}
