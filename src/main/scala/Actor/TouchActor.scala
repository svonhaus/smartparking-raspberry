package Actor

import akka.actor.Actor
import config.Config
import controller.TouchSensor
import data.DataAdd

import scalaj.http.Http

/**
 * Classe Actor qui reçoit les valeurs du capteur de touché, exécute un traitement (entrée dans le parking et génération de qrcode) si il y a bien touché
 */
class TouchActor extends Actor {

  //true si un touché a déjà été fait, false sinon.
  private var touch = false
  /*
    Si changement de valeur et on a déjà relaché le boutton avant, on traite dans touchControl
   */
  def receive = {
    case value: Int => {
      println("Changement de touché : " + value)
      if (!touch && value >= 100) {
        touch = true
        //TouchSensor.touchControl()
        println("touché")
      } else if (value <= 100) touch = false
    }
    case _     => println("problem from " + Config.TOUCH_SENSOR)
  }

}
