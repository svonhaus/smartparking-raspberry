package config

import java.io.{FileInputStream}
import java.util.Properties
import scala.concurrent.duration._

/**
 * Created by Steven on 26-04-15.
 */
object MyProperties
{
  private val prop = new Properties();
  private val inputStream = new FileInputStream(System.getProperty("user.dir") + "/src/main/resources/config.properties");

  // load a properties file
  prop.load(inputStream)

  /*
   Application context
   */
  val CONTEXT = prop.getProperty("CONTEXT").toInt //au lancement, crée une interface graphique (0) ou non (1)
  val API_URL = prop.getProperty("API_URL")
  val PHIDGET_SERVER = prop.getProperty("PHIDGET_SERVER")
  val PORT_PHIGET_SERVER = prop.getProperty("PORT_PHIGET_SERVER").toInt
  val IK_SERIAL_NUMBER = prop.getProperty("IK_SERIAL_NUMBER").toInt
  val RFID_SERIAL_NUMBER = prop.getProperty("RFID_SERIAL_NUMBER").toInt
  /*
    Emplacement des LEDs
   */
  val LED_TEMP_PROBLEM: Int = prop.getProperty("LED_TEMP_PROBLEM").toInt
  val LED_VIBRATION_PROBLEM: Int = prop.getProperty("LED_VIBRATION_PROBLEM").toInt
  val LED_PLACE:Int = prop.getProperty("LED_PLACE").toInt
  val LED_IN_GREEN:Int = prop.getProperty("LED_IN_GREEN").toInt
  val LED_IN_RED:Int = prop.getProperty("LED_IN_RED").toInt
  val LED_OUT_GREEN:Int = prop.getProperty("LED_OUT_GREEN").toInt
  val LED_OUT_RED:Int = prop.getProperty("LED_OUT_RED").toInt
  val LED_F1: Int = prop.getProperty("LED_F1").toInt
  val LED_F2: Int = prop.getProperty("LED_F2").toInt

  /*
     Emplacement des capteurs
   */
  val SHARP_SENSOR_1:Int = prop.getProperty("SHARP_SENSOR_1").toInt
  val SHARP_SENSOR_2:Int = prop.getProperty("SHARP_SENSOR_2").toInt
  val TEMP_SENSOR: Int = prop.getProperty("TEMP_SENSOR").toInt
  val MAGNETIC_SENSOR: Int = prop.getProperty("MAGNETIC_SENSOR").toInt
  val TOUCH_SENSOR: Int = prop.getProperty("TOUCH_SENSOR").toInt
  val VIBRATION_SENSOR: Int = prop.getProperty("VIBRATION_SENSOR").toInt
  val IR_SENSOR: Int = prop.getProperty("IR_SENSOR").toInt

  val PLACE_NUM = prop.getProperty("PLACE_NUM").toInt //numéro de la place sur laquelle est placé le capteur magnétique
  val INTERVAL_CHECK_TEMP = prop.getProperty("INTERVAL_CHECK_TEMP").toInt seconds
  val INTERVAL_CHECK_VIBR = prop.getProperty("INTERVAL_CHECK_VIBR").toInt seconds
  val INTERVAL_CHECK_PLACES = prop.getProperty("INTERVAL_CHECK_PLACES").toInt seconds
  val MAX_TEMP = prop.getProperty("MAX_TEMP").toDouble
  val MAX_VIBR = prop.getProperty("MAX_VIBR").toDouble
}
