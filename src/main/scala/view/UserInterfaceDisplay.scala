package view

import controller._
import data.{DataAdd, DataGet}
import model._
import viewJava._
import javax.swing._

import scala.util.{Failure, Success}

/**
 * Démarrage de l'application sur une interface graphique en fenêtre.
 * Les actions possibles sont :
 *  no (par défaut / scan du tag et affichage des informations en fenêtre)
 *  in (entrée dans le parking)
 *  out (sortie du parking)
 *  write (inscription d'un tag correspondant à un utilisateur avec ses informations)
 *  update (mise à jour d'un utilisateur et de ses informations ou de son tag)
 */
class UserInterfaceDisplay extends AbstractDisplay
{
  //expression régulière d'un e-mail
  val regexpMail = """^[_A-Za-z0-9-+]+(.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(.[A-Za-z0-9]+)*(.[A-Za-z]{2,})$"""

  /**
   *  appel à une couche d'interface graphique
   */
  val panelWelcome = new PanelWelcome
  {
    override def actionScalaForm() //Choix de l'action à effectuer
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

    /**
     * change l'action en "in"
     */
    override def actionScalaIn()
    {
      RFID.in()
    }

    /**
     * change l'action en "out"
     */
    override def actionScalaOut()
    {
      RFID.out()
    }

    /**
     * Ouvre la barrière (par action sur le servoMotor)
     */
    override def actionScalaOpen()
    {
      barriere.ouverture()
    }

    /**
     * Ferme la barrière (par action sur le servoMotor)
     */
    override def actionScalaClose()
    {
      barriere.fermeture()
    }

    /**
     * Permet de rechercher un utilisateur et ses informations avec son adresse e-mail
     */
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
            RFID.ledRedOn()
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

    /**
     * Permet d'inscrire un utilisateur avec ses informations et inscrire un identifiant sur son tag RFID
     */
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

    /**
     * Permet de mettre à jour le tag de l'utilisateur (et ses informations) associé à l'adresse e-mail inscrite.
     */
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

    /**
     * Permet de mettre à jour les informations de l'utilisateur associé à son tag lu.
     */
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

    /**
     * Vérifie la valeur des champs de l'interface graphique.
     * @return Un message d'erreur si il y a une, rien sinon.
     */
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

  /**
   * Appel à une couche d'interface graphique en plus de la méthode héritée
   */
  override def initialize()
  {
    RFID.action = "no"
    super.initialize()
    val mainJFrame = new MainJFrame(panelWelcome)
  }

  /**
   * Pas d'action car on la choisit sur l'interface graphique
   */
  override def choisirAction() = {}

}
