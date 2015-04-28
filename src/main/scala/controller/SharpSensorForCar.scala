package controller

import config.Config
import data.{DataAdd, DataGet}
import view.UtilConsole

/**
 * Created by Steven on 25-04-15.
 */
object SharpSensorForCar
{
  def initializationBeforeCarComeIn(tag:String, action:String): Boolean =
  {
    val result = DataGet.searchTagUser(tag, action)

    result match
    {
      case "ok" => /* S'il peut passer, allume la led verte, ouvre la barriere, indique un message et détecte la présence de la voiture qui doit passer */
      {
        RFID.ledGreenOn()
        Config.barriere.ouverture
        UtilConsole.showMessage(tag + " peut passer, faites entrer la voiture.", "Passage", "INFORMATION_MESSAGE")
        return true
      }
      case _ => //message d'erreur si le user ne peut pas passer
      {
        RFID.ledRedOn()
        UtilConsole.showMessage(tag + " ne peut pas passer. Cause : " + result, "Passage", "ERROR_MESSAGE")
        RFID.ledRedOff()

        return false
      }
    }
  }
}
