package config

import controller.{InterfaceKit, Barriere}
import scala.concurrent.duration._

/**
 * Fichier de configuration de l'application
 */
object Config
{
   val barriere = new Barriere //Initialisation du servoControler
   val IK = new InterfaceKit //Initialisation de l'interfaceKit
   var token = "" //utilisé pour l'authentification au webservice
}