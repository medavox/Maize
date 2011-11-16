package maize.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import maize.*;
import java.awt.geom.*;


public class MazePanel extends Canvas{
   
    // The maze to show
    private Maze maze = null;

    // Keep lists of stuff to render
    private Vector<Agent> agents = new Vector<Agent>();
    private Vector<Point> dirty_tiles = new Vector<Point>();

    // These are scaled, but needs to be known
    private static int TILE_WIDTH = 16;
    private static int TILE_HEIGHT = 16;

    // Images used to render
    private BufferedImage space;
    private BufferedImage wall;
    private BufferedImage botN;
    private BufferedImage botE;
    private BufferedImage botW;
    private BufferedImage botS;
    private BufferedImage bot2N;
    private BufferedImage bot2E;
    private BufferedImage bot2W;
    private BufferedImage bot2S;
    private BufferedImage start;
    private BufferedImage finish;

    private Image buffer = null;

    // if everything is dirty also erase background
    private boolean blankBeforePaint = false;

    public MazePanel(Maze maze, BufferedImage space, BufferedImage wall, 
		     BufferedImage start, BufferedImage finish,
		     BufferedImage botN, BufferedImage botE, BufferedImage botS, BufferedImage botW){

		// Set maze
		this.maze = maze;

		// Populate images	
		TILE_HEIGHT = space.getHeight();
		TILE_WIDTH = space.getWidth();
		this.space = space;
		this.wall = wall;
		this.botN = botN;
		this.botE = botE;
		this.botS = botS;
		this.botW = botW;
		this.start = start;
		this.finish = finish;

		// Ensure the first render refreshes everything
		dirtyEverything();
    }

    public MazePanel(BufferedImage space, BufferedImage wall, 
		     BufferedImage start, BufferedImage finish,
		     BufferedImage botN, BufferedImage botE, BufferedImage botS, BufferedImage botW){

		// Populate images	
		TILE_HEIGHT = space.getHeight();
		TILE_WIDTH = space.getWidth();
		this.space = space;
		this.wall = wall;
		this.botN = botN;
		this.botE = botE;
		this.botS = botS;
		this.botW = botW;
		this.start = start;
		this.finish = finish;

		// Ensure the first render refreshes everything
		dirtyEverything();
    }
    
		//overload used to pass images for second bot
        public MazePanel(BufferedImage space, BufferedImage wall, 
		     BufferedImage start, BufferedImage finish,
		     BufferedImage botN, BufferedImage botE, BufferedImage botS, BufferedImage botW,
		     BufferedImage bot2N, BufferedImage bot2E, BufferedImage bot2S, BufferedImage bot2W){

		// Populate images	
		TILE_HEIGHT = space.getHeight();
		TILE_WIDTH = space.getWidth();
		this.space = space;
		this.wall = wall;
		this.botN = botN;
		this.botE = botE;
		this.botS = botS;
		this.botW = botW;
		this.bot2N = bot2N;
		this.bot2E = bot2E;
		this.bot2S = bot2S;
		this.bot2W = bot2W;
		this.start = start;
		this.finish = finish;

		// Ensure the first render refreshes everything
		dirtyEverything();
    }
	
    // Set everything to be rendered next time swing gets around to it.
    public void dirtyEverything(){

	if(maze != null)
	    for(int j=0; j<maze.getHeight(); j++){
		synchronized(dirty_tiles){
		    for(int i=0; i<maze.getWidth(); i++)
			dirty_tiles.add(new Point(i, j));
		}
	    }

	this.blankBeforePaint = true;
    }

    // Add an agent to the list to render
    public boolean addAgent(Agent a){
	if( agents.indexOf(a) == -1){
	    agents.add(a);
	    return true;
	}
	return false;
    }

    // Removes an agent from the list to simulate
    public boolean remAgent(Agent a){
	if(agents.indexOf(a) == -1)
	    return false;
	agents.remove(a);
	return true;
    }

    // Render what is dirty
    public void paint(Graphics g)
    {
		// One-off filling in of the background
		if(blankBeforePaint){
			g.setColor(Color.WHITE);
			g.fillRect(0,0, this.getWidth(), this.getHeight());
			this.blankBeforePaint = false;
		}

		// If no maze then make sure we render everything when the maze is added
		if(maze != null){
			dirtyEverything();
			update(g);
		}
    }
	
    // Renders only the dirty bits, keeps rendering quick
    private void renderDirtyAreas( Graphics g){
	synchronized(dirty_tiles){
	    for(Iterator<Point> ip = dirty_tiles.iterator(); ip.hasNext(); )
		drawTile( ip.next(), g);

	    dirty_tiles.clear();
	}
    }

    // Makes the agent's trails dirty so they get refreshed
    private void dirtyAgentAreas(){
	Iterator<Agent> ia = agents.iterator();
	Agent current;
	while( ia.hasNext()){
	    current = ia.next();
	    dirty_tiles.add(new Point( current.getX(), current.getY()));
	}
    }

