package Config.IK

import config.Config
import controller._
import data.DataAdd
import model.Person
import org.json.JSONObject

import scala.util.Success
import scalaj.http.Http

/**
 * Batterie de test de l'application
 */
object Test 
{
  def main(args: Array[String]) 
  {
    testSensorListener()
  }
  
  def test1()
  {
    Config.IK.waitForAttachment
    
    println("début test")
    
    val oneSensor = new InterfaceKitOneSensor(0)
    oneSensor.startSensor(100)
    
    readLine()
    oneSensor.stopSensor()
    Config.IK.close
    println("fin test")
  }
  
  def test2()
  {
    Config.IK.waitForAttachment
    
    println("début test")
    
    val observableWaitCar = new InterfaceKitWaitCar()
    observableWaitCar.waitForCarToPassBarrier()
    
    readLine()
    Config.IK.close
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
    Config.IK.waitForAttachment

    println("début test SENSOR")

    val oneSensor = new InterfaceKitOneSensor(4)
    oneSensor.startSensor(100)
    readLine()
    oneSensor.stopSensor()
    Config.IK.close
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
    Config.IK.addSensorChangeListener
    readLine()
    Config.IK.close
    println("fin test")
  }

  def testData(): Unit = {
    println("debut test")
    DataAdd.auth() match {
      case Success(rep) => {
        val result = DataAdd.updateTemp(24.0)
        println(result)
        val responseGet = Http.get("http://smartking.azurewebsites.net/api/Parking/temperature").header("Authorization", "Bearer " + Config.token).asString
        println(responseGet)
      }
      case _ =>
    }
    println("fin test")
  }
}