package data

import org.json.JSONObject

import scalaj.http._
import scala.util._

/**
 * Objet permettant d'envoyer des données sur le webservice.
 */
object DataAdd 
{

  val apiUrl = "http://smartking.azurewebsites.net/api/"

  /**
   * Inscris l'utilisateur via le webservice
   * @param tag : le tag généré pour l'utilisateur à inscrire
   * @param userLastname : le nom de l'utilisateur à inscrire
   * @param userFirstName : le prénom de l'utilisateur à inscrire
   * @param userMail : l'adresse e-mail de l'utilisateur à inscrire
   * @return "ok" si l'utilisateur a pu être enregistré avec ses informations,
   *         "AlreadyRegistred" si l'utilisateur n'a pu pas être enregistré parce qu'il a utilisé une adresse e-mail déjà enregistré,
   *         ou une exception dans les autres cas, l'utilisateur n'étant pas enregistré.
   */
  def register (tag: String, userLastname: String, userFirstName: String, userMail: String): Try[String] =
  {
    Try(Http.post(apiUrl + "users").params(Map(("idTag", tag), ("lastname", userLastname), ("firstname", userFirstName), ("mail", userMail))).asString)
  }

  /**
   * Met à jour les informations de l'utilisateur via le webservice ayant pour "id" idUser
   * @param idUser : L'identifiant id de l'utilisateur à mettre à jour via le webservice
   * @param tag : le tag de l'utilisateur
   * @param userLastname : le nom de l'utilisateur
   * @param userFirstName : le prénom de l'utilisateur
   * @param userMail : l'adresse e-mail de l'utilisateur
   * @return "ok" si l'utilisateur a pu être mis à jour avec ses informations,
   *         "AlreadyRegistred" si l'utilisateur n'a pu pas être mis à jour parce qu'il a utilisé une adresse e-mail déjà enregistré qui n'est pas la sienne,
   *         ou une exception dans les autres cas, l'utilisateur n'étant pas mis à jour.
   */
  def updateUser (idUser : String, tag : String, userLastname: String, userFirstName: String, userMail: String) =
  {
    val json = new JSONObject().put("id", idUser).put("idTag", tag).put("lastname", userLastname).put("firstname", userFirstName).put("mail", userMail).toString()
    Try(Http.postData(apiUrl + "users", json).method("put").header("Content-Type", "application/json").asString)
  }
  
  def updateTagCarNotComeIn(tagRfid:String)
  {
    
  }
  
  def updateTagCarComeIn(tagRfid:String)
  {
    
  }

  /**
   * Enregistre l'action effectuée sur le webservice.
   * @param idTag : le tag de l'utilisateur étant passé à l'intérieur ou à l'extérieur du parking
   * @param action : action à faire, "in" : entrer dans le parking ou "out" : sortir du parking
   */
  def addFlowParking(idTag : String, action : String)
  {
    Try(Http.post(apiUrl + "FlowUsers").params("action" -> action).params("idTag" -> idTag).asString)
  }
  
  def addLeavingFromParking(tagRfid:String)
  {
    
  }
  
  def addTemperatureInWebservice()
  {
    //val request = Http.post(apiUrl + "users").
  }
  
}