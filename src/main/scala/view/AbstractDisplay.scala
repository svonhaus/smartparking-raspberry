package view

import Actor.ActorManager
import com.phidgets.event._
import config.Config
import controller.TouchSensor._
import controller.{RFID, Barriere, InterfaceKit}
import data.{DataAdd, DataGet}

import scala.util.{Failure, Success}

/**
 * Classe abstraite apellée au lancement de l'application.
 * Permet de fournir des méthodes génériques à une application en console ou en fenêtre.
 */
abstract class AbstractDisplay
{

  /**
   * Affichage du tag scanné
   * @param tag : le tag scanné
   */
  def messageTagLu(tag:String)

  /**
   * Affiche les informations (en entrée) de l'utilisateur ayant scanné son tag RFID.
   * @param lastName : le nom de l'utilisateur qui a scanné son tag RFID.
   * @param firstName : le prénom de l'utilisateur.
   * @param mail : l'addresse a-mail de l'utilisateur.
   * @param inTheParking : true si la voiture de l'utilisateur est pour l'instant à l'intérieur du parking, false sinon.
   */
  def messagePerson(lastName:String, firstName:String, mail:String, inTheParking : Boolean)

  /**
   * Affiche un message d'information ou d'erreur.
   * @param message : message à afficher
   * @param titre : le titre du message
   * @param typeMessage : le type : ERROR OU INFORMATION
   */
  def showMessage(message:String, titre:String, typeMessage:String)

  /**
   * Choix de l'action à effectuer si pas d'interface graphique.
   */
  def choisirAction()

  /**
   * Initialisation des phidgets et écoute du RFID afin de scanner un tag RFID,
   * connaître sa situation et son statut pour faire passer un utilisateur ou non.
   * Appel au controller Barriere (servoMotor), InterfaceKit et RFID en vue d'utiliser les phidgets.
   */
  def initialize()
  {
    Config.barriere.Barriere()

    RFID.rfid.addTagGainListener(new TagGainListener() //attend un scan de tag RFID
    {
      def tagGained(oe: TagGainEvent)
      {
        val tag = oe.getValue
        choisirAction()

        if (RFID.action != "write" && RFID.action != "update")
        {
          messageTagLu(tag) //Indique le tag scanné

          try
          {
            val user = DataGet.found(tag) /* recherche le tag et indique les informations du user, ou non si pas trouvé. */
            messagePerson(user.lastName, user.firstName, user.mail, user.inTheParking)

            if (RFID.action == "in" || RFID.action == "out")
              ActorManager.waitCarToPassActor ! List(tag, RFID.action)
          }
          catch
            {
              case exc : Exception =>
              {
                messagePerson("", "", "", false)
                RFID.ledRedOn()
                showMessage(exc.getMessage, "Scan", "ERROR_MESSAGE")
                RFID.ledRedOff()
              }
            }
        }
      }
    })

    RFID.rfid.addDetachListener(new DetachListener()
    {
      def detached(ae: DetachEvent)
      {
        UtilConsole.showMessage("Detachment of " + ae, getClass.getName, "WARNING_MESSAGE")
        showMessage("Veuillez rattacher le Phidget RFID", "RFID detached", "WARNING_MESSAGE")
      }
    })

    RFID.rfid.addErrorListener(new ErrorListener()
    {
      def error(ee: ErrorEvent)
      {
        println("error event for " + ee)
        showMessage("Il y a eu une erreur pour l'événement " + ee, "RFID error", "ERROR_MESSAGE")
      }
    })
  }
}
