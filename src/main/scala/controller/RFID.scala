package controller

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
  var inscription = false

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
        println("Tag Loss : " + oe.getValue());
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

  //update tout sauf le taf
  def updateUser (tag : String, person : Person) = 
  {
    val user = DataGet.found(tag)
    val idUser = user match {
      case Some(person) => person.id
      case None => throw new Exception("Utilisateur non-existant")
    }
    DataAdd.updateUser(idUser, person.lastName, person.firstName, person.mail) match {
      case Success(rep) => {
        true
      }
      case Failure(exc) => {
        false
      }
    }
  }

  //update tout en regénérant un tag (si l'user perd son tag ...)
  def updateUser (person : Person) =
  {
    val tag = genTag()

    try {
      rfid.write(tag, RFIDPhidget.PHIDGET_RFID_PROTOCOL_PHIDGETS, false) //écrit sur la tag

      val user = DataGet.found(tag)
      val idUser = user match {
        case Some(person) => person.id
        case None => throw new Exception("Utilisateur non-existant")
      }
      DataAdd.updateUser(idUser, person.lastName, person.firstName, person.mail) match {
        case Success(rep) => {
          true
        }
        case Failure(exc) => {
          false
        }
      }
    } catch {
      case exc : Exception => false
    }
  }
  
  def register(tag: String, userLastname: String, userFirstName: String, userMail: String) = 
  {
    DataAdd.register(tag, userLastname, userFirstName, userMail)
  }
  
  def inscriptionTag(person:Person): Boolean =
  {
    val tag = genTag()
    
    try {
        rfid.write(tag, RFIDPhidget.PHIDGET_RFID_PROTOCOL_PHIDGETS, false) //écrit sur la tag       
        register(tag, person.lastName, person.firstName, person.mail) match {
          case Success(rep) => {
            println("\nWrite Tag : " + tag)
            true
          }
          case Failure(exc) => {
            false
          }
        }
    } catch {
      case exc : Exception => false
    }
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
        println("You must attach the interfaceKit");
        false
     }
  }

  def genTag(): String = {
    val uuid = UUID.randomUUID();
    val time = System.currentTimeMillis() / 1000;
    time.toString() ++ uuid.toString.replace("-", "").substring(9, 23)
  }

}