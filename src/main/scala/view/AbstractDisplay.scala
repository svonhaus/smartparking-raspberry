package view

import com.phidgets.event._
import controller.{RFID, Barriere, InterfaceKit}
import data.{DataAdd, DataGet}

import scala.util.{Failure, Success}

/**
 * Classe abstraite apellée au lancement de l'application.
 * Permet de fournir des méthodes génériques à une application en console ou en fenêtre.
 */
abstract class AbstractDisplay
{
  val barriere = new Barriere()

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
    InterfaceKit.openAny
    barriere.Barriere()

    RFID.rfid.addTagGainListener(new TagGainListener() //attend un scan de tag RFID
    {
      def tagGained(oe: TagGainEvent)
      {
        val tag = oe.getValue
        choisirAction()

        if (RFID.action != "write" && RFID.action != "update")
        {
          messageTagLu(tag) //Indique le tag scanné
          try {
              val user = DataGet.found(tag) /* recherche le tag et indique les informations du user, ou non si pas trouvé. */
              messagePerson(user.lastName, user.firstName, user.mail, user.inTheParking)

              if (RFID.action == "in" || RFID.action == "out")
              {
                val result = DataGet.searchTagUser(tag, RFID.action) // Vérifie si le user peut passer en entrée (in) ou en sortie (out)
                result match
                {
                  case "ok" => /* S'il peut passer, allume la led verte, ouvre la barriere, indique un message et détecte la présence de la voiture qui doit passer */
                  {
                    RFID.ledGreenOn()
                    barriere.ouverture
                    showMessage("L'utilisateur peut passer, faites entrer la voiture.", "Passage", "INFORMATION_MESSAGE")
                    RFID.carPassed(tag) match /* Si détection que la voiture est bien passée, on enregistre l'action, sinon on affiche une erreur. */
                    {
                      case true => {
                        DataAdd.addFlowParking(tag, RFID.action)
                        showMessage("La voiture est bien passée.", "Passage", "INFORMATION_MESSAGE")
                      }
                      case false => showMessage("La voiture n'est pas passée", "Passage", "ERROR_MESSAGE")
                    }
                    barriere.fermeture
                    RFID.ledGreenOff()
                  }
                  case _ => //message d'erreur si le user ne peut pas passer
                  {
                    RFID.ledRedOn()
                    showMessage("L'utilisateur ne peut pas passer. \nCause : " + result, "Passage", "ERROR_MESSAGE")
                    RFID.ledRedOff()
                  }
                }
              }
          } catch { //message d'erreur s'il y a eu un problème
            case exc : Exception => {
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
        println("detachment  1 of " + ae)
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
