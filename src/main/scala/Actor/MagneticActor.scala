package Actor

import akka.actor.Actor
import config.Config
import data.DataAdd

/**
 * Classe Actor qui reçoit les valeurs du capteur magnétique et les envoie sur la couche Data
 */
class MagneticActor extends Actor {

  /*si changement de valeur, envoi de la place et si elle est prise ou non,
   allume la led de la place et recalcule les trajets
  */
  def receive = {
    case value: Int => {
      println("Changement de magnetisme : " + value)
    }
    case _     => println("problem from " + Config.MAGNETIC_SENSOR)
  }

}
