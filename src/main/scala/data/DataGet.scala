package data

import java.lang.Double
import java.net.UnknownHostException

import config.Config
import org.json._
import scala.util.Try
import scalaj.http._
import model._

/**
 * Objet fournissant des méthodes pour récupérer des données du webservice.
 */
object DataGet 
{
  //définition générique d'une requête get avec scalaj
  def getHttp (path : String) = Http.get(Config.apiUrl + path).option(HttpOptions.connTimeout(5000)).option(HttpOptions.readTimeout(10000)).header("Authorization", "Bearer "+Config.token).asString

  /**
   * @param tagOrMail : tag ou email de l'utilisateur recherché
   * @return l'utilisateur de type Person s'il existe, qu'il n'est pas expiré et que son tag n'a pas été supprimé ; une exception sinon
   */
  def found(tagOrMail : String) : Person =
  {
    try {
      val responseGet = getHttp("users/" + tagOrMail)
      responseGet match {
        case "\"TagNotFound\"" => throw new Exception("Le tag ne correspond à aucun utilisateur.")
        case "\"Expired\"" => throw new Exception("Le tag est expiré.")
        case "\"UserNotFound\"" => throw new Exception("L'utilisateur n'est pas enregistré.")
        case _ => {
          val temp = new JSONObject(responseGet)
          new Person(temp.get("id").toString, temp.get("firstname").toString, temp.get("lastname").toString, temp.get("mail").toString, temp.get("inTheParking").toString.toBoolean)
        }
      }
    } catch {
      case networkException : UnknownHostException => throw new Exception("Erreur réseau.")
      case exc : Exception => throw new Exception(exc.getMessage)
    }
  }

  /**
   * @param tag : le tag RFID lu
   * @param action : l'action à effectuer par-rapport au parking (entrer ou sortir)
   * @return un message d'information ou d'erreur correspondant à la situation et le statut de l'utilisateur correspondant au tag lu et à l'action.
   */
  def searchTagUser (tag : String, action : String) : String =
  {
    val responseGet = getHttp("Tags/"+ action +"/" + tag)
    responseGet match {
      case "\"Ok\"" => "ok"
      case "\"Full\"" => "Le parking est rempli."
      case "\"Closed\"" => "Le parking est fermé."
      case "\"Expired\"" => "Le tag est expiré."
      case "\"NotFound\"" => "Le tag n'existe pas."
      case "\"AccessDenied\"" => "Le tag doit être scanné dans une autre file."
      case _ => "Erreur réseau"
    }
  }

  /**
   * @return la température dans le parking
   */
  def getTemp (): Try[Double] = {
    Try(Double.parseDouble(getHttp("Parking/temperature")))
  }

  /*
  * @return l'indice de vibration dans le parking
  */
  def getVibr (): Try[Double] = { //TODO
    Try(850.0/*Double.parseDouble(getHttp("Parking/vibration"))*/)
  }

  /*
  * @return les places dispos du parking
  */
def getPlaces (): Try[String] = { //TODO
  Try("ok"/*getHttp("Parking/places")*/)
}

  /**
   * @return l'action qui doit être effectué au moment de la demande.
   */
  def foundAction () : String = 
  {
    val actionStr = getHttp("global/rfid")
    new JSONObject(actionStr).getString("value")
  }

}