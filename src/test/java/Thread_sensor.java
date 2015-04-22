import com.phidgets.PhidgetException;


public class Thread_sensor extends Thread{

    private String str;

    public Thread_sensor() {}

    public Thread_sensor(String str) {
        this.str = str;
    }
    public void run() {
        // faire quelque chose avec str

        try {
            main2.ph.faire_clignoter(Config.LED_TEMP_PROBLEM);
        } catch (PhidgetException | InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.out.println("Problème Thread");
        }

    }
}
