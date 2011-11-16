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
// Renders two mazes side-by-side
public class DoubleMazeFrame extends JFrame {

    private final String SPACE	= "imgres/space.png";
    private final String WALL	= "imgres/wall.png";
    private final String START	= "imgres/start.png";
    private final String FINISH	= "imgres/finish.png";
    private final String BOTN	= "imgres/botN.png";
    private final String BOTE	= "imgres/botE.png";
    private final String BOTS	= "imgres/botS.png";
    private final String BOTW	= "imgres/botW.png";
    private final String BOT2N	= "imgres/bot2N.png";
    private final String BOT2E	= "imgres/bot2E.png";
    private final String BOT2S	= "imgres/bot2S.png";
    private final String BOT2W	= "imgres/bot2W.png";


    private Maze m1 = null;
    private Maze m2 = null;

    private MazePanel m1Panel;

    public DoubleMazeFrame() throws IOException{
	super("Auxiliary Mazes");
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	//setResizable(false);
   
        //assign both	
	//this.m1 = m1;
	//this.m2 = m2;


	setSize(new Dimension(520, 530));
	setResizable(false);
	GridBagConstraints gbc = new GridBagConstraints();
	setLayout(new GridBagLayout());
	

	// maze panel
	BufferedImage space	= ImageIO.read(new File(SPACE));
	BufferedImage wall	= ImageIO.read(new File(WALL));
	BufferedImage start	= ImageIO.read(new File(START));
	BufferedImage finish	= ImageIO.read(new File(FINISH));
	BufferedImage botN	= ImageIO.read(new File(BOTN));
	BufferedImage botE	= ImageIO.read(new File(BOTE));
	BufferedImage botS	= ImageIO.read(new File(BOTS));
	BufferedImage botW	= ImageIO.read(new File(BOTW));
	BufferedImage bot2N	= ImageIO.read(new File(BOT2N));
	BufferedImage bot2E	= ImageIO.read(new File(BOT2E));
	BufferedImage bot2S	= ImageIO.read(new File(BOT2S));
	BufferedImage bot2W	= ImageIO.read(new File(BOT2W));

	this.m1Panel = new MazePanel(space, wall, start, finish, botN, botE, botS, botW, bot2N, bot2E, bot2S, bot2W);
	this.m1Panel.setSize(500,500);

	// mazes are canvases -- reign in their greedy getSize();
	JPanel m1PanelPanel = new JPanel();

	m1PanelPanel.add(this.m1Panel);
	m1PanelPanel.setSize(500,500);	

	// type
	gbc.gridx = 0;
	gbc.gridy = 0;
	this.add(new JLabel("Maze 1"), gbc);

	gbc.gridx = 0;
	gbc.gridy = 1;
	this.add(m1PanelPanel, gbc);	
	
	setVisible(true);
	this.repaint();
	

    }


    public MazePanel getM1Panel(){
	return this.m1Panel;
    }

}	
