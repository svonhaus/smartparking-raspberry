package data

import config.Config
import org.json.JSONObject

import scalaj.http._
import scala.util._

/**
 * Objet permettant d'envoyer des données sur le webservice.
 */
object DataAdd
{
  //définition générique d'une requête post avec scalaj
  def postHttp (path : String) = Http.post(Config.apiUrl + path).option(HttpOptions.connTimeout(5000)).option(HttpOptions.readTimeout(10000)).header("Authorization", "Bearer "+Config.token)
  //définition générique d'une requête put avec scalaj
  def putHttp (path : String, json : String) = Http.postData(Config.apiUrl + path, json).header("Authorization", "Bearer "+Config.token).method("put").header("Content-Type", "application/json").asString

  /** Permet une authentification sur le webservice afin de faire les requêtes privée par-après
    * @return un objet json contenant le token
    */
  def auth ()  = {
    Try(Http.post("http://smartking.azurewebsites.net/Token").option(HttpOptions.connTimeout(1000)).option(HttpOptions.readTimeout(10000)).params(Map(("grant_type", "password"), ("username", "laurent@phidgets.com"), ("password", "Password1!"))).asString)
  }

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
    Try(postHttp("Account/Register").params(Map(("TagId", tag), ("lastname", userLastname), ("firstname", userFirstName), ("Email", userMail))).asString)
  }

  /**
   *
   * @param idGen : id généré pour les clients temporaires du parking à l'entrée
   * @return "ok" si l'utilisateur temporaire a pu être enregistré et peut passer
   *         "AlreadyRegistred" si l'id a déjà été mis en bd
   *         ou une exception dans les autres cas, l'utilisateur temporaire n'étant pas enregistré.
   */
  def registerTmp (idGen : String) = {
    Try(postHttp("Ticket").param("id", idGen).asString)
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
    Try(putHttp("users", json))
  }

  /**
   * Enregistre l'action effectuée sur le webservice.
   * @param idTag : le tag de l'utilisateur étant passé à l'intérieur ou à l'extérieur du parking
   * @param action : action à faire, "in" : entrer dans le parking ou "out" : sortir du parking
   */
  def addFlowParking (idTag : String, action : String) =
  {
    Try(postHttp("FlowUsers").params("action" -> action).params("idTag" -> idTag).asString)
  }

  /**
   * Enregistre la sortie effectuée sur le webservice.
   * @param id : l'id qrcode de l'utilisateur étant passé à l'extérieur du parking
   * @return     "ok" si l'utilisateur temporaire peut sortir du parking, cad s'il a payé
   *             "NotFound" si l'utilisateur temporaire n'existe pas (% à son id)
   *             "NotPaid" si l'utilisateur n'a pas payé
   *             ou "AlreadyOut" si l'utilisateur temporaire est déjà sorti.
   */
  def addFlowParkingTmp (id : String) =
  {
    val json = new JSONObject().put("id", id).toString()
    Try(putHttp("Ticket", json))
}

  /**
  * Mise à jour via le webservice de la température du parking.
  * @param temp : température du parking ayant changée
  */
  def updateTemp(temp : Double) =
  {
    val json = new JSONObject().toString()
    Try(putHttp("Parking?temperature="+temp, json))
  }

  /**
  * Mise à jour via le webservice de la place de parking.
  * @param num_place : température du parking ayant changée
  * @param taken : true si la place est prise, false sinon
  */
  def updateParkingSpace(num_place : Int, taken : Boolean) =
  {
  /*val json = new JSONObject().put("space", temp).toString()
  Try(Http.postData(apiUrl + "Parking", json).method("put").header("Content-Type", "application/json").asString)*/
  }

  /**
  * Mise à jour via le webservice de la détection de tremblement dans parking.
  * @param vibration : vibration du parking ayant changée et pouvant causer un tremblement de terre
  */
  def updateVibration(vibration : Double) =
  {
  /*val json = new JSONObject().put("vibration", temp).toString()
  Try(Http.postData(apiUrl + "Parking", json).method("put").header("Content-Type", "application/json").asString)*/
  }

}