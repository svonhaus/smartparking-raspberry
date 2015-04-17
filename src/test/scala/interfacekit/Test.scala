package interfacekit

import controller._
import model.Person
import org.json.JSONObject

import scalaj.http.Http

/**
 * Batterie de test de l'application
 */
object Test 
{
  def main(args: Array[String]) 
  {
    test1
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
    observableWaitCar.waitForCarToPassBarrier();
    
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
      val person = new Person(temp.get("id").toString, temp.get("firstname").toString, temp.get("lastname").toString, temp.get("mail").toString, temp.get("inTheParking").toString.toBoolean)

      println(person.firstName)
    }

    println("shit happened")
  }

  def test5()
  {
    InterfaceKit.waitForAttachment

    println("début test SENSOR")

    val oneSensor = new InterfaceKitOneSensor(4)
    oneSensor.startSensor(100)
    readLine()
    oneSensor.stopSensor()
    InterfaceKit.close
    println("fin test")
  }

  def testQRCode(): Unit = {
    val idGen = TouchSensor.genIdTmp()
    TouchSensor.genQRCode(idGen) match {
      case true => println("qr code généré")
      case false => println("problème")
    }
  }

  def testSensorListener(): Unit = {
    println("debut test")
    InterfaceKit.addSensorChangeListener
    readLine()
    InterfaceKit.close
    println("fin test")
  }
}