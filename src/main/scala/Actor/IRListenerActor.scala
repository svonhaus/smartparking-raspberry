package Actor

import akka.actor.Actor
import config.{MyProperties, Config}
import data.DataAdd
import view.UtilConsole

import scala.util.{Failure, Success}

/**
 * Classe Actor qui reçoit les valeurs du capteur infrarouge et les envoie sur la couche Data
 */
class IRListenerActor extends Actor {

  //true si une détection a déjà été fait, false sinon.
  private var continueIn = true
  private var continueOut = false

  /*
   si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place
  */
  def receive = {
    case value: Int => {
      UtilConsole.showMessage("Changement de réflexion infra-rouge : " + value, getClass.getName, "INFORMATION_MESSAGE")

      if(value == 0 && continueIn) {
        DataAdd.updateParkingSpace(MyProperties.PLACE_NUM, false) match {
          case Success(rep) =>
            UtilConsole.showMessage(rep, getClass.getName, "INFORMATION_MESSAGE")
          case Failure(exc) =>
            UtilConsole.showMessage(exc.getMessage, getClass.getName, "ERROR_MESSAGE")
        }
        Config.IK.allumer_led(MyProperties.LED_PLACE)
        continueIn = false
        continueOut = true
      } else if (continueOut) {
        DataAdd.updateParkingSpace(MyProperties.PLACE_NUM, true) match {
          case Success(rep) =>
            UtilConsole.showMessage(rep, getClass.getName, "INFORMATION_MESSAGE")
          case Failure(exc) =>
            UtilConsole.showMessage(exc.getMessage, getClass.getName, "ERROR_MESSAGE")
        }
        UtilConsole.showMessage("libre", getClass.getName, "INFORMATION_MESSAGE")
        Config.IK.eteindre_led(MyProperties.LED_PLACE)
        continueIn = true
        continueOut = false
      }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
