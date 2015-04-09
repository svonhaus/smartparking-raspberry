package view

import com.phidgets.event._
import controller.{RFID, Barriere, InterfaceKit}
import data.{DataAdd, DataGet}

abstract class AbstractDisplay
{
  val barriere = new Barriere()

  def messageTagLu(tag:String)
  def messagePerson(lastName:String, firstName:String, user:String)
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
        println("\nTag Gained: " + tag)

        if (RFID.action != "write" && RFID.action != "update")
        {
          messageTagLu(tag)
          val userOption = DataGet.found(tag)

          userOption match
          {
            case Some(user) =>
            {
              messagePerson(user.lastName, user.firstName, user.mail)

              if (RFID.action == "in" || RFID.action == "out")
              {
                DataGet.searchTagUser(tag, RFID.action) match
                {
                  case true =>
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
                  case false =>
                  {
                    RFID.ledRedOn() //rouge si le tag est présent en BD
                    showMessage("L'utilisateur ne peut pas passer", "Passage", "ERROR_MESSAGE")
                    RFID.ledRedOff()
                  }
                }
              }

            }
            case None =>
            {
              messagePerson("", "", "")

              RFID.ledRedOn() //rouge si le tag est présent en BD
              showMessage("L'utilisateur n'existe pas", "Passage", "ERROR_MESSAGE")
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
