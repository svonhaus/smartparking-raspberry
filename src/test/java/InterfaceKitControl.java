import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.AttachEvent;
import com.phidgets.event.AttachListener;
import com.phidgets.event.DetachEvent;
import com.phidgets.event.DetachListener;
import com.phidgets.event.SensorChangeEvent;
import com.phidgets.event.SensorChangeListener;
import view.UtilConsole;


public class InterfaceKitControl {

    private InterfaceKitPhidget phInter;
    static Thread_sensor ts= new Thread_sensor();

    public InterfaceKitControl() throws PhidgetException{

        this.phInter=new InterfaceKitPhidget();

        ts.start();
        ts.suspend();

        this.phInter.addAttachListener(new AttachListener() {
            public void attached(AttachEvent ae) {
                System.out.println("attachment of " + ae);
                //if(this.open==true){this.ouverture();}
                //if(this.close==true){}


                System.out.println("L'erreur est : " + ae.getClass());

            }
        });

        this.phInter.addDetachListener(new DetachListener() {
            public void detached(DetachEvent ae) {
                System.out.println("detachment of " + ae);
                System.out.println("Veuillez le rebranchez");

            }
        });

        this.phInter.addSensorChangeListener(new SensorChangeListener() {

            @Override
            public void sensorChanged(SensorChangeEvent arg0) {
                // TODO Auto-generated method stub
                //System.out.println("Changement... : "+ arg0.getIndex());
                if (arg0.getIndex() == Config.TEMP_SENSOR) {
                    try {
                        System.out.println("Changement de température");
                        System.out.println(get_temp());
                        send_db_temp();
                        check_problem_temp();
                    } catch (PhidgetException | IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Problème changement de température...");
                        e.printStackTrace();
                    }
                }

                if (arg0.getIndex() == Config.MAGNETIC_SENSOR) {
                    try {
                        System.out.println("Changement de magnetisme");
                        System.out.println(get_magneticSensor());
                        //send_db_temp();
                    } catch (PhidgetException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Problème changement de magnétisme...");
                        e.printStackTrace();
                    }
                }

                if (arg0.getIndex() == Config.TOUCH_SENSOR) {
                    try {
                        System.out.println("Changement de touché");
                        System.out.println(get_touchSensor());
                        send_db_temp();
                        //check_problem_temp();
                    } catch (PhidgetException | IOException e) {
                        // TODO Auto-generated catch block
                        System.out.println("Problème changement de touché...");
                        e.printStackTrace();
                    }
                }
            }


        });

        //temp=new TemperatureContr();
        this.phInter.openAny();
        System.out.println("waiting for PhidgetInterfaceSensor attachment...");
        this.phInter.waitForAttachment();
        this.phInter.setOutputState(7, true);
        this.phInter.setOutputState(6, true);
        this.phInter.setOutputState(5, true);
        this.phInter.setOutputState(4, true);
        //this.phInter.setEngaged(0, true);

    }

    public int get_temp() throws PhidgetException{
        //Attention, on considère que le capteur de température est branché sur l'entrée 2
        return ((int)((this.phInter.getSensorValue(Config.TEMP_SENSOR)*0.2222)-61.111));
    }

    public int get_magneticSensor() throws PhidgetException{
        //Attention, on considère que le capteur magnétique est branché sur l'entrée 2
        System.out.println("Magnetic sensor: "+ this.phInter.getSensorValue(Config.MAGNETIC_SENSOR));
        return (this.phInter.getSensorValue(Config.MAGNETIC_SENSOR));
    }

    public int get_touchSensor() throws PhidgetException{
        //Attention, on considère que le capteur de touché est branché sur l'entrée 2
        System.out.println("Touch sensor: "+ this.phInter.getSensorValue(Config.TOUCH_SENSOR));
        return (this.phInter.getSensorValue(Config.TOUCH_SENSOR));
    }

    public double get_vibration() throws PhidgetException{
        //Attention, on considère que le capteur de température est branché sur l'entrée 2
        return (this.phInter.getSensorValue(Config.VIBRATION_SENSOR));
    }

    public void allumer_led(int num) throws PhidgetException{

        this.phInter.setOutputState(num, true);
    }

    public void eteindre_led(int num) throws PhidgetException{

        this.phInter.setOutputState(num, false);
    }

    public void allumer_lampe(int num) throws PhidgetException{

        this.phInter.setOutputState(num, false);
    }

    public void eteindre_lampe(int num) throws PhidgetException{

        this.phInter.setOutputState(num, true);
    }

    public void check_problem_temp() throws PhidgetException {
        // TODO Auto-generated method stub

        if (this.get_temp()>=30){
            ts.resume();
            //this.phInter.setOutputState(0, true);
        }
        if (this.get_temp()<30){
            ts.suspend();
            this.eteindre_led(Config.LED_TEMP_PROBLEM);
            //this.phInter.setOutputState(0, true);
        }
    }

    public void faire_clignoter(int i) throws PhidgetException, InterruptedException
    {
        while(true)
        {
            UtilConsole.showMessage("Clignotement car trop chaud...", getClass().getName(), "INFORMATION_MESSAGE");
            System.out.println("");
            this.allumer_led(i);
            //LecteurTexte.lt = new LecteurTexte("Attention! Attention! La température du parking est trop élevée");
            Thread.sleep(500);
            this.eteindre_led(i);
            Thread.sleep(500);
        }
    }

    public void go_to_floor(int num) throws PhidgetException, InterruptedException{
        //Manipule les LED pour guider les utilisateurs au bon étage (lampe étage num)

        for(int i=0; i<5; i++){
            System.out.println(i);
            this.allumer_lampe(num);
            Thread.sleep(500);
            this.eteindre_lampe(num);
            Thread.sleep(500);
        }
    }





    public void send_db_temp() throws IOException, PhidgetException{
	/*	
		Renvoit la température à la BD
		      
		      
	*/
        Random gen = new Random();
        String url = "http://smarking.azurewebsites.net/api/Parking?temperature="+this.get_temp();
        //String url = "http://smarking.azurewebsites.net/api/Parking?temperature="+gen.nextInt(65);
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("PUT");
        con.setRequestProperty("User-Agent", "");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type","application/xml;charset=UTF-8");
        con.setRequestProperty("Content-Length", "500");
        String urlParameters = "";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        //wr.Content-Length:0

        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'PUT' request to URL : " + url);
        //System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());
    }
}
