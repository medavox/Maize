package maize.ui;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import javax.imageio.*;
import java.util.*;


import maize.*;
public class NewMazeDialog extends JDialog implements ActionListener{
    MazeTest mazeTest;
    MazeUI parent;

    private JButton createButton = new JButton("Create");    
    private JButton cancelButton = new JButton("Cancel");    

    private JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(20,1,250,1));
    private JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(20,1,250,1));

    private JComboBox factoryCombo;

    private JTextField nameField = new JTextField();

    public NewMazeDialog(MazeTest mazeTest, MazeUI owner){
	super((Frame)owner,"New Maze", true);	//1.6 only
	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	setResizable(false);

	this.parent = owner;
	this.mazeTest = mazeTest;

	createButton.addActionListener(this);
	cancelButton.addActionListener(this);

	setSize(new Dimension(600, 150));
	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());



	factoryCombo = new JComboBox( mazeTest.factories);


    



	// type
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.ipadx = 80;
	this.add(new JLabel("Type: "), gbc);

	gbc.gridwidth = 2;
	gbc.gridx = 1;
	gbc.gridy = 0;
	gbc.ipadx = 80;
	this.add(factoryCombo, gbc);

	// dimensions
	gbc.gridwidth = 1;
	gbc.gridx = 0;
	gbc.gridy = 1;
	gbc.ipadx = 80;
	this.add(new JLabel("Dim WxH: "),gbc);

	gbc.gridx = 1;
	gbc.gridy = 1;
	gbc.ipadx = 40;
	this.add(widthSpinner, gbc);

	gbc.gridx = 2;
	gbc.gridy = 1;
	gbc.ipadx = 40;
	this.add(heightSpinner, gbc);
	
	//name
	gbc.gridx = 0;
	gbc.gridy = 2;
	gbc.ipadx = 80;
	gbc.ipady = 10;
	this.add(new JLabel("Name: "), gbc);
	
	gbc.gridwidth = 2;
	gbc.gridx = 1;
	gbc.gridy = 2;
	gbc.ipadx = 80;
	this.add(nameField, gbc);


	gbc.gridwidth = 2;
	// buttons
	gbc.gridx = 0;
	gbc.gridy = 8;
	gbc.anchor = GridBagConstraints.LAST_LINE_START;
	this.add(cancelButton,gbc);


	gbc.gridwidth = 1;
	gbc.gridx = 2;
	gbc.gridy = 8;
	gbc.anchor = GridBagConstraints.LAST_LINE_END;
	this.add(createButton,gbc);


	setVisible(true);

    }



    public void actionPerformed(ActionEvent Ae){
	if(Ae.getSource() == cancelButton){
	    dispose();
    	}else if(Ae.getSource() == createButton){
	    int mw = ((SpinnerNumberModel)widthSpinner.getModel()).getNumber().intValue();
	    int mh = ((SpinnerNumberModel)heightSpinner.getModel()).getNumber().intValue();
	    
	    Maze m = ((MazeFactory)factoryCombo.getSelectedItem()).getMaze(mw, mh);
	    m.setName(nameField.getText());

	    mazeTest.mazes.add( m );
	    this.parent.updatePanes();
	    dispose();

	}
    }


}	
