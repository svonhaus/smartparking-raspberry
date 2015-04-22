package Actor

import akka.actor.Actor
import config.Config
import controller.TouchSensor
import data.DataAdd

import scalaj.http.Http


class TempActor extends Actor {

  def receive = {
    case value: Int => {
          //si changement de température, envoi de celle-ci sur le webservice et action si elle est trop élevée.
          println("Changement de température")
          val temp = (value * 0.2222) - 61.111
          println(temp)
          DataAdd.updateTemp(temp)
          Thread.sleep(1000)
          val responseGet = Http.get("http://smartking.azurewebsites.net/api/Parking/temperature").header("Authorization", "Bearer " + Config.token).asString
          println("response get : " + responseGet)
          //check_problem_temp(temp)
    }
    case _     => println("problem from " + Config.TEMP_SENSOR)
  }

}
