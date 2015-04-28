package controller

import java.io.File
import java.util.UUID
import javax.imageio.ImageIO

import Actor.ActorManager
import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.phidgets.InterfaceKitPhidget
import config.Config
import data.{DataAdd, DataGet}
import view.UtilConsole

import scala.util.{Failure, Success}

/**
 * Controller pour la détection de touché permettant de gérer les activités à la suite du touché d'un utilisateur
      //TODO : requete get pour savoir s'il peut passer + led
      //TODO : génération d'un qrcode et d'un id sur celui ci
      //TODO : envoi d'une requete post au ws pour enregistrer ce client temporaire
      //TODO : impression du ticket avec qrcode
      //TODO : le client peut passer (capteur dist + barrière)
      //TODO : si bien passé, requete ws pour le faire passer
 */
object TouchSensor {

  val barriere = Config.barriere
  val IK = Config.IK

  /**
   * @return un tag généré par concaténation d'un UUID aléatoire et de l'instant de génération.
   */
  def genIdTmp(): String = {
    val uuid = UUID.randomUUID()
    val time = System.currentTimeMillis() / 1000
    time.toString ++ uuid.toString.replace("-", "").substring(9, 23)
  }

  /**
   * @param idGen : id généré à inclure dans le qrcode
   * @return
   */
  def genQRCode(idGen : String) : Boolean = {
    val QRCODE_IMAGE_HEIGHT = 250
    val QRCODE_IMAGE_WIDTH = 250

    val qrWriter = new QRCodeWriter()
    val matrix = qrWriter.encode(idGen,
      BarcodeFormat.QR_CODE,
      QRCODE_IMAGE_WIDTH,
      QRCODE_IMAGE_HEIGHT)

    val image = MatrixToImageWriter.toBufferedImage(matrix)
    val imageFile = new File("qrcode.png")
    ImageIO.write(image, "PNG", imageFile)
  }

  /**
   * Traitement pour la création d'un utilisateur temporaire et passage de voiture
   */
  def touchControl(): Unit =
  {
    UtilConsole.showMessage("Génération du qrcode et impression du ticket", getClass.getName, "INFORMATION_MESSAGE")

    val idGen = genIdTmp()
    genQRCode(idGen)

    DataAdd.registerTmp(idGen) match
    {
      case Success(rep) =>
      {
        UtilConsole.showMessage(rep, getClass.getName, "INFORMATION_MESSAGE")

        if (rep == "\"Ok\"")
        {
          UtilConsole.showMessage("Guest peut passer, faites entrer la voiture.", getClass.getName, "INFORMATION_MESSAGE")
          ActorManager.waitCarToPassActor ! RFID.action
        }
        else "Le parking n'est pas accessible aux utilisateurs temporaires pour l'instant"
      }
      case Failure(exc) => UtilConsole.showMessage(exc.getMessage, getClass.getName, "ERROR_MESSAGE")
    }
  }
}
