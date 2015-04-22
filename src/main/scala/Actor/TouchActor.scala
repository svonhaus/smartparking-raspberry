package Actor

import akka.actor.Actor
import config.Config
import controller.TouchSensor
import data.DataAdd

import scalaj.http.Http

/**
 * Created by timmy_machine on 22/04/15.
 */
class TouchActor extends Actor {

  //true si un touché a déjà été fait, false sinon.
  private var touch = false
  def receive = {
    case value: Int => {
      //si changement, envoi de la place et si elle est prise ou non, allume la led de la place et recalcule les trajets
      println("Changement de touché")
      println(value)
      if (!touch && value >= 100) {
        touch = true
        //TouchSensor.touchControl()
        println("touché")
      } else if (value <= 100) touch = false
    }
    case _     => println("problem from " + Config.TOUCH_SENSOR)
  }

}
