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
public class MazeDemo extends JFrame implements ActionListener{

    private static String BOT_DIRECTORY = "./bots";
    private static String BOT_PACKAGE_NAME = "bots";


    private JPanel	    detailsPanel;
    private MazePanel	    mazePanel;
    private JPanel	    basePane;

    private MazeTest mazeTest;
    private int mazeWidth;
    private int mazeHeight;

    public MazeDemo(MazeTest mazeTest,int mazeWidth, int mazeHeight)throws IOException{
	super("Maize Demo");
	//super.setIconImage(new ImageIcon(ICON_PATH).getImage());
	setSize(800, 630);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setResizable(false);

	// keep the maze object
	this.mazeTest = mazeTest;
	this.mazeWidth = mazeWidth;
	this.mazeHeight = mazeHeight;
	BotCompilerHelper.compileAndLoadBots(this.mazeTest, BOT_PACKAGE_NAME, BOT_DIRECTORY);

	// build the content pane and add it to the back
	try{
	    this.basePane = renderBase();
	}catch(IOException IOe){
	    System.err.println("Error loading resources : \n" + IOe );
	}
	setContentPane(basePane);
	setVisible(true);
    }

    private JPanel renderBase() throws IOException{
	//return new JPanel();
	return new SingleDemoPanel(this.mazeTest, this.mazeWidth, this.mazeHeight);
    }


    /**fired when a component performs an action whilst this class is listening to it.
      @param Ae the action even generated
     */
    public void actionPerformed(ActionEvent Ae){
	if(Ae.getActionCommand().equals("exit_all")){
	    quit();
	}
    }

    private void quit(){
	System.exit(0);
    }



}


