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
public class DoubleTestTabPanel extends JPanel implements ActionListener, ChangeListener{
    private static final int MAX_DELAY = 500;
    private static final String BOT_NAME_PLACEHOLDER = "No bot";
    private static final String MAZE_NAME_PLACEHOLDER = "No maze";
    private static final String MOVE_COUNT_PLACEHOLDER = "No moves yet";
    
    // links to state and to which bot to choose from UI
    private MazeTest mazeTest;
    private BotTabPanel botSelector;
    private BotTabPanel bot2Selector;
    private MazeTabPanel mazeSelector;


    private JButton startButton = new JButton("[re]Start");
    private JButton pauseButton = new JButton("Pause/play");
    private JButton stopButton = new JButton("Stop");
    private JButton refreshButton = new JButton("Reload");
    private JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_DELAY, MAX_DELAY/2);
    private JLabel moveCountLabel = new JLabel(MOVE_COUNT_PLACEHOLDER);
    private JLabel moveCount2Label = new JLabel(MOVE_COUNT_PLACEHOLDER);
    private JLabel botNameLabel = new JLabel(BOT_NAME_PLACEHOLDER);
    private JLabel bot2NameLabel = new JLabel(BOT_NAME_PLACEHOLDER);
    private JLabel mazeNameLabel = new JLabel(MAZE_NAME_PLACEHOLDER);


    private Maze maze = null;
    private Maze maze2 = null;
    private Bot bot = null;
    private Bot bot2 = null;
    private TestThread test = null; 
    private TestThread test2 = null; 
    private DoubleMazeFrame testFrame = null;
    private MazePanel m1Panel = null;

    public DoubleTestTabPanel(MazeTest mazeTest, BotTabPanel botSelector, BotTabPanel bot2Selector, 
	    MazeTabPanel mazeSelector) throws IOException{
	this.mazeTest = mazeTest;
	this.botSelector = botSelector;
	this.bot2Selector = bot2Selector;
	this.mazeSelector = mazeSelector;

	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());

	// speed slider
	speedSlider.setSize(new Dimension(200, 10));
	speedSlider.addChangeListener(this);

	// button
	pauseButton.addActionListener(this);
	startButton.addActionListener(this);
	stopButton.addActionListener(this);
	refreshButton.addActionListener(this);

	
	// label formatting
	moveCountLabel.setHorizontalAlignment( JLabel.CENTER);
	moveCount2Label.setHorizontalAlignment( JLabel.CENTER);


	// create second window with mazes
	this.testFrame = new DoubleMazeFrame();
	this.m1Panel = this.testFrame.getM1Panel();



	gbc.anchor = GridBagConstraints.CENTER;


	gbc.weightx = 0.5;
	gbc.weighty = 0.5;
	gbc.ipadx = 100;
	gbc.ipady = 20;
	// slider
	gbc.gridheight = 1;
	gbc.gridx = 1;
	this.add(new JLabel("Speed: "),gbc);
	gbc.gridy = 1;
	gbc.gridwidth = 2;
	gbc.ipadx = 200;
	this.add(speedSlider,gbc);
	
	// bot name
	gbc.gridheight = 1;
	gbc.gridwidth = 1;
	gbc.gridx = 1;
	gbc.gridy = 2;
	gbc.ipadx = 100;
	this.add(new JLabel("Bot 1 (red): "),gbc);
	gbc.gridx = 2;
	this.add(botNameLabel,gbc);

	// bot name
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 3;
	this.add(new JLabel("Bot 2 (blue): "),gbc);
	gbc.gridx = 2;
	this.add(bot2NameLabel,gbc);
	
	// maze name
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 4;
	this.add(new JLabel("Maze: "),gbc);
	gbc.gridx = 2;
	this.add(mazeNameLabel,gbc);



	gbc.fill = GridBagConstraints.HORIZONTAL;

	// maze name
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 5;
	this.add(refreshButton,gbc);
	gbc.gridx = 2;
	this.add(startButton,gbc);
	
	// maze name
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 6;
	this.add(pauseButton,gbc);
	gbc.gridx = 2;
	this.add(stopButton,gbc);

	
	
	gbc.anchor = GridBagConstraints.CENTER;
	gbc.ipady = 20;
	gbc.weighty = 20;
	// move count
	gbc.gridwidth = 2;
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 7;
	this.add(moveCountLabel,gbc);

	// mover count 2
	gbc.gridwidth = 2;
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 8;
	this.add(moveCount2Label,gbc);

	
	setVisible(true);
    }

    public void actionPerformed(ActionEvent Ae){
	if(Ae.getSource() == refreshButton){
	    if( test != null)
		stop();
	    update();
	}else if(Ae.getSource() == startButton){
	    if( test != null)
		stop();
	    start();
	}else if(Ae.getSource() == stopButton){
	    stop();
	}else if(Ae.getSource() == pauseButton){
	    pause();
	}
	    //saveMaze((Maze) mazeList.getSelectedValue());
	//}else if(Ae.getSource() == deleteButton){
	    //deleteMaze((Maze) mazeList.getSelectedValue());
	//}
	
    }

    public void stateChanged(ChangeEvent e) {
	JSlider source = (JSlider)e.getSource();
	if (!source.getValueIsAdjusting()) {

	    int speedval = (int)source.getValue();
	    if(this.test != null)
		this.test.setDelay( MAX_DELAY - speedval);
	    if(this.test2 != null)
		this.test2.setDelay( MAX_DELAY - speedval);
	    
	}
    }

   
    private void stop(){
	// check both for safety
	if(test == null && test2 == null){
	    JOptionPane.showMessageDialog(this, "No test is running.");
	    return;
	}
	
	// just in case, check and clean both
	if(test != null){
	    test.quit();
	    this.test = null;
	}
	if(test2 != null){
	    test2.quit();
	    this.test2 = null;
	}
    }

    private void pause(){
	// check and pause both
	if(test != null && test2 != null){
	    this.test.toggle_pause();
	    this.test2.toggle_pause();
	}else{
	    JOptionPane.showMessageDialog(this, "No test is running.");
	}
    }

    private void start(){
	if(maze == null || bot == null || test != null || test2 != null){
	    JOptionPane.showMessageDialog(this, "You must first select a maze and bot and stop any running test.");
	    return;
	} 
    


	this.test   = new TestThread(maze, bot, this.m1Panel, MAX_DELAY - speedSlider.getValue() ,moveCountLabel);
	this.test2  = new TestThread(maze, bot2, this.m1Panel, MAX_DELAY - speedSlider.getValue() , moveCount2Label);


	this.test.start();
	this.test2.start();
    }

    public void reset(){
	maze	= null;
	bot	= null;
	bot2	= null;
	test	= null;
	test2	= null;
	update();
    }

    //called to regtresjh state changes
    public void update(){
	try{
	    maze    = this.mazeSelector.getSelectedMaze();
	    bot	    = this.botSelector.getSelectedBot();
	    bot2    = this.bot2Selector.getSelectedBot();
	}catch(NullPointerException NPe){
	}

	if(maze == null){
	    mazeNameLabel.setText(MAZE_NAME_PLACEHOLDER);
	    m1Panel.setMaze(null);
	}else{
	    mazeNameLabel.setText(maze.getName());
	    m1Panel.setMaze(maze);
	    m1Panel.repaint();
	}
	

	if(bot == null)
	    botNameLabel.setText(BOT_NAME_PLACEHOLDER); 
	else
	    botNameLabel.setText(bot.getName());
	if(bot2 == null)
	    bot2NameLabel.setText(BOT_NAME_PLACEHOLDER); 
	else
	    bot2NameLabel.setText(bot2.getName());
	

	if(test == null)
	    moveCountLabel.setText(MOVE_COUNT_PLACEHOLDER);
	else
	    moveCountLabel.setText("Moves: " + test.moves);
	
	if(test2 == null)
	    moveCount2Label.setText(MOVE_COUNT_PLACEHOLDER);
	else
	    moveCount2Label.setText("Moves: " + test2.moves);
	
    }

	



}

