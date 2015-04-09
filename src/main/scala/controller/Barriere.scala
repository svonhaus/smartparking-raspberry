/**
 * @author Jason
 */
package controller

import com.phidgets._
import com.phidgets.event._

class Barriere 
{
  var servo:AdvancedServoPhidget=null; //test
  val open:Boolean=false;
  val close:Boolean=false;
  
  def Barriere() = {
    servo= new AdvancedServoPhidget();
    /**
     * Régler les paramètres du servo motor
     * Set Min Position = 0; Set Max Position=100
     */
    servo.addAttachListener(new AttachListener() {
      def attached(ae:AttachEvent ) = {
        println("attachment of " + ae)
      }
    });
    servo.addDetachListener(new DetachListener() {
      def  detached(ae:DetachEvent) {
        println("detachment of " + ae)
        try {
          servo.setPosition(0, 112.5)
        } catch {
          case e : PhidgetException => println("Problème quand on le branche");
        }
      }
    });
    servo.addErrorListener(new ErrorListener() {
      def  error(ee:ErrorEvent ) {
        println("error event for " + ee);
      }
    });
    servo.addServoPositionChangeListener(new ServoPositionChangeListener() {
      def servoPositionChanged(oe:ServoPositionChangeEvent) {
        println(oe);
      }
    });
    config();
  }
	
  def config() {
    println("Configuration du servo motor");
    println("waiting for AdvancedServo attachment...");
    servo.openAny();
    servo.waitForAttachment();
    servo.setPosition(0, 112.5);
    servo.setEngaged(0, true);
  }
  
  def ouverture():Unit ={
		println("Ouverture barrière...");
	  servo.setPosition(0, 0.0);
		println("Barrière ouverte");
	}	
	
	def  fermeture() = {
		println("Fermeture barrière...");
	  servo.setPosition(0, 112.5);	
		println("Barrière fermée");
	}
	
	def  close_system() = {
		servo.setEngaged(0, false);
	}
}


