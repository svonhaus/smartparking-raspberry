package controller

import java.net.UnknownHostException
import java.util.UUID

import com.phidgets._
import com.phidgets.event._
import data._
import model._

import scala.util._

/**
 * Classe controller RFID permettant d'initaliser, configurer le RFID et d'agir et de récupérer des informations sur le tag RFID
 * Relai entre la couche Vue permettant les intéractions et la couche Data permettant les échanges de données avec le WebService azur
 */
object RFID 
{
  val rfid: RFIDPhidget = new RFIDPhidget() // initialisation du RFID phidgets
  addAttachListener() //détecte l'attachement du RFID
  tagLossListener() //détection d'une perte de tag RFID
  addOutputChangeListener() //détection d'un changement d'état d'output (led)
  openAny()
  waitForAttachement()
  
  var action = "no"

  def addAttachListener()
  {
    rfid.addAttachListener(new AttachListener() {
      def attached(ae: AttachEvent) {
        try {
          (ae.getSource match {
            case aeRFID: RFIDPhidget => aeRFID
          }).setAntennaOn(true)
  
          (ae.getSource match {
            case aeRFID: RFIDPhidget => aeRFID
          }).setLEDOn(true)
        } catch {
          case exc: PhidgetException => println(exc)
        }
        println("attachment 1 of " + ae)
      }
    })
  }
  
  def tagLossListener()
  {
    rfid.addTagLossListener(new TagLossListener() {
      def tagLost(oe: TagLossEvent) {
        rfid.setOutputState(0, false)
        rfid.setOutputState(1, false)
      }
    })
  }
  
  def addOutputChangeListener()
  {
    rfid.addOutputChangeListener(new OutputChangeListener() {
      def outputChanged(oe: OutputChangeEvent) {
        println(oe.getIndex + " change to " + oe.getState)
        }
     })
  }
  
  def openAny() = rfid.openAny()
  
  def waitForAttachement()
  {
      println("waiting for RFID attachment...")
      rfid.waitForAttachment(1000)
  }

  /**
   * Change l'action à effectuer (entrée dans le parking)
   */
  def in() {
    action = "in"
  }

  /**
   * Change l'action à effectuer (sortie du parkingà
   */
  def out() {
    action = "out"
  }

  /**
   * Change l'état de l'output 0 à true (représenté par un led allumé rouge)
   */
  def ledRedOn() = rfid.setOutputState(0, true)

  /**
   * Change l'état de l'output 0 à false (représenté par un led éteint)
   */
  def ledRedOff() = rfid.setOutputState(0, false)

  /**
   * Change l'état de l'output 1 à true (représenté par un led allumé vert)
   */
  def ledGreenOn() = rfid.setOutputState(1, true)

  /**
   * Change l'état de l'output 1 à false (représenté par un led éteint)
   */
  def ledGreenOff() = rfid.setOutputState(1, false)

  /**
   * Appel à l'interface kit permettant d'attendre qu'une voiture passe devant les capteurs de distances, laissant la barrière ouverte.
   * @return true si la voiture a pu passer, false sinon.
   */
  def carPassed () : Boolean =
  {
    if (InterfaceKit.isAttached)
    {
      val interfaceKitWaitCar = new InterfaceKitWaitCar()
      interfaceKitWaitCar.waitForCarToPassBarrier()
    }
    else
    {
      println("You must attach the interfaceKit")
      false
    }
  }

  /**
   * @return un tag généré par concaténation d'un UUID aléatoire et de l'instant de génération.
   */
  def genTag(): String = {
    val uuid = UUID.randomUUID()
    val time = System.currentTimeMillis() / 1000
    time.toString ++ uuid.toString.replace("-", "").substring(9, 23)
  }

  /**
   * Enregistre un utilisateur en appellant la couche Data
   * @param tag : le tag généré pour l'utilisateur à inscrire
   * @param userLastname : le nom de l'utilisateur à inscrire
   * @param userFirstName : le prénom de l'utilisateur à inscrire
   * @param userMail : l'adresse mail de l'utilisateur à inscrire
   * @return "ok" si l'utilisateur a pu être enregistré avec ses informations,
   *         "AlreadyRegistred" si l'utilisateur n'a pu pas être enregistré parce qu'il a utilisé une adresse e-mail déjà enregistré,
   *         ou une exception dans les autres cas, l'utilisateur n'étant pas enregistré.
   */
  def register(tag: String, userLastname: String, userFirstName: String, userMail: String) =
  {
    DataAdd.register(tag, userLastname, userFirstName, userMail)
  }

  /**
   * Inscription de l'utilisateur via le webservice et d'un identifiant sur son tag RFID
   * @param person l'utilisateur voulant s'inscrire
   * @return "ok" si l'utilisateur a été correctement inscrit, un message d'erreur adapté sinon.
   */
  def inscriptionTag(person:Person) : String =
  {
    val tag = genTag()

    try {
      rfid.write(tag, RFIDPhidget.PHIDGET_RFID_PROTOCOL_PHIDGETS, false) //écrit sur le tag
      register(tag, person.lastName, person.firstName, person.mail) match {
        case Success(rep) => {
          println("\nWrite Tag : " + tag )
          if(rep == "\"Ok\"") "ok"
          else "Cet e-mail est déjà utilisé."
        }
        case Failure(exc) => {
          "Erreur réseau"
        }
      }
    } catch {
      case exc : Exception => if(exc.getMessage == "Erreur réseau") exc.getMessage
                              else "Erreur lors de l'écriture du tag."
    }
  }

  /**
   * Met à jour l'utilisateur person tout en regénérant un tag (utilisé si l'user perd son tag)
   * @param person : l'utilisateur désirant récupérer un tag RFID
   * @return "ok" si l'utilisateur a été correctement mis à jour, un message d'erreur adapté sinon.
   */
  def updateUser (person : Person) : String =
  {
    val tag = genTag()

    try {
      rfid.write(tag, RFIDPhidget.PHIDGET_RFID_PROTOCOL_PHIDGETS, false) //écrit sur la tag
      val idUser = DataGet.found(person.mail.replace("@", "-at-").replace(".", "-dot-")).id
      DataAdd.updateUser(idUser, tag, person.lastName, person.firstName, person.mail) match {
        case Success(rep) => {
          println("\nWrite Tag : " + tag )
          if(rep == "\"Ok\"") "ok"
          else "Cet e-mail est déjà utilisé."
        }
        case Failure(exc) => {
          "Erreur réseau."
        }
      }
    } catch {
      case exc : Exception => if(exc.getMessage == "Erreur réseau") exc.getMessage
                              else "Erreur lors de l'écriture du tag."
    }
  }

  /**
   * Met à jour l'utilisateur person sans regénérer un tag (utilisé si l'user désire mettre à jour ses informations)
   * @param person : l'utilisateur désirant modifier ses informations
   * @return "ok" si l'utilisateur a été correctement mis à jour, un message d'erreur adapté sinon.
   */
  def updateUser (tag : String, person : Person) : String =
  {
    try {
      val idUser = DataGet.found(tag).id
      DataAdd.updateUser(idUser, tag, person.lastName, person.firstName, person.mail) match {
        case Success(rep) => {
          if(rep == "\"Ok\"") "ok"
          else "Cet e-mail est déjà utilisé."
        }
        case Failure(exc) => {
          "Erreur réseau."
        }
      }
    } catch {
      case exc : Exception => if(exc.getMessage == "Erreur réseau") exc.getMessage
                              else "Erreur lors de l'écriture du tag."
    }
  }

}