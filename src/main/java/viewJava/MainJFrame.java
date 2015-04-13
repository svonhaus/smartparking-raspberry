package viewJava;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;

/**
 * Initialisation et configuration d'une JFrame pour l'interface graphique
 */
public class MainJFrame extends JFrame
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 6618848859440615955L;
	private Container container;
    private JMenuBar menuBarOfApp;
    private JMenu menuFile;
    private JMenuItem menuItemHome, menuItemQuit;
    private JPanel _panelWelcome;
    
    public MainJFrame(PanelWelcome panelWelcome)
    {
		/*Création de la fenêtre*/
        super("RFID");
        
    	_panelWelcome = panelWelcome;
        
        container = this.getContentPane();
        container.setLayout(new BorderLayout());
        this.setLocationRelativeTo(null);
        
	    //Définit un titre pour notre fenêtre
	    setTitle("RFID SmartKing");
	    //Définit sa taille : 400 pixels de large et 100 pixels de haut
	    setSize(350, 550);
	    //Nous demandons maintenant à notre objet de se positionner au centre
	    setLocationRelativeTo(null);
	    //Termine le processus lorsqu'on clique sur la croix rouge
	    setResizable(false);
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(eventCloseWindow());

		/*Ajout d'une menuBarOfApp à la fenêtre*/
        menuBarOfApp = new JMenuBar( );
        this.setJMenuBar(menuBarOfApp);

		/*Création des JMenu*/
        menuFile= new JMenu("File");
        menuFile.setMnemonic('F');

		/*Ajout des menus à la menuBarOfApp*/
        menuBarOfApp.add(menuFile);

		/*Création du gestionnaire d'action*/
        MonGestionnaireAction g= new MonGestionnaireAction();

		/*Ajout du JMenuItem "menuItemQuit" au menuFile*/
        menuItemHome =new JMenuItem("Home");
        menuItemHome.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
        menuItemHome.addActionListener(g);
        menuFile.add(menuItemHome);

        menuFile.addSeparator( );

        menuItemQuit =new JMenuItem("Exit");
        menuItemQuit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        menuItemQuit.addActionListener(g);
        menuFile.add(menuItemQuit);;
        
        this.add(panelWelcome);
        this.setVisible(true);
    }

    public WindowAdapter eventCloseWindow()
    {
        return new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent we)
            {
                closeWindow();
            }
        };
    }

    public void closeWindow()
    {
        String buttons[] = {"Yes", "No"};

        int PromptResult = JOptionPane.showOptionDialog(null, "Are you sure you want to exit?", "Warning",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null,
                buttons, buttons[1]);

        if (PromptResult == 0)
            System.exit(0);
    }
    
    public void replacePanel(JPanel panel)
    {
        container.removeAll();
        container.add(_panelWelcome);
        container.repaint();
        container.validate();
    }
    
    private class MonGestionnaireAction implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if (e.getSource() == menuItemQuit)
                closeWindow();

            if (e.getSource() == menuItemHome)
            {
                replacePanel(_panelWelcome);
            }
        }
    }
}
