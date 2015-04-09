package data

import java.net.UnknownHostException

import org.json._
import scalaj.http._
import model._

object DataGet 
{
  /**
   * @param tagOrMail : tag ou email de l'utilisateur recherché
   * @return l'utilisateur s'il existe, qu'il n'est pas expiré et que son tag n'a pas été supprimé ; une exception sinon
   */
  def found(tagOrMail : String) : Person =
  {
    val url = "http://smarking.azurewebsites.net/api/users/" + tagOrMail
    try {
      val responseGet = Http.get(url).asString
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
  
  def searchTagUser (tag : String, action : String) : String =
  {
    val responseGet = Http.get("http://smarking.azurewebsites.net/api/Tags/"+ action +"/" + tag).asString
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
  
  def foundAction () : String = 
  {
    val actionStr = Http.get("http://smarking.azurewebsites.net/api/global/rfid").asString
    new JSONObject(actionStr).getString("value")
  }
  
}