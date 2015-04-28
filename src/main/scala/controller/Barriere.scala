/**
 * @author Jason
 */
package controller

import com.phidgets._
import com.phidgets.event._
import view.UtilConsole

/**
 * Classe controlleur pour initialiser et effectuer des actions sur le servoMotor (représenté par une barrière dans notre application)
 */
class Barriere 
{
  var servo:AdvancedServoPhidget=null; //test
  val open:Boolean=false;
  val close:Boolean=false;
  
  def Barriere() = {/*
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
          case e : PhidgetException => println("Problème de branchement");
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
    config();*/
  }
	
  def config()
  {
    UtilConsole.showMessage("Configuration du servo motor", getClass.getName, "INFORMATION_MESSAGE")
    UtilConsole.showMessage("Waiting for AdvancedServo attachment.", getClass.getName, "INFORMATION_MESSAGE")
    servo.openAny();
    servo.waitForAttachment();
    servo.setPosition(0, 112.5);
    servo.setEngaged(0, true);
  }

  /**
   * Ouverture de la barrière (rotation du ServoMotor vers la position 0)
   */
  def ouverture():Unit =
  {
    UtilConsole.showMessage("Ouverture barrière...", getClass.getName, "INFORMATION_MESSAGE")
	  //servo.setPosition(0, 0.0);
	}

  /**
   * Fermeture de la barrière (rotation du ServoMotor vers la position 112.5)
   */
	def  fermeture() = {
    UtilConsole.showMessage("Fermeture barrière...", getClass.getName, "INFORMATION_MESSAGE")
	  //servo.setPosition(0, 112.5);
	}
	
	def  close_system() = {
		servo.setEngaged(0, false);
	}
}


