package viewJava;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public abstract class PanelWelcome extends JPanel 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3862594280332085599L;
	
	protected JPanel _panelCenter, _panelSouth, _panelTop, _panelReadPan, _panelUserPan;
	protected JButton _buttonIn, _buttonOut, _buttonOpen, _buttonClose, _buttonInscription, _buttonUpdate, _buttonUpdateTag, _buttonSearch;
	protected JLabel _labelChoix, _labelTag, _labelUserNom, _labelUserPrenom, _labelUserMail, _labelUserStatut;
	protected JTextField _textFieldTagLu, _textFieldUserNom, _textFieldUserPrenom, _textFieldUserMail, _textFieldUserStatut;
	protected JComboBox<String> _comboBox;
	
	public PanelWelcome()
	{
		_panelCenter = new JPanel();
		_panelSouth = new JPanel();
		_panelTop = new JPanel();
		_panelReadPan = new JPanel();
		_panelUserPan = new JPanel();
		
		_buttonInscription = new JButton("Inscription");
		_buttonUpdate = new JButton("Mise à jour");
		_buttonSearch = new JButton("Rechercher un utilisateur par mail");
		_buttonUpdateTag = new JButton("Mise à jour du tag");
		_buttonIn = new JButton("Entrée parking");
		_buttonOut = new JButton("Sortie Parking");
		_buttonOpen = new JButton("Ouvrir barrière");
		_buttonClose = new JButton("Fermer barrière");
		_labelChoix = new JLabel("Choix de l'action");
        
        _buttonInscription.addActionListener(writeListener());
        _buttonUpdate.addActionListener(updateListener());
		_buttonUpdateTag.addActionListener(updateTagListener());
		_buttonSearch.addActionListener(searchListener());
	    _buttonIn.addActionListener(inListener());
	    _buttonOut.addActionListener(outListener());
	    _buttonOpen.addActionListener(openListener());
	    _buttonClose.addActionListener(closeListener());
		
		_comboBox = new JComboBox<String>();
	    _comboBox.addItem("Scan");
	    _comboBox.addItem("Inscription");
		_comboBox.addItem("Mise à jour");
	    _comboBox.addActionListener(formListener());

		_labelUserNom = new JLabel();
	    _labelUserNom.setText("Nom : ");
		_textFieldUserNom = new JTextField();
	    _textFieldUserNom.setEditable(false);
	    _textFieldUserNom.setPreferredSize(new Dimension(300, 30));
		
		_labelUserPrenom = new JLabel();
	    _labelUserPrenom.setText("Prénom : ");
		_textFieldUserPrenom = new JTextField();
	    _textFieldUserPrenom.setEditable(false);
	    _textFieldUserPrenom.setPreferredSize(new Dimension(300, 30));
		
		_labelUserMail = new JLabel();
	    _labelUserMail.setText("E-mail : ");
		_textFieldUserMail = new JTextField();
	    _textFieldUserMail.setEditable(false);
	    _textFieldUserMail.setPreferredSize(new Dimension(300, 30));

		_labelTag = new JLabel();
	    _labelTag.setText("Tag : ");
		_textFieldTagLu = new JTextField();
	    _textFieldTagLu.setEditable(false);
	    _textFieldTagLu.setPreferredSize(new Dimension(300, 30));

		_labelUserStatut = new JLabel();
		_labelUserStatut.setText("Statut : ");
		_textFieldUserStatut = new JTextField();
		_textFieldUserStatut.setEditable(false);
		_textFieldUserStatut.setPreferredSize(new Dimension(300, 30));

	    setBackground(Color.GRAY);
	    setLayout(new BorderLayout());
    
	    _panelTop.add(_labelChoix);
	    _panelTop.add(_comboBox);
	    add(_panelTop, BorderLayout.NORTH);

	    _panelUserPan.setBorder(BorderFactory.createTitledBorder("Informations utilisateur"));
	    _panelUserPan.setBackground(Color.WHITE);
	    _panelUserPan.add(_labelUserNom);
	    _panelUserPan.add(_textFieldUserNom);
	    _panelUserPan.add(_labelUserPrenom);
	    _panelUserPan.add(_textFieldUserPrenom);
	    _panelUserPan.add(_labelUserMail);
	    _panelUserPan.add(_textFieldUserMail);
	    _panelUserPan.add(_labelUserStatut);
		_panelUserPan.add(_textFieldUserStatut);

	    _panelReadPan.setBorder(BorderFactory.createTitledBorder("Tag lu"));
	    _panelReadPan.setPreferredSize(new Dimension(300, 100));
	    _panelReadPan.setBackground(Color.WHITE);
	    _panelReadPan.add(_labelTag);
	    _panelReadPan.add(_textFieldTagLu);

	    _panelCenter.setLayout(new BorderLayout());
	    _panelCenter.setBackground(Color.WHITE);
	    _panelCenter.add(_panelReadPan, BorderLayout.NORTH);
	    _panelCenter.add(_panelUserPan, BorderLayout.CENTER);
	    add(_panelCenter, BorderLayout.CENTER);

	    _panelSouth.setPreferredSize(new Dimension(300, 70));
	    _panelSouth.add(_buttonIn);

	    _panelSouth.add(_buttonOut);
	    _panelSouth.add(_buttonOpen);
	    _panelSouth.add(_buttonClose);
	    add(_panelSouth, BorderLayout.SOUTH);
	}

	public void switchToUpdateMode()
	{
		_panelSouth.removeAll();
		_panelSouth.add(_buttonSearch);
		_panelSouth.add(_buttonUpdate);
		_panelSouth.add(_buttonUpdateTag);
		_textFieldUserNom.setEditable(true);
		_textFieldUserPrenom.setEditable(true);
		_textFieldUserMail.setEditable(true);
		_panelSouth.revalidate();
		_panelSouth.repaint();
	}

	public void switchToWriteMode() {
        _panelSouth.removeAll();
        _panelSouth.add(_buttonInscription);
        _textFieldUserNom.setEditable(true);
        _textFieldUserPrenom.setEditable(true);
        _textFieldUserMail.setEditable(true);
        _panelSouth.revalidate();
        _panelSouth.repaint();
	}
	
	public void switchToReadMode()
	{
        _panelSouth.removeAll();
        _panelSouth.revalidate();
        _panelSouth.repaint();
		_panelSouth.add(_buttonIn);
        _panelSouth.add(_buttonOut);
		_panelSouth.add(_buttonOpen);
		_panelSouth.add(_buttonClose);
        _textFieldUserNom.setEditable(false);
		_textFieldUserPrenom.setEditable(false);
		_textFieldUserMail.setEditable(false);
	}
	
    public void updatePersonFields(String lastName, String firstName, String mail, Boolean statut)
    {
		_textFieldUserNom.setText(lastName);
		_textFieldUserPrenom.setText(firstName);
		_textFieldUserMail.setText(mail);
		if(statut) {
			_textFieldUserStatut.setText("A l'intérieur du parking");
		} else if (!_textFieldUserNom.getText().isEmpty()) {
			_textFieldUserStatut.setText("A l'extérieur du parking");
		} else {
			_textFieldUserStatut.setText("Non-client");
		}
    }

    public void showOptionPane(String message, String titre, String type)
    {
        int typeMessage = -1;

        switch (type)
        {
            case "ERROR_MESSAGE": typeMessage = JOptionPane.ERROR_MESSAGE; break;
            case "INFORMATION_MESSAGE": typeMessage = JOptionPane.INFORMATION_MESSAGE; break;
            case "WARNING_MESSAGE": typeMessage = JOptionPane.INFORMATION_MESSAGE; break;
        }

        JOptionPane.showMessageDialog(null, message, titre, typeMessage);
    }

    public void setTagLu(String tag)
    {
        _textFieldTagLu.setText(tag);
    }
	
	public ActionListener inListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				actionScalaIn();
			}
		};
	}
	
	public ActionListener outListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				actionScalaOut();
			}
		};
	}
	
	public ActionListener openListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				actionScalaOpen();
			}
		};
	}
	
	public ActionListener closeListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0) 
			{
				actionScalaClose();
			}
		};
	}

	
	public ActionListener writeListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				actionScalaWrite();
			}
		};
	}

	public ActionListener updateListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				actionScalaUpdate();
			}
		};
	}

	public ActionListener searchListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				actionScalaSearch();
			}
		};
	}

	public ActionListener updateTagListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				actionScalaUpdateTag();
			}
		};
	}
	
	public ActionListener formListener()
	{
		return new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				actionScalaForm();
			}
		};
	}
	
	protected abstract void actionScalaIn();
	protected abstract void actionScalaOut();
	protected abstract void actionScalaOpen();
	protected abstract void actionScalaClose();
	protected abstract void actionScalaWrite();
	protected abstract void actionScalaUpdate();
	protected abstract void actionScalaSearch();
	protected abstract void actionScalaUpdateTag();
	protected abstract void actionScalaForm();
}
