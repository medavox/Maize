package maize.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import java.util.Vector;
import java.io.*;
import javax.imageio.*;


import maize.*;
public class BotTabPanel extends JPanel implements ActionListener, ListSelectionListener{

    // Text labels
    private static final String BOT_NAME_PLACEHOLDER	= "Name";
    private static final String BOT_DESC_PLACEHOLDER	= "Description";
    private static final String REFRESH_BUTTON_LABEL	= "Refresh";
    private static final String DELETE_BUTTON_LABEL	= "Delete";
    private static final String SAVE_BUTTON_LABEL	= "Save";

    // The maze test object to populate
    private MazeTest mazeTest;

    // The list and controlling buttons
    private JList botList;
    private JButton refreshButton	= new JButton(REFRESH_BUTTON_LABEL);
    private JButton deleteButton	= new JButton(DELETE_BUTTON_LABEL);
    private JButton saveButton		= new JButton(SAVE_BUTTON_LABEL);
    // Selected bot
    private JLabel botNameLabel		= new JLabel(BOT_NAME_PLACEHOLDER);
    private JLabel botDescriptionLabel	= new JLabel(BOT_DESC_PLACEHOLDER);


    public BotTabPanel(MazeTest mazeTest) throws IOException{
	this.mazeTest = mazeTest;

	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());

	//label style
	botNameLabel.setHorizontalAlignment( JLabel.CENTER );
	botNameLabel.setVerticalTextPosition( JLabel.CENTER );
	botNameLabel.setFont(new Font("Serif", Font.BOLD, 24));

	
	botDescriptionLabel.setHorizontalAlignment( JLabel.CENTER );
	botNameLabel.setVerticalTextPosition( JLabel.CENTER );


	// listeners
	refreshButton.addActionListener(this);
	deleteButton.addActionListener(this);
	saveButton.addActionListener(this);
	

	////symbol list
	botList = new JList(mazeTest.mazes);
	botList.addListSelectionListener(this);

	JScrollPane botListPanel = new JScrollPane(botList);
	botListPanel.setPreferredSize(new Dimension(300, 400));

	
	gbc.fill = GridBagConstraints.CENTER;
	// label1
	gbc.gridwidth = 5;
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.ipadx = 0;
	gbc.weightx = 0.5;
	gbc.ipady = 20;
	this.add(botNameLabel,gbc);
	
	// label2
	gbc.gridx = 0;
	gbc.gridy = 1;
	gbc.ipadx = 0;
	gbc.ipady = 30;
	this.add(botDescriptionLabel,gbc);

	//list
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.gridx = 2;
	gbc.gridy = 2;
	gbc.ipady = 0;
	this.add(botListPanel,gbc);

	//button
	gbc.anchor = GridBagConstraints.LINE_END;
	gbc.gridwidth = 1;
	gbc.gridx = 2;
	gbc.gridy = 3;
	this.add(refreshButton,gbc);

	gbc.anchor = GridBagConstraints.CENTER;
	gbc.gridx = 3;
	gbc.gridy = 3;
	this.add(saveButton,gbc);

	gbc.anchor = GridBagConstraints.LINE_START;
	gbc.gridx = 4;
	gbc.gridy = 3;
	this.add(deleteButton,gbc);


	setVisible(true);
    }

    public void actionPerformed(ActionEvent Ae){
	if(Ae.getSource() == refreshButton){
	    botList.setListData(mazeTest.bots);
	}else if(Ae.getSource() == saveButton){
	    saveBot((Bot) botList.getSelectedValue());
	}else if(Ae.getSource() == deleteButton){
	    deleteBot((Bot) botList.getSelectedValue());
	}
	
    }

    //called to regtresjh state changes
    public void update(){
	botList.setListData(mazeTest.bots);
    }

    // Deletes a bot from the list and from the mazeTest object
    private void deleteBot(Bot b){
	mazeTest.bots.remove(b);
	update();
    }

    // Fires when the user selects something in the list
    public void valueChanged(ListSelectionEvent LSe){
	if(LSe.getSource() != botList)
		return;

	if( botList.getSelectedIndex() == -1){
	    botNameLabel.setText( BOT_NAME_PLACEHOLDER);
	    botDescriptionLabel.setText( BOT_DESC_PLACEHOLDER);
	}else{
	    botNameLabel.setText( ((Bot)botList.getSelectedValue()).getName());
	    botDescriptionLabel.setText( ((Bot)botList.getSelectedValue()).getDescription());
	}
    }

    // Saves a bot to disk as a serialised object
    private void saveBot(Bot b){
	final JFileChooser fileChooser = new JFileChooser();
	fileChooser.setMultiSelectionEnabled(false);
	//fileChooser.setFileFilter(new SymbolFileFilter());
	if(fileChooser.showSaveDialog(this) == 0){
	    try{
		ClassSerializer.save( fileChooser.getSelectedFile(), b);	
	    }catch(Exception e){
		System.err.println("Error saving bot in bot tab panel, saveBot(Bot):\n");
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, "Error saving bot.");
	    }
	}
    }

    // Returns the selected bot from the list
    public Bot getSelectedBot(){
	return (Bot)botList.getSelectedValue();
    }

}

