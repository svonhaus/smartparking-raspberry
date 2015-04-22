package Actor

import akka.actor.Actor
import config.Config
import data.DataAdd

import scalaj.http.Http

/**
 * Created by timmy_machine on 22/04/15.
 */
class VibrationActor extends Actor {
  def receive = {
    case value: Int => {
      //action et envoi sur webservice si changement et valeur trop élevée
      println("Changement de vibration")
      println(value)
      //check_problem_vibration(vibration)
    }
    case _     => println("problem from " + Config.VIBRATION_SENSOR)
  }
}
