package view

import com.phidgets.event._
import controller.{RFID, Barriere, InterfaceKit}
import data.{DataAdd, DataGet}

import scala.util.{Failure, Success}

abstract class AbstractDisplay
{
  val barriere = new Barriere()

  def messageTagLu(tag:String)
  def messagePerson(lastName:String, firstName:String, mail:String, inTheParking : Boolean)
  def showMessage(message:String, titre:String, typeMessage:String)

  def initialize()
  {
    InterfaceKit.openAny
    barriere.Barriere()

    RFID.rfid.addTagGainListener(new TagGainListener()
    {
      def tagGained(oe: TagGainEvent)
      {
        val tag = oe.getValue

        if (RFID.action != "write" && RFID.action != "update")
        {
          messageTagLu(tag)
          try {
              val user = DataGet.found(tag)
              messagePerson(user.lastName, user.firstName, user.mail, user.inTheParking)

              if (RFID.action == "in" || RFID.action == "out")
              {
                val result = DataGet.searchTagUser(tag, RFID.action)
                result match
                {
                  case "ok" =>
                  {
                    RFID.ledGreenOn()
                    barriere.ouverture
                    showMessage("L'utilisateur peut passer, faites entrer la voiture.", "Passage", "INFORMATION_MESSAGE")
                    RFID.carPassed(tag) match
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
                  case _ =>
                  {
                    RFID.ledRedOn() //rouge si le tag est présent en BD
                    showMessage("L'utilisateur ne peut pas passer. \nCause : " + result, "Passage", "ERROR_MESSAGE")
                    RFID.ledRedOff()
                  }
                }
              }
          } catch {
            case exc : Exception => {
              messagePerson("", "", "", false)
              RFID.ledRedOn() //rouge si le tag est présent en BD
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
