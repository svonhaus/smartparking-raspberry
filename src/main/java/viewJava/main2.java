package viewJava;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.phidgets.InterfaceKitPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.*;


public class main2 {
	static InterfaceKitControl ph;
	static Servo obj;
	//static LEDControl led;
	//static TemperatureContr temp;
	static Thread_sensor ts= new Thread_sensor();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ph= new InterfaceKitControl();
			ts.start();
		} catch (PhidgetException e1) {
			// TODO Auto-generated catch block
			System.out.println("Erreur à l'initialistaion");
			e1.printStackTrace();
		}
		//ph.setEngaged(true);
		
		System.out.println("Chargement init...");
		JFrame fr= new JFrame();
		fr.setSize(350, 550);
		JPanel jPanel1 = new JPanel();
		JButton but1 = new JButton("Ouvrir");
		jPanel1.add(but1);
		JButton but2 = new JButton("Fermer");
		jPanel1.add(but2);
		JButton but3 = new JButton("Allumer lampe");
		jPanel1.add(but3);
		JButton but4 = new JButton("Eteindre lampe");
		jPanel1.add(but4);
		JButton but5 = new JButton("Afficher température");
		jPanel1.add(but5);
		JButton but6 = new JButton("Go to 1st floor");
		jPanel1.add(but6);
		JButton but7 = new JButton("Go to 2nd floor");
		jPanel1.add(but7);
		JButton but8 = new JButton("Changer la temp...");
		jPanel1.add(but8);
		JButton but9 = new JButton("Magnetic Sensor");
		jPanel1.add(but9);
		JButton but10 = new JButton("Touch Sensor");
		jPanel1.add(but10);
		fr.add(jPanel1);
		
		but1.setEnabled(true);
		but2.setEnabled(true);
		but3.setEnabled(true);
		but4.setEnabled(true);
		but5.setEnabled(true);
		but6.setEnabled(true);
		but7.setEnabled(true);
		but8.setEnabled(true);
		but9.setEnabled(true);
		but10.setEnabled(true);
		
		/*try {
			obj= new Servo();
		} catch (PhidgetException | IOException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		fr.setVisible(true);
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		but1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				//Servo obj = null;
				try {
					//obj = new Servo();
					obj.ouverture();
					//obj.close_system();
				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Erreur pendant l'ouverture...");
				}

			}
		});
		but2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				try {
					//obj2 = new Servo();
					obj.fermeture();
					//obj2.close_system();
				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Erreur pendant la fermeture...");
				}

			}
		});
		but3.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				try {
					//obj2 = new Servo();
					//led.allumage();
					//obj2.close_system();
					ph.allumer_lampe(Config.LED_F1);
					ph.allumer_lampe(Config.LED_F2);
				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Erreur pendant l'allumage de la lampe...");
				}
				
			}
		});
		but4.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				//obj2 = new Servo();
				//led.extinction();
				//obj2.close_system();
				try {
					ph.eteindre_lampe(Config.LED_F1);
					ph.eteindre_lampe(Config.LED_F2);
				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		});
		but5.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				//obj2 = new Servo();
				//temp.print_temp();
				try {
					main2.ph.get_temp();
					System.out.println("[main2]: La température est de : " + main2.ph.get_temp() + "C°");

				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//obj2.close_system();
				
			}
		});
		
		but6.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				//obj2 = new Servo();
				//temp.print_temp();
				try {
					main2.ph.go_to_floor(Config.LED_F1);
					System.out.println("[main2]: Lampe 1er étage");
				} catch (PhidgetException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//obj2.close_system();
				
			}
		});
		
		but7.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				//obj2 = new Servo();
				//temp.print_temp();
				try {
					ph.go_to_floor(Config.LED_F2);
					System.out.println("[main2]: Lampe 2eme étage");

				} catch (PhidgetException | InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//obj2.close_system();
				
			}
		});
		
		but8.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				try {
					ph.send_db_temp();
				} catch (IOException | PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("[main2]: Température changée");
				
			}
		});

		but9.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				//obj2 = new Servo();
				//temp.print_temp();
				try {
					ph.get_magneticSensor();
					System.out.println("[main2]: Magnetic Sensor");

				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//obj2.close_system();

			}
		});

		but10.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				Servo obj2;
				try {
					//ph.send_db_temp();
					ph.get_touchSensor();

				} catch (PhidgetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("[main2]: Température changée");

			}
		});
	/*	try {
			//obj.contact();
		} catch (PhidgetException | IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}

}
