package data

import org.json._
import scalaj.http._
import model._

object DataGet 
{
  def found(tag : String) : Option[Person] = 
  {

    val url = "http://smarking.azurewebsites.net/api/users/" + tag
    val responseGet = Http.get(url).asString
    if (responseGet != "\"TagNotFound\"" && responseGet != "\"Expired\"" && responseGet != "\"UserNotFound\"")
    {
      val temp = new JSONObject(responseGet)
      Some(new Person(temp.get("id").toString, temp.get("firstname").toString, temp.get("lastname").toString, temp.get("mail").toString))
    } 
    else None
  }
  
  def searchTagUser (tag : String, action : String) : Boolean = 
  {
    val responseGet = Http.get("http://smarking.azurewebsites.net/api/Tags/"+ action +"/" + tag).asString
    
    if (responseGet == "\"Ok\"") {
        true
    }
    else {
        false
    }
  }
  
  def foundAction () : String = 
  {
    val actionStr = Http.get("http://smarking.azurewebsites.net/api/global/rfid").asString
    new JSONObject(actionStr).getString("value")
  }
  
}