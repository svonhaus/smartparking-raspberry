package view

import com.phidgets.event.{TagGainEvent, TagGainListener}
import controller.RFID
import data.{DataAdd, DataGet}

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
}