    // Plots a tile at a given point
    // takes into account agents, etc
    private void drawTile(Point p, Graphics bg)
    {
		//System.out.println("No. Agents on this MazePanel: " + agents.size());
		boolean[][] mdata = maze.getData();
		BufferedImage img = null;

		// Check for maze background section
		if(maze.getEntX() == p.x && maze.getEntY() == p.y)
			img = start;
			//renderTile(start, p.x, p.y, bg);
		else if(maze.getExiX() == p.x && maze.getExiY() == p.y)
			img = finish;
			//renderTile(finish, p.x, p.y, bg);
		else if( mdata[p.x][p.y])
			img = wall;
		else
			img = space;
		

		// Check each agent
		//hopefully now renders last agent in the vector in blue.
		Iterator<Agent> ia = agents.iterator();
		Agent current;
		while( ia.hasNext()){
			current = ia.next();
		
			if(current.getX() == p.x && current.getY() == p.y)
			{
				if(ia.hasNext())
					img = bot2S;
				else
					img = botS;
				switch(current.getOrientation())
				{
					case Orientation.NORTH:
					if(ia.hasNext())
					img = bot2N;
					else
					img = botN;
					break;
					
					case Orientation.EAST:
					if(ia.hasNext())
					img = bot2E;
					else
					img = botE;
					break;
					
					case Orientation.WEST:
					if(ia.hasNext())
					img = bot2W;
					else
					img = botW;
				}
			}
		}

		// If we found something, scale and render the tile
		if(img != null)
		{
			//rescale the image to be done
			float tilex = (float)(this.getWidth() / maze.getWidth());
			float tiley = (float)(this.getHeight() / maze.getHeight());

			AffineTransform transform = AffineTransform.getScaleInstance((float)tilex / (float)img.getWidth(),  
											 (float)tiley / (float)img.getWidth());
			AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
			img = op.filter(img, null);

			renderTile(img, p.x, p.y, bg);
		}
    }

    // Renders a tile with a given buffered image
    private void renderTile(BufferedImage tile, int x, int y, Graphics g){
	g.drawImage(tile, (int)(x*((float)this.getWidth() / (float)maze.getWidth())), 
		(int)(y*((float)this.getHeight() / (float)maze.getHeight())), this);
    }

    // Update the render surface, dirties the agent's moved tiles automatically.
    public void update(Graphics g){
	// Blank if we have been asked to do so
	if(blankBeforePaint){
	    g.setColor(Color.WHITE);
	    g.fillRect(0,0, this.getWidth(), this.getHeight());
	    this.blankBeforePaint = false;
	}

	// Else update the dirty areas
	if(maze != null){
	    dirtyAgentAreas();
	    renderDirtyAreas(g);
	    dirtyAgentAreas();
	}
    }

    // Sets a given maze
    public void setMaze(Maze m){
	this.maze = m;
	dirtyEverything();
    }

}


// FIXME: this appears after a little while
/*
 *Exception in thread "AWT-EventQueue-0" java.lang.ArrayIndexOutOfBoundsException: -1
 *        at maize.ui.MazePanel.drawTile(MazePanel.java:162)
 *        at maize.ui.MazePanel.renderDirtyAreas(MazePanel.java:133)
 *        at maize.ui.MazePanel.update(MazePanel.java:221)
 *        at sun.awt.RepaintArea.updateComponent(RepaintArea.java:255)
 *        at sun.awt.X11.XRepaintArea.updateComponent(XRepaintArea.java:60)
 *        at sun.awt.RepaintArea.paint(RepaintArea.java:232)
 *        at sun.awt.X11.XComponentPeer.handleEvent(XComponentPeer.java:587)
 *        at java.awt.Component.dispatchEventImpl(Component.java:4936)
 *        at java.awt.Component.dispatchEvent(Component.java:4686)
 *        at java.awt.EventQueue.dispatchEventImpl(EventQueue.java:707)
 *        at java.awt.EventQueue.access$000(EventQueue.java:101)
 *        at java.awt.EventQueue$3.run(EventQueue.java:666)
 *        at java.awt.EventQueue$3.run(EventQueue.java:664)
 *        at java.security.AccessController.doPrivileged(Native Method)
 *        at java.security.ProtectionDomain$1.doIntersectionPrivilege(ProtectionDomain.java:76)
 *        at java.security.ProtectionDomain$1.doIntersectionPrivilege(ProtectionDomain.java:87)
 *        at java.awt.EventQueue$4.run(EventQueue.java:680)
 *        at java.awt.EventQueue$4.run(EventQueue.java:678)
 *        at java.security.AccessController.doPrivileged(Native Method)
 *        at java.security.ProtectionDomain$1.doIntersectionPrivilege(ProtectionDomain.java:76)
 *        at java.awt.EventQueue.dispatchEvent(EventQueue.java:677)
 *        at java.awt.EventDispatchThread.pumpOneEventForFilters(EventDispatchThread.java:211)
 *        at java.awt.EventDispatchThread.pumpEventsForFilter(EventDispatchThread.java:128)
 *        at java.awt.EventDispatchThread.pumpEventsForHierarchy(EventDispatchThread.java:117)
 *        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:113)
 *        at java.awt.EventDispatchThread.pumpEvents(EventDispatchThread.java:105)
 *        at java.awt.EventDispatchThread.run(EventDispatchThread.java:90)
 *
 */
