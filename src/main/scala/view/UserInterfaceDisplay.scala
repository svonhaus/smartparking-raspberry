package view

import controller._
import data.DataGet
import model._
import viewJava._
import javax.swing._

import scala.util.{Failure, Success}

class UserInterfaceDisplay extends AbstractDisplay
{
  val regexpMail = """^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$"""

  val panelWelcome = new PanelWelcome
  {
    override def actionScalaForm()
    {
      if (_comboBox.getSelectedItem == "Inscription")
      {
        RFID.action = "write"
        switchToWriteMode()
      }
      else if (_comboBox.getSelectedItem == "Scan")
      {
        RFID.action = "no"
        switchToReadMode()
      }
      else
      {
        RFID.action = "update"
        switchToUpdateMode()
      }
    }

    override def actionScalaIn()
    {
      RFID.in()
    }

    override def actionScalaOut()
    {
      RFID.out()
    }

    override def actionScalaOpen()
    {
      barriere.ouverture()
    }

    override def actionScalaClose()
    {
      barriere.fermeture()
    }

    override def actionScalaSearch() {
      val mail = _textFieldUserMail.getText
      if (!mail.matches(regexpMail)) {
        showMessage("Veuillez encoder une adresse e-mail correcte.", "Recherche", "ERROR_MESSAGE")
      } else {
        try {
            val user = DataGet.found(mail.replace("@", "-at-").replace(".", "-dot-"))
            messagePerson(user.lastName, user.firstName, user.mail, user.inTheParking)
        } catch {
          case exc : Exception => {
            messagePerson("", "", mail, false)
            RFID.ledRedOn() //rouge si le tag est présent en BD
            if(exc.getMessage == "Erreur réseau.") {
              showMessage(exc.getMessage, "Recherche", "ERROR_MESSAGE")
            } else {
              showMessage("Ce mail ne correspond à aucun utilisateur", "Recherche", "ERROR_MESSAGE")
            }
            RFID.ledRedOff()
          }
        }
      }
    }

    override def actionScalaWrite()
    {
      verifChamps match
      {
        case None =>
        {
          val person = new Person(_textFieldUserPrenom.getText, _textFieldUserNom.getText, _textFieldUserMail.getText, false)

          val result = RFID.inscriptionTag(person)
          result match {
            case "ok" => {
              RFID.ledGreenOn()
              showMessage("L'utilisateur est bien inscrit", "Inscription", "INFORMATION_MESSAGE")
              RFID.ledGreenOff()
            }
            case _ => {
              RFID.ledRedOn()
              showMessage("L'utilisateur n'a pas été inscrit.\nCause : " + result, "Inscription", "ERROR_MESSAGE")
              RFID.ledRedOff()
            }
          }
        }
        case Some(exc) =>
        {
          showMessage(exc.toString(), "Inscription", "ERROR_MESSAGE")
        }
      }
    }

    override def actionScalaUpdateTag()
    {
      verifChamps match
      {
        case None =>
        {
          val person = new Person(_textFieldUserPrenom.getText, _textFieldUserNom.getText, _textFieldUserMail.getText, false)

          val result = RFID.updateUser(person)
          result match {
            case "ok" => {
              RFID.ledGreenOn()
              showMessage("L'utilisateur a bien été mis à jour.", "MAJ", "INFORMATION_MESSAGE")
              RFID.ledGreenOff()
            }
            case _ => {
              RFID.ledRedOn()
              showMessage("L'utilisateur n'a pas été mis à jour.\nCause : " + result, "MAJ", "ERROR_MESSAGE")
              RFID.ledRedOff()
            }
          }
        }
        case Some(exc) =>
        {
          showMessage(exc.toString(), "MAJ", "ERROR_MESSAGE")
        }
      }
    }

    override def actionScalaUpdate()
    {
      val tag = _textFieldTagLu.getText
      if (tag.isEmpty())
      {
        RFID.ledRedOn()
        showMessage("Veuillez scanner le tag RFID", "MAJ", "ERROR_MESSAGE")
        RFID.ledRedOff()
      }
      else
      {
        verifChamps match
        {
          case None =>
          {
            val person = new Person(_textFieldUserPrenom.getText, _textFieldUserNom.getText, _textFieldUserMail.getText, false)

            val result = RFID.updateUser(tag, person)
            result match {
              case "ok" => {
                RFID.ledGreenOn()
                showMessage("L'utilisateur a bien été mis à jour.", "MAJ", "INFORMATION_MESSAGE")
                RFID.ledGreenOff()
              }
              case _ => {
                RFID.ledRedOn()
                showMessage("L'utilisateur n'a pas été mis à jour.\nCause : " + result, "MAJ", "ERROR_MESSAGE")
                RFID.ledRedOff()
              }
            }
          }
          case Some(exc) =>
          {
            showMessage(exc.toString(), "MAJ", "ERROR_MESSAGE")
          }
        }
      }
    }

    def verifChamps(): Option[String] =
    {
      if (_textFieldUserPrenom.getText.isEmpty() || _textFieldUserNom.getText.isEmpty() || _textFieldUserMail.getText.isEmpty())
        Some("Veuillez remplir les champs.")
      else if (!_textFieldUserMail.getText.matches(regexpMail))
      {
        Some("Veuillez encoder une adresse e-mail correcte.")
      }
      else
      {
        None
      }
    }
  }

  def messageTagLu(tag:String)
  {
    panelWelcome.setTagLu(tag)
  }

  def messagePerson(lastName:String, firstName:String, mail:String, inTheParking : Boolean)
  {
    panelWelcome.updatePersonFields(lastName, firstName, mail, inTheParking)
  }

  def showMessage(message:String, titre:String, typeMessage:String)
  {
    panelWelcome.showOptionPane(message, titre, typeMessage)
  }

  override def initialize()
  {
    RFID.action = "no"
    super.initialize()
    val mainJFrame = new MainJFrame(panelWelcome)
  }

}
