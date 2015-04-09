package view

import controller._
import model._
import viewJava._
import javax.swing._

class UserInterfaceDisplay extends AbstractDisplay
{
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

    override def actionScalaSearch()
    {
      println("ok")
    }

    override def actionScalaUpdateTag()
    {
      verifChamps match
      {
        case None =>
        {
          val person = new Person(_textFieldUserPrenom.getText, _textFieldUserNom.getText, _textFieldUserMail.getText)

          if (RFID.updateUser(person))
          {
            RFID.ledGreenOn()
            JOptionPane.showMessageDialog(null, "L'utilisateur a bien été mis à jour", "MAJ", JOptionPane.INFORMATION_MESSAGE);
            RFID.ledGreenOff()
          }
          else
          {
            RFID.ledRedOn()
            JOptionPane.showMessageDialog(null, "L'utilisateur n'a pas été mis à jour", "MAJ", JOptionPane.ERROR_MESSAGE);
            RFID.ledRedOff()
          }
        }
        case Some(exc) =>
        {
          JOptionPane.showMessageDialog(null, exc.toString(), "MAJ", JOptionPane.ERROR_MESSAGE);
        }
      }
    }

    override def actionScalaWrite()
    {
      verifChamps match
      {
        case None =>
        {
          val person = new Person(_textFieldUserPrenom.getText, _textFieldUserNom.getText, _textFieldUserMail.getText)

          if (RFID.inscriptionTag(person))
          {
            RFID.ledGreenOn()
            JOptionPane.showMessageDialog(null, "L'utilisateur est bien inscrit", "Inscription", JOptionPane.INFORMATION_MESSAGE);
            RFID.ledGreenOff()
          }
          else
          {
            RFID.ledRedOn()
            JOptionPane.showMessageDialog(null, "L'utilisateur n'a pas été inscrit", "Inscription", JOptionPane.ERROR_MESSAGE);
            RFID.ledRedOff()
          }
        }
        case Some(exc) =>
        {
          JOptionPane.showMessageDialog(null, exc.toString(), "Inscription", JOptionPane.ERROR_MESSAGE);
        }
      }
    }

    override def actionScalaUpdate()
    {
      val tag = _textFieldTagLu.getText
      if (tag.isEmpty())
      {
        RFID.ledRedOn()
        JOptionPane.showMessageDialog(null, "Veuillez scanner le tag RFID", "Inscription", JOptionPane.ERROR_MESSAGE);
        RFID.ledRedOff()
      }
      else
      {
        verifChamps match
        {
          case None =>
          {
            val person = new Person(_textFieldUserPrenom.getText, _textFieldUserNom.getText, _textFieldUserMail.getText)

            if (RFID.updateUser(tag, person))
            {
              RFID.ledGreenOn()
              JOptionPane.showMessageDialog(null, "L'utilisateur a bien été mis à jour", "MAJ", JOptionPane.INFORMATION_MESSAGE);
              RFID.ledGreenOff()
            }
            else
            {
              RFID.ledRedOn()
              JOptionPane.showMessageDialog(null, "L'utilisateur n'a pas été mis à jour", "MAJ", JOptionPane.ERROR_MESSAGE);
              RFID.ledRedOff()
            }
          }
          case Some(exc) =>
          {
            JOptionPane.showMessageDialog(null, exc.toString(), "MAJ", JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    }

    def verifChamps(): Option[String] =
    {
      if (_textFieldUserPrenom.getText.isEmpty() || _textFieldUserNom.getText.isEmpty() || _textFieldUserMail.getText.isEmpty())
        Some("Veuillez remplir les champs.")
      else if (!_textFieldUserMail.getText.matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"))
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

  def messagePerson(lastName:String, firstName:String, mail:String)
  {
    panelWelcome.updatePersonFields(lastName, firstName, mail)
  }

  def showMessage(message:String, titre:String, typeMessage:String)
  {
    panelWelcome.showOptionPane(message, titre, typeMessage)
  }

  override def initialize()
  {
    super.initialize()
    val mainJFrame = new MainJFrame(panelWelcome)
  }
}
