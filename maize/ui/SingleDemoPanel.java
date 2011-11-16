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
public class SingleDemoPanel extends JPanel implements ActionListener, ChangeListener{
    private static final int MAX_DELAY = 500;
    private static final String BOT_NAME_PLACEHOLDER = "No bot";
    private static final String MAZE_NAME_PLACEHOLDER = "No maze";
    private static final String MOVE_COUNT_PLACEHOLDER = "No moves yet";

    private final String SPACE	= "imgres/space.png";
    private final String WALL	= "imgres/wall.png";
    private final String START	= "imgres/start.png";
    private final String FINISH	= "imgres/finish.png";
    private final String BOTN	= "imgres/botN.png";
    private final String BOTE	= "imgres/botE.png";
    private final String BOTS	= "imgres/botS.png";
    private final String BOTW	= "imgres/botW.png";
    
    // links to state and to which bot to choose from UI
    private MazeTest mazeTest;
    private int mazeWidth;
    private int mazeHeight;

    private JPanel mazePanelPanel = new JPanel();
    private MazePanel mazePanel;


    private JButton pauseButton = new JButton("Pause/play");
    private JButton stopButton = new JButton("Stop");
    private JButton refreshButton = new JButton("Reload");
    private JSlider speedSlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_DELAY, MAX_DELAY/2);
    private JLabel moveCountLabel = new JLabel(MOVE_COUNT_PLACEHOLDER);
    private JLabel botNameLabel = new JLabel(BOT_NAME_PLACEHOLDER);
    private JLabel mazeNameLabel = new JLabel(MAZE_NAME_PLACEHOLDER);


    private Maze maze = null;
    private Bot bot = null;
    private TestThread test = null; 

    private boolean isRunning = true;

    public SingleDemoPanel(MazeTest mazeTest, int mazeWidth, int mazeHeight) throws IOException{
	this.mazeTest = mazeTest;
	this.mazeWidth = mazeWidth;
	this.mazeHeight = mazeHeight;

	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());

	// speed slider
	speedSlider.setSize(new Dimension(200, 10));
	speedSlider.addChangeListener(this);

	// button
	pauseButton.addActionListener(this);
	stopButton.addActionListener(this);
	refreshButton.addActionListener(this);


	// maze panel
	BufferedImage space	= ImageIO.read(new File(SPACE));
	BufferedImage wall	= ImageIO.read(new File(WALL));
	BufferedImage start	= ImageIO.read(new File(START));
	BufferedImage finish	= ImageIO.read(new File(FINISH));
	BufferedImage botN	= ImageIO.read(new File(BOTN));
	BufferedImage botE	= ImageIO.read(new File(BOTE));
	BufferedImage botS	= ImageIO.read(new File(BOTS));
	BufferedImage botW	= ImageIO.read(new File(BOTW));
	mazePanel		= new MazePanel(space, wall, start, finish, botN, botE, botS, botW);
	mazePanel.setSize(500,500);

	mazePanelPanel.add(mazePanel);




	gbc.fill = GridBagConstraints.CENTER;
	// panel
	gbc.gridheight = 7;
	gbc.gridx = 0;
	gbc.gridy = 0;
	gbc.ipadx = 0;
	gbc.ipady = 20;
	this.add(mazePanelPanel,gbc);


	// slider
	gbc.gridheight = 1;
	gbc.gridwidth = 2;
	gbc.gridx = 1;
	gbc.ipadx = 200;
	gbc.ipady = 30;
	this.add(new JLabel("Speed: "),gbc);
	gbc.gridy = 1;
	gbc.gridx = 1;
	this.add(speedSlider,gbc);
	
	// bot name
	gbc.gridwidth = 1;
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 2;
	gbc.ipadx = 0;
	this.add(new JLabel("Bot: "),gbc);
	gbc.gridx = 2;
	this.add(botNameLabel,gbc);

	// maze name
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 3;
	this.add(new JLabel("Maze: "),gbc);
	gbc.gridx = 2;
	this.add(mazeNameLabel,gbc);

	
	gbc.fill = GridBagConstraints.HORIZONTAL;

	// maze name
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 4;
	gbc.gridwidth = 2;
	this.add(refreshButton,gbc);
	
	// maze name
	gbc.gridwidth = 1;
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 5;
	this.add(pauseButton,gbc);
	gbc.gridx = 2;
	this.add(stopButton,gbc);

	// maze name
	gbc.fill = GridBagConstraints.CENTER;
	gbc.gridwidth = 2;
	gbc.gridheight = 1;
	gbc.gridx = 1;
	gbc.gridy = 6;
	this.add(moveCountLabel,gbc);

	
	setVisible(true);
	
	new RestartThread(this).start();
    }

    public void actionPerformed(ActionEvent Ae){
	if(Ae.getSource() == refreshButton){
	    reset();
	    update();
	    mazePanel.repaint();
	}else if(Ae.getSource() == stopButton){
	    stop();
	}else if(Ae.getSource() == pauseButton){
	    pause();
	}
    }

    public void stateChanged(ChangeEvent e) {
	JSlider source = (JSlider)e.getSource();
	if (!source.getValueIsAdjusting()) {

	    int speedval = (int)source.getValue();
	    if(this.test != null){
		//System.out.println("Changing delay to " + (MAX_DELAY-speedval));
		this.test.setDelay( MAX_DELAY - speedval);
	    }
	}
    }

   
    private void stop(){
	if(test == null){
	    JOptionPane.showMessageDialog(this, "No test is running.");
	    return;
	}
	
	test.quit();
	this.test = null;
	this.isRunning = false;
	mazePanel.repaint();
    }

    private void pause(){
	if(test == null){
	    JOptionPane.showMessageDialog(this, "No test is running.");
	    return;
	}
	// stop if running, start if not.
	this.test.toggle_pause();
    }

    private void start(){
	if(maze == null || bot == null || test != null){
	    JOptionPane.showMessageDialog(this, "You must first select a maze and bot and stop any running test.");
	    return;
	} 

	this.test = new TestThread(maze, bot, mazePanel, MAX_DELAY - speedSlider.getValue() , moveCountLabel);

	this.test.start();
	this.isRunning = true;
    }

    public void reset(){
	if( test != null)
	    stop();
	maze = getNewMaze();
	bot = (Bot)getRandomVectorComponent(this.mazeTest.bots);
	test = null;
	update();
	start();
    }

    private Maze getNewMaze(){
	// select a random factory
	MazeFactory factory = (MazeFactory)getRandomVectorComponent(this.mazeTest.factories);	
	Maze m = (factory).getMaze(this.mazeWidth, this.mazeHeight);
	m.setName("Maze_" + Math.random());

	mazeTest.mazes.add( m );
	return(m);
    }


    // so not thread safe
    private Object getRandomVectorComponent(Vector v){
	int length = v.size();
	if(length == 0){
	    System.err.println("Cannot find an item in a relatively important vector! \nThis is normally caused by having no bots loaded!");
	    System.exit(1);
	}
	return( v.elementAt( (int)(Math.random() * length)));
    }
    
    //called to regtresjh state changes
    public void update(){
	//try{
	    ////maze = this.mazeSelector.getSelectedMaze();
	    ////bot = this.botSelector.getSelectedBot();
	//}catch(NullPointerException NPe){
	//}

	if(maze == null){
	    mazeNameLabel.setText(MAZE_NAME_PLACEHOLDER);
	}else{
	    mazeNameLabel.setText(maze.getName());
	    mazePanel.setMaze(maze);
	}

	if(bot == null){
	    botNameLabel.setText(BOT_NAME_PLACEHOLDER); 
	}else{
	    botNameLabel.setText(bot.getName());
	}

	if(test == null){
	    moveCountLabel.setText(MOVE_COUNT_PLACEHOLDER);
	}else{
	    moveCountLabel.setText("Moves: " + test.moves);
	}

    }

    public boolean isRunning(){
	boolean paused = false;
	if(this.test != null)
	    paused = this.test.isPaused();

	return this.isRunning && !paused;
    }

    public class RestartThread extends Thread {
	private SingleDemoPanel parent;
	private int DELAY = 10000;

	public RestartThread(SingleDemoPanel parent){
	    this.parent = parent;
	}

	public void run(){
	    while(true) 
		if(parent.isRunning()){
		    parent.reset();
		    try{ Thread.currentThread().sleep(DELAY); }catch(java.lang.InterruptedException ie){ }
	    }
	}
    }



}

