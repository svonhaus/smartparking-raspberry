package view

import com.phidgets.event.{TagGainEvent, TagGainListener}
import controller.RFID
import data.{DataAdd, DataGet}


class ConsoleDisplay {

  RFID.rfid.addTagGainListener(new TagGainListener() {
    def tagGained(oe: TagGainEvent) {
      val tag = oe.getValue
      println("\nTag Gained: " + tag)
      val userOption = DataGet.found(tag)

      userOption match {
        case Some(user) => {
          val action = DataGet.foundAction()
          action match {
            case ("in" | "out") => {
              DataGet.searchTagUser(tag, action) match {
                case true => {
                  RFID.ledGreenOn()
                  barriere.ouverture
                  RFID.carPassed(tag) match {
                    case true => DataAdd.addFlowParking(tag, action)
                    case false => println("Car not passed")
                  }
                  Thread.sleep(1000)
                  barriere.fermeture
                  RFID.ledGreenOff()
                }
                case false => {
                  RFID.ledRedOn() //rouge si le tag est présent en BD
                  Thread.sleep(1000)
                  RFID.ledRedOff()
                }
              }
            }
            case _ => {
              println("Pas la bonne action")
              RFID.ledRedOn()
              Thread.sleep(1000)
              RFID.ledRedOff()
            }
          }
        }
        case None => {
          RFID.ledRedOn() //rouge si le tag est présent en BD
          Thread.sleep(1000)
          RFID.ledRedOff()
        }
      }
    }
  })

}
