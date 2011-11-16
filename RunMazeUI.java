import maize.*;
import maize.ui.*;

import javax.swing.UIManager;
import java.io.IOException;
import java.io.*;

public class RunMazeUI{
    

    public static void main(String[] args){
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


	try{
	    new MazeUI(mt);
	}catch(IOException IOe){
	    System.err.println("Could not load some resources!");
	}
    }	
}
