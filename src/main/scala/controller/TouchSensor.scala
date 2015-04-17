package controller

import java.io.File
import java.util.UUID
import javax.imageio.ImageIO

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import com.phidgets.InterfaceKitPhidget
import config.Config
import data.{DataAdd, DataGet}

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

  val barriere = new Barriere()
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
   *
   * @param interfaceKit
   */
  def touchControl(interfaceKit : InterfaceKitPhidget): Unit = {
      DataGet.tmpIn() match {
        case true => {
          println("génération du qrcode et impression du ticket")
          val idGen = genIdTmp()
          genQRCode(idGen)
          DataAdd.registerTmp(idGen) match {
            case Success(rep) => {
              if (rep == "\"Ok\"") {
                interfaceKit.setOutputState(Config.LED_IN_GREEN, true)
                barriere.ouverture
                println("L'utilisateur peut passer, faites entrer la voiture.")
                RFID.carPassed() match /* Si détection que la voiture est bien passée, on enregistre l'action, sinon on affiche une erreur. */ {
                  case true => {
                    DataAdd.addFlowParkingTmp(idGen, "in")
                    println("La voiture est bien passée.")
                  }
                  case false => println("La voiture n'est pas passée")
                }
                barriere.fermeture
                interfaceKit.setOutputState(Config.LED_IN_GREEN, false)
              }
              else "Erreur"
            }
            case Failure(exc) => {
              "Erreur réseau"
            }
          }
        }
        case false => {
          println ("Le parking n'est pas accessible aux utilisateurs temporaires pour l'instant")
          interfaceKit.setOutputState(Config.LED_IN_RED, true)
          Thread.sleep(500)
          interfaceKit.setOutputState(Config.LED_IN_RED, false)
        }
      }
  }

}
