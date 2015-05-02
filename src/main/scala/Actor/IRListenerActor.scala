package Actor

import akka.actor.Actor
import config.{MyProperties, Config}
import data.DataAdd
import view.UtilConsole

/**
 * Classe Actor qui reçoit les valeurs du capteur infrarouge et les envoie sur la couche Data
 */
class IRListenerActor extends Actor {
  /*si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place
  */
  def receive = {
    case value: Int => {
      UtilConsole.showMessage("Changement de réflexion infra-rouge : " + value, getClass.getName, "INFORMATION_MESSAGE")

      if(value == 0) {
        DataAdd.updateParkingSpace(MyProperties.PLACE_NUM, true)
        Config.IK.allumer_led(MyProperties.LED_PLACE)
      } else {
        DataAdd.updateParkingSpace(MyProperties.PLACE_NUM, false)
        Config.IK.eteindre_led(MyProperties.LED_PLACE)
      }
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }
}
