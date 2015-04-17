package data

import java.net.UnknownHostException

import config.Config
import org.json._
import scalaj.http._
import model._

/**
 * Objet fournissant des méthodes pour récupérer des données du webservice.
 */
object DataGet 
{
  val apiUrl = "http://smartking.azurewebsites.net/api/"

  /**
   * @param tagOrMail : tag ou email de l'utilisateur recherché
   * @return l'utilisateur de type Person s'il existe, qu'il n'est pas expiré et que son tag n'a pas été supprimé ; une exception sinon
   */
  def found(tagOrMail : String) : Person =
  {
    val url = apiUrl + "users/" + tagOrMail
    try {
      val responseGet = Http.get(url).header("Authorization", "Bearer "+Config.token).asString
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
    val responseGet = Http.get(apiUrl + "Tags/"+ action +"/" + tag).header("Authorization", "Bearer "+Config.token).asString
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
   * @return l'action qui doit être effectué au moment de la demande.
   */
  def foundAction () : String = 
  {
    val actionStr = Http.get(apiUrl + "global/rfid").header("Authorization", "Bearer "+Config.token).asString
    new JSONObject(actionStr).getString("value")
  }

  /**
   *
   * @return true si un client temporaire peut entrer dans le parking, false sinon
   */
  def tmpIn () : Boolean =
  {
      true
  }

  /**
   *
   * @param id : l'identifiant du ticket qrcode pour un client temporaire
   * @return true si le client temporaire peut sortir du parking, c'est-à-dire si il a payé, false sinon
   */
  def tmpOut(id : String) : Boolean =
  {
      true
  }
}