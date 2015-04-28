package Actor

import akka.actor.Actor
import config.Config
import controller.TouchSensor
import data.DataAdd
import view.UtilConsole

import scalaj.http.Http

/**
 * Classe Actor qui reçoit les valeurs du capteur de touché, exécute un traitement (entrée dans le parking et génération de qrcode) si il y a bien touché
 */
class TouchListenerActor extends Actor {

  //true si un touché a déjà été fait, false sinon.
  private var touch = false
  /*
    Si changement de valeur et on a déjà relaché le boutton avant, on traite dans touchControl
   */
  def receive =
  {
    case value: Int =>
    {
      UtilConsole.showMessage("Changement de touché : " + value, getClass.getName, "INFORMATION_MESSAGE")

      if (!touch && value >= 100)
      {
        touch = true

        TouchSensor.touchControl()
        UtilConsole.showMessage("Touched", getClass.getName, "INFORMATION_MESSAGE")

      } else if (value <= 100) touch = false
    }

    case _  => UtilConsole.showMessage("Actor error", "Problem from" + getClass.getName, "ERROR_MESSAGE")
  }

}
