package config

import controller.{InterfaceKit, Barriere}
import scala.concurrent.duration._

/**
 * Fichier de configuration de l'application
 */
object Config {
   val context = 0 //au lancement, crée une interface graphique (0) ou non (1)

   val barriere = new Barriere //Initialisation du servoControler
   val IK = new InterfaceKit //Initialisation de l'interfaceKit
   //url de base de webservice azure
   val apiUrl = "http://smartking.azurewebsites.net/api/"
   //utilisé pour l'authentification au webservice
   var token = ""

   /*
     Emplacement des LEDs
    */
   val LED_TEMP_PROBLEM: Int = 0
   val LED_VIBRATION_PROBLEM: Int = 1
   val LED_PLACE:Int = 2
   val LED_IN_GREEN:Int = 3
   val LED_IN_RED:Int = 4
   val LED_OUT_GREEN:Int = 3
   val LED_OUT_RED:Int = 4
   val LED_F1: Int = 5
   val LED_F2: Int = 7

   /*
      Emplacement des capteurs
    */
   val SHARP_SENSOR_1:Int = 0
   val SHARP_SENSOR_2:Int = 1
   val TEMP_SENSOR: Int = 2
   val MAGNETIC_SENSOR: Int = 3
   val TOUCH_SENSOR: Int = 4
   val VIBRATION_SENSOR: Int = 5
   val IR_SENSOR: Int = 6

   val PLACE_NUM = 1 //numéro de la place sur laquelle est placé le capteur magnétique
   val INTERVAL_CHECK_TEMP = 5 seconds
   val INTERVAL_CHECK_VIBR = 5 seconds
   val INTERVAL_CHECK_PLACES = 5 seconds
   val MAX_TEMP = 27.0
   val MAX_VIBR = 900.0
}