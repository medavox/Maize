import maize.*;
import maize.ui.*;

import javax.swing.UIManager;
import java.io.IOException;

public class RunMazeDemo{
    public static void main(String args[]){
	try{
		UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
	//	UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
	//	UIManager.setLookAndFeel("com.sun.java.swing.plaf.metal.MetalLookAndFeel");
              //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); 
	}catch(Exception e){}


	MazeTest mt = new MazeTest();
	mt.factories.add( new FullDFSMazeFactory());
	mt.factories.add( new CircleMazeFactory());
	mt.factories.add( new ScatterMazeFactory());
	mt.factories.add( new RandomScatterMazeFactory());
	mt.factories.add( new LineMazeFactory());
	mt.factories.add( new EmptyMazeFactory());
	//mt.factories.add( new ());

	//mt.bots.add(new LeftBot());
	//mt.bots.add(new RightBot());
	//mt.bots.add(new DaveBot());
	//mt.bots.add(new LeftStateBot());
	//mt.bots.add(new SmartTreeStateBot());
 

	try{
	    new MazeDemo(mt, 20, 20);
	}catch(IOException IOe){
	    System.err.println("Could not load some resources!");
	}
	
	    

    }
}
