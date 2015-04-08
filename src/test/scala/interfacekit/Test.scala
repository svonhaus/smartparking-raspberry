package interfacekit

import interfaceKit.controller._
import model.Person
import org.json.JSONObject

import scalaj.http.Http

object Test 
{
  def main(args: Array[String]) 
  {
    test2()
  }
  
  def test1()
  {
    InterfaceKit.waitForAttachment
    
    println("début test")
    
    val oneSensor = new InterfaceKitOneSensor(0)
    oneSensor.startSensor(100)
    
    readLine()
    oneSensor.stopSensor()
    InterfaceKit.close
    println("fin test")
  }
  
  def test2()
  {
    InterfaceKit.waitForAttachment
    
    println("début test")
    
    val observableWaitCar = new InterfaceKitWaitCar()
    observableWaitCar.waitForCarToPassBarrier("50");
    
    readLine()
    InterfaceKit.close
    println("fin test")
  }

  def test3()
  {
    println("test2")

    val url = "http://smarking.azurewebsites.net/api/users/1427793347204458c9f63a72"
    val responseGet = Http.get(url).asString

    println("test3")

    if (responseGet != "\"TagNotFound\"")
    {
      val temp = new JSONObject(responseGet)
      val person = new Person(temp.get("id").toString, temp.get("firstname").toString, temp.get("lastname").toString, temp.get("mail").toString)

      println(person.firstName)
    }

    println("shit happened")
  }
}