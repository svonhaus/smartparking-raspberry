package view

import controller.RFID
import data.DataGet

/**
 * Démarrage de l'application sans interface graphique et affichage de message en console.
 * Les seules actions sont : in (l'entrée dans le parking) et out (la sortie du parking).
 */
class ConsoleDisplay extends AbstractDisplay
{
  override def messageTagLu(tag:String)
  {
    UtilConsole.showMessage("Tag read : " + tag, getClass.getName, "INFORMATION_MESSAGE")
  }

  override def messagePerson(lastName:String, firstName:String, mail:String, inTheParking : Boolean)
  {
    UtilConsole.showMessage("PERSON : last name = " + lastName + "; first name = " + firstName + "; mail = " + mail + "; statut = " + inTheParking, getClass.getName, "INFORMATION_MESSAGE")
  }

  override def showMessage(message:String, titre:String, typeMessage:String): Unit =
  {
    UtilConsole.showMessage(message, titre, typeMessage)
  }

  override def choisirAction(): Unit = {
    RFID.action = DataGet.foundAction()
  }

}
