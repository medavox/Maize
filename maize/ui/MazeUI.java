package maize.ui;
import javax.swing.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.swing.event.*;
import java.awt.geom.*;
import javax.imageio.*;


import maize.*;
public class MazeUI extends JFrame implements ActionListener{


    private static String BOT_DIRECTORY = "./bots";
    private static String BOT_PACKAGE_NAME = "bots";

    // state
    private MazeTest mazeTest = null;
    /**The menu bar*/
    private JMenuBar menuBar;
    /**The tab handler which holds all of the panels*/
    private JTabbedPane tabs = new JTabbedPane();

    private MazeTabPanel mazeTab;
    private BotTabPanel botTab;
    private BotTabPanel bot2Tab;

    private SingleTestTabPanel singleTestTab;
    private DoubleTestTabPanel doubleTestTab;


    public MazeUI(MazeTest mazeTest)throws IOException{
	super("Maize UI");
	//super.setIconImage(new ImageIcon(ICON_PATH).getImage());
	setSize(800, 630);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setResizable(false);


	this.mazeTest = mazeTest;
	BotCompilerHelper.compileAndLoadBots(this.mazeTest, BOT_PACKAGE_NAME, BOT_DIRECTORY);


	//menu
	menuBar = buildMenu();
	this.setJMenuBar(menuBar);

	//tabs
	mazeTab = new MazeTabPanel(mazeTest);
	tabs.add("Select Maze", mazeTab);

	botTab = new BotTabPanel(mazeTest);
	tabs.add("Select Bot 1", botTab);

	singleTestTab = new SingleTestTabPanel(mazeTest, botTab, mazeTab);
	tabs.add("Single Test", singleTestTab);

	bot2Tab = new BotTabPanel(mazeTest);
	tabs.add("Select Bot 2", bot2Tab);


	doubleTestTab = new DoubleTestTabPanel(mazeTest, botTab, bot2Tab, mazeTab);
	tabs.add("Head to Head", doubleTestTab);


	setContentPane(tabs);
	setVisible(true);

	updatePanes();

    }

    // Builds a single menu item
    private JMenuItem buildMenuItem(String label, String actionCommand){
	JMenuItem menuItem = new JMenuItem(label);
	menuItem.setActionCommand(actionCommand);
	menuItem.addActionListener(this);
	return menuItem;
    }

    // Builds the whole drop-down menu
    private JMenuBar buildMenu(){
	JMenuBar menuBar = new JMenuBar();

	JMenu fileMenu = new JMenu("File");
	/*fileMenu.add(buildMenuItem("Analyse","analyse"));*/
	fileMenu.add(buildMenuItem("Exit","exit_all"));
	menuBar.add(fileMenu);


	JMenu botMenu = new JMenu("Bot");
	botMenu.add(buildMenuItem("Compile (" + BOT_DIRECTORY + ")","compile_bot"));
	botMenu.add(buildMenuItem("Instantiate...","inst_bot"));
	botMenu.add(buildMenuItem("Load...","load_bot"));
	menuBar.add(botMenu);

	JMenu mazeMenu = new JMenu("Maze");
	mazeMenu.add(buildMenuItem("New...","new_maze"));
	mazeMenu.add(buildMenuItem("Load...","load_maze"));
	menuBar.add(mazeMenu);

	return menuBar;
    }


    /**fired when a component performs an action whilst this class is listening to it.
      @param Ae the action even generated
     */
    public void actionPerformed(ActionEvent Ae){
	if(Ae.getActionCommand().equals("exit_all")){
	    quit();
	}else if(Ae.getActionCommand().equals("new_maze")){
	    new NewMazeDialog(mazeTest, this);
	}else if(Ae.getActionCommand().equals("load_maze")){
	    loadMaze();
	}else if(Ae.getActionCommand().equals("load_bot")){
	    loadBot();
	}else if(Ae.getActionCommand().equals("inst_bot")){
	    new NewBotDialog(mazeTest, this);
	}else if(Ae.getActionCommand().equals("compile_bot")){
	    BotCompilerHelper.compileAllBots(BOT_DIRECTORY); 
	}else{
	    System.err.println("Unable to find handler for action command: " + Ae.getActionCommand().toString());
	}
    }

    // Load a maze from a serialised file
    private void loadMaze(){
	final JFileChooser fileChooser = new JFileChooser();
	fileChooser.setMultiSelectionEnabled(false);
	//fileChooser.setFileFilter(new ImageFileFilter());
	if(fileChooser.showOpenDialog(this) == 0){
	    try{
		Maze m = (Maze)ClassSerializer.load(fileChooser.getSelectedFile());
		mazeTest.mazes.add(m);
		updatePanes();
	    }catch(Exception e){
		JOptionPane.showMessageDialog(this, "Error loading maze.");
	    }
	}
    }

    // Load a bot from a serialised file
    private void loadBot(){
	final JFileChooser fileChooser = new JFileChooser();
	fileChooser.setMultiSelectionEnabled(false);
	//fileChooser.setFileFilter(new ImageFileFilter());
	if(fileChooser.showOpenDialog(this) == 0){
	    try{
		Bot b = (Bot)ClassSerializer.load(fileChooser.getSelectedFile());
		mazeTest.bots.add(b);
		updatePanes();
	    }catch(Exception e){
		JOptionPane.showMessageDialog(this, "Error loading bot.");
	    }
	}
    }




    // Quit
    private void quit(){
	System.exit(0);
    }

    // Update all of the panes
    public void updatePanes(){
	this.mazeTab.update();
	this.botTab.update();
	this.singleTestTab.update();
	this.bot2Tab.update();
	this.doubleTestTab.update();
    }

}

