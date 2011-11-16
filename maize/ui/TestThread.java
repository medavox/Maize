package maize.ui;

import maize.*;
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





public class TestThread extends Thread{

    // store state on one run
    public Agent agent;
    public MazePanel panel;
    public int moves = 0;

    // flipped when the bot hits the end
    public boolean isDone = false;

    // delay, can be changed on the fly
    private int delayms;

    // label to update with 'moves: '
    private JLabel moveCountLabel;

    // quit on next run
    private boolean quit = false;
    // pause indefinitely
    private boolean pause = false;

    public TestThread(Maze m, Bot b, MazePanel mp, int delayms, JLabel moveCountLabel){
	AgentFactory af = new AgentFactory();
	agent = af.getAgent(m, b);
	this.panel = mp;
	panel.addAgent(agent);
	this.delayms = delayms;
	this.moveCountLabel = moveCountLabel;
    }	
    
    public void setDelay(int delayms){
	if(delayms >= 0)
	    this.delayms = delayms;
    }

    public void quit(){
	panel.remAgent(agent);
	quit = true;
    }

    public boolean isPaused(){
	return pause;
    }

    public void toggle_pause(){
	pause = !pause;
    }

    public void run(){
	while(!agent.isFinished()){   
	    
	    // move the agent
	    agent.move();

	    // update ui
	    moves++;
	    moveCountLabel.setText("Moves: " + moves);
	    panel.repaint();

	    pause_again:
	    
	    try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){ }
	   
	    // exit without completion
	    if( quit ){
		panel.remAgent(agent);
		panel.repaint();
		return;
	    } 

	    // hang but allow for response from other calls
	    while(pause)
		try{ Thread.currentThread().sleep(delayms); }catch(java.lang.InterruptedException ie){}
	}

	// execution is done, i.e. the bot has reached the finish
	isDone = true;

    }
}

