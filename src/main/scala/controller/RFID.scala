package controller

import java.net.UnknownHostException
import java.util.UUID

import com.phidgets._
import com.phidgets.event._
import data._
import model._

import scala.util._

/**
 * @author laurent
 */
object RFID 
{
  val rfid: RFIDPhidget = new RFIDPhidget()
  addAttachListener()
  tagLossListener()
  addOutputChangeListener()
  openAny()
  waitForAttachement()
  
  var action = "no"

  def addAttachListener()
  {
    rfid.addAttachListener(new AttachListener() {
      def attached(ae: AttachEvent) {
        try {
          (ae.getSource() match {
            case aeRFID: RFIDPhidget => aeRFID
          }).setAntennaOn(true)
  
          (ae.getSource() match {
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
        println(oe.getIndex + " change to " + oe.getState);
        }
     })
  }
  
  def openAny() = rfid.openAny()
  
  def waitForAttachement()
  {
      println("waiting for RFID attachment...")
      rfid.waitForAttachment(1000)
  }

  def in() {
    action = "in"
  }

  def out() {
    action = "out"
  }

  def ledRedOn() = rfid.setOutputState(0, true)
  def ledRedOff() = rfid.setOutputState(0, false)

  def ledGreenOn() = rfid.setOutputState(1, true)
  def ledGreenOff() = rfid.setOutputState(1, false)

  def carPassed (tag : String) : Boolean =
  {
    if (InterfaceKit.isAttached)
    {
      val interfaceKitWaitCar = new InterfaceKitWaitCar()
      interfaceKitWaitCar.waitForCarToPassBarrier(tag)  // PUT RIFD HERE
    }
    else
    {
      println("You must attach the interfaceKit")
      false
    }
  }

  def genTag(): String = {
    val uuid = UUID.randomUUID()
    val time = System.currentTimeMillis() / 1000
    time.toString() ++ uuid.toString.replace("-", "").substring(9, 23)
  }

  def register(tag: String, userLastname: String, userFirstName: String, userMail: String) =
  {
    DataAdd.register(tag, userLastname, userFirstName, userMail)
  }

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

  //update tout en regénérant un tag (si l'user perd son tag (récupération par mail)...)
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

  //update tout sauf le tag
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