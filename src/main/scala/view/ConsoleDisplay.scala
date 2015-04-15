package view

import com.phidgets.event.{TagGainEvent, TagGainListener}
import controller.RFID
import data.{DataAdd, DataGet}
import org.json.JSONObject

import scalaj.http.Http

/**
 * Démarrage de l'application sans interface graphique et affichage de message en console.
 * Les seules actions sont : in (l'entrée dans le parking) et out (la sortie du parking).
 */
class ConsoleDisplay extends AbstractDisplay
{
  override def messageTagLu(tag:String)
  {
    println("[INFO] Tag read : " + tag)
  }

  override def messagePerson(lastName:String, firstName:String, mail:String, inTheParking : Boolean)
  {
    println("[INFO] PERSON : last name = " + lastName + "; first name = " + firstName + "; mail = " + mail + "; statut = " + inTheParking)
  }

  override def showMessage(message:String, titre:String, typeMessage:String): Unit =
  {
    typeMessage match
    {
      case "ERROR_MESSAGE" => print("[ERROR] ")
      case "INFORMATION_MESSAGE" => print("[INFO] ")
      case "WARNING_MESSAGE" => print("[WARNING] ")
    }

    println(titre + " : " + message)
  }

  override def choisirAction(): Unit = {
    RFID.action = DataGet.foundAction()
  }

}
