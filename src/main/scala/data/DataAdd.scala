package data

import org.json.JSONObject

import scalaj.http._
import scala.util._

object DataAdd 
{
  def register (tag: String, userLastname: String, userFirstName: String, userMail: String) = 
  {
    Try(Http.post("http://smarking.azurewebsites.net/api/users").params(Map(("idTag", tag), ("lastname", userLastname), ("firstname", userFirstName), ("mail", userMail))).asString)
  }
  
  def updateUser (idUser : String, tag : String, userLastname: String, userFirstName: String, userMail: String) =
  {
    val json = new JSONObject().put("id", idUser).put("idTag", tag).put("lastname", userLastname).put("firstname", userFirstName).put("mail", userMail).toString()
    Try(Http.postData("http://smarking.azurewebsites.net/api/users", json).method("put").header("Content-Type", "application/json").asString)
  }
  
  def updateTagCarNotComeIn(tagRfid:String)
  {
    
  }
  
  def updateTagCarComeIn(tagRfid:String)
  {
    
  }
  
  def addFlowParking(idTag : String, action : String)
  {
    Try(Http.post("http://smarking.azurewebsites.net/api/FlowUsers").params("action" -> action).params("idTag" -> idTag).asString)  
  }
  
  def addLeavingFromParking(tagRfid:String)
  {
    
  }
  
  def addTemperatureInWebservice()
  {
    //val request = Http.post("http://smarking.azurewebsites.net/api/users").
  }
  
}