package Actor

import akka.actor.Actor
import config.Config
import data.DataAdd

import scalaj.http.Http

/**
 * Created by timmy_machine on 22/04/15.
 */
class MagneticActor extends Actor {
  def receive = {
    case value: Int => {
      //si changement, envoi de la place et si elle est prise ou non, allume la led de la place et recalcule les trajets
      println("Changement de magnetisme")
      println(value)
      DataAdd.updateParkingSpace(Config.PLACE_NUM, true) //TODO
      //allumer_led(Config.LED_PLACE)
    }
    case _     => println("problem from " + Config.MAGNETIC_SENSOR)
  }

}
