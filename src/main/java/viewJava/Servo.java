package viewJava;



/*
 * Copyright 2007 Phidgets Inc.  All rights reserved.
 */

import java.awt.Button;
import java.awt.Frame;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.IOException;

import com.phidgets.*;
import com.phidgets.event.*;


class Servo {
	private static int nombre_servo=0;
	static AdvancedServoPhidget servo;
	private int num_servo;
	private boolean open=false;
	private boolean close=false;
	
	public Servo() throws PhidgetException, IOException, InterruptedException{
		nombre_servo=1;
		this.servo= new AdvancedServoPhidget();
		this.num_servo=nombre_servo-1;
		/**
		 * Régler les paramètres du servo motor
		 * Set Min Position = 0; Set Max Position=100
		 */
		
		this.servo.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("attachment of " + ae);
				//if(this.open==true){this.ouverture();}
				//if(this.close==true){}
				System.out.println("L'erreur est : " + ae.getClass());
				try {
					config();
				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		this.servo.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("detachment of " + ae);
				System.out.println("Veuillez le rebranchez");
				try {
					Servo.servo.setPosition(0, 112.5);
				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Problème quand on le branche");
				}
			}
		});
		this.servo.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("error event for " + ee);
			}
		});
		this.servo.addServoPositionChangeListener(new ServoPositionChangeListener()
		{
			public void servoPositionChanged(ServoPositionChangeEvent oe)
			{
				System.out.println(oe);
			}
		});
		config();
		
	}
	
	
	public void config() throws PhidgetException{
		System.out.println("Confguration du servo motor");
		System.out.println("waiting for AdvancedServo attachment...");
		servo.openAny();
		servo.waitForAttachment();
		//System.out.println("Ligne 70 : "+ servo.getVelocity(0) + " - Position : " + servo.getPosition(0));
		
		servo.openAny();
		servo.setPosition(0, 112.5);
		//System.out.println("Renew 70 : "+ servo.getVelocity(0) + " - Position : " + servo.getPosition(0));

		System.out.println("Serial: " + servo.getSerialNumber());
		System.out.println("Servos: " + servo.getMotorCount());

		//this.speed()
		this.servo.setEngaged(0, true);
		
	}
	
	public void contact() throws PhidgetException, IOException, InterruptedException {
		/*
		 * Copyright 2008 Phidgets Inc.  All rights reserved.
		 */
		AdvancedServoPhidget servo;
        

		System.out.println(Phidget.getLibraryVersion());


		servo = new AdvancedServoPhidget();
		servo.addAttachListener(new AttachListener() {
			public void attached(AttachEvent ae) {
				System.out.println("attachment of " + ae);
			}
		});
		servo.addDetachListener(new DetachListener() {
			public void detached(DetachEvent ae) {
				System.out.println("detachment of " + ae);
			}
		});
		servo.addErrorListener(new ErrorListener() {
			public void error(ErrorEvent ee) {
				System.out.println("error event for " + ee);
			}
		});
		servo.addServoPositionChangeListener(new ServoPositionChangeListener()
		{
			public void servoPositionChanged(ServoPositionChangeEvent oe)
			{
				System.out.println(oe);
			}
		});

		servo.openAny();
		System.out.println("waiting for AdvancedServo attachment...");
		servo.waitForAttachment();

		System.out.println("Serial: " + servo.getSerialNumber());
		System.out.println("Servos: " + servo.getMotorCount());

                //Initialize the Advanced Servo
                servo.setEngaged(0, false);
                servo.setSpeedRampingOn(0, false);
                
                servo.setPosition(0, 50);
                servo.setEngaged(0, true);
                Thread.sleep(500);
                
                System.out.println();
                System.out.println("Start Position: " + servo.getPosition(0));
                
                servo.setSpeedRampingOn(0, true);
                servo.setAcceleration(0,servo.getAccelerationMin(0));
                servo.setVelocityLimit(0, 200);
                servo.setPosition(0, 150);
                
		System.out.println("Outputting events.  Press Enter to stop");
		System.in.read();
                
		System.out.print("closing...");
                System.out.println();
		servo.close();
		servo = null;
		System.out.println(" ok");
	}			
	
	public void ouverture() throws PhidgetException{
	
		System.out.println("Ouverture barrière...");
		this.open=true;
		//for(int i=0; i<101;i++){
	    	this.servo.setPosition(0, 0.0);
	    	//servo.setPosition(0, ((int)servo.getPosition(0))+1);
	    //}
		System.out.println("Barrière ouverte");
		this.open=false;
	}	
	
	public void fermeture() throws PhidgetException{
		
		System.out.println("Fermeture barrière...");
		this.close=true;
		//for(int i=0; i<101;i++){
	    	this.servo.setPosition(0, 112.5);	
	    	//servo.setPosition(0, ((int)servo.getPosition(0))-1);
	    //}
		System.out.println("Barrière fermée");
		this.close=false;
	}
	
	public void close_system() throws PhidgetException{
		
		this.servo.setEngaged(0, false);
		//this.servo.close();
		
	}
}


