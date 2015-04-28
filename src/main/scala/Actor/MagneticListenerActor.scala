package Actor

import akka.actor.Actor
import view.UtilConsole

/**
 * Classe Actor qui reçoit les valeurs du capteur magnétique et les envoie sur la couche Data
 */
class MagneticListenerActor extends Actor {

  /*si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place et recalcule les trajets
  */
  def receive =
  {
    case value: Int => UtilConsole.showMessage("Changement de magnetisme : " + value, getClass.getName, "INFORMATION_MESSAGE")
    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }

}
