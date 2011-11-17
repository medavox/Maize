package bots;
import java.util.Random;
import maize.*;
import java.util.*;
import java.awt.Point;
/* 
 * HOW TO SOLVE THE 2 DEAD END PROBLEM
 * have a list of squares (points) you've been to before
 * whenever you detect a pattern, make a list of of the repeated squares;
 * for every repeated square in that list, check if it has any other directions you can go in.
 * 
 * OR
 * whilst doing this, create a map of the explored maze so far, containing its blocks and spaces
 * (a massive and growable boolean matrix, basically)
 * for all spaces on the edge of what we know (contain only blocks and unexplored areas for neighbours)
 * order them in preference by their closeness to the goal (or closeness to you), then head for each of them in turn.*/
public class Gorad implements Bot
{
	//NESW; always NESW.
	private boolean[] cardBlock = new boolean[4];
	private boolean frontBlokt;
	private boolean leftBlokt;
	private boolean rightBlokt;
	private boolean backBlokt;
	
	private boolean leftOfGoal;
	private boolean aboveGoal;
	private boolean furtherVertically;
	
	private Set<Point> visitedSquares = new LinkedHashSet<Point>(100);
	private Set<Point> retreadedSquares = new LinkedHashSet<Point>(100);
	//private Set<Square> visitedSquares = new LinkedHashSet<Square>(100);
	
	//init the previous move to an invalid one, so when compared in 1st move, prevSqCard does not equal any of them.
	private int prevSqCard = 5;
	private int logicFails = 0;
	
	public String getName()
	{
		return "Gorad, the Go-Getter";
	}
	
	public String getDescription()
	{
		//return "Moves directly to the goal. If blocked, Gorad will try 1)to go around in the perpendicularly but still towards the goal, 2) the other perpendicular direction , or 3) the opposite direction to the optimum one.";
		return "Now with states! And randomness!";
		/* Tries to take the shortest possible route to the goal, by moving in the direction (x or y) that Gorad is further from the goal in.
		 * If a wall is encountered, Gorad will try in order (eg if north is best but blocked, and goal is north-west)
		 * 1)to go around in the perpendicular direction that it is from the goal, (eg west) 
		 * 2) the other perpendicular direction (eg east), then 
		 * 3) the opposite direction to the optimum one (eg south).
		 * Contains ~20% of your RDA* of nested if statements.
		 * *Based on average consumption rates for undergrads.*/
	}
	
	public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy)
	{
		int dx = x - fx;
		int dy = y - fy;
		
		frontBlokt = view[1][0];
		leftBlokt = view[0][1];
		rightBlokt = view[2][1];
		backBlokt = view[1][2];
		
		Point currentSquare = new Point(x, y);
		
		if(!visitedSquares.add(currentSquare)
		&& !retreadedSquares.add(currentSquare))
		{
			//WARNING! we've been to this square at least twice before!
			//we're probably stuck in a loop!
			System.out.println("Gorad: We've been to "+x+","+y+" at least twice before!");
			
		}
		
		
		/*make array of whether north, east, south west (respectively)
		 are blocked, independent of current orientation.
		 * 0=north
		 * 1=east
		 * 2=west
		 * 3=south
		 * */
		switch(o)
		{
			case Orientation.NORTH:
			cardBlock[0] = frontBlokt;
			cardBlock[1] = rightBlokt;
			cardBlock[2] = backBlokt;
			cardBlock[3] = leftBlokt;
			break;
			
			case Orientation.EAST:
			cardBlock[1] = frontBlokt;
			cardBlock[2] = rightBlokt;
			cardBlock[3] = backBlokt;
			cardBlock[0] = leftBlokt;
			break;
			
			case Orientation.SOUTH:
			cardBlock[2] = frontBlokt;
			cardBlock[3] = rightBlokt;
			cardBlock[0] = backBlokt;
			cardBlock[1] = leftBlokt;
			break;
			
			case Orientation.WEST:
			cardBlock[3] = frontBlokt;
			cardBlock[0] = rightBlokt;
			cardBlock[1] = backBlokt;
			cardBlock[2] = leftBlokt;
			break;
		}
				
		if(dx <= 0)
		{	
			//we are left of goal
			leftOfGoal = true;
		}
		else if (dx > 0)
		{
			//we are right of goal
			leftOfGoal = false;
		}
	
		if(dy <= 0)
		{	
			//we are above goal
			aboveGoal = true;
		}
		else if (dy > 0)
		{
			//we are below goal
			aboveGoal = false;
		}
		
		//are we further away horizontally or vertically
		if(Math.abs(dx) >= Math.abs(dy) )
		{
			//we are further horizontally from the goal than vertically.
			furtherVertically = false;
		}
		else
		{
			//we are further vertically from the goal than horizontally.
			furtherVertically = true;
		}
		
		int[] prefCard = new int[4];
		//sort cardinal directions in order of preference, 0 being the best direction to go in.
		if(aboveGoal)
		{
			if(furtherVertically)
			{
				prefCard[0] = Orientation.SOUTH;
				prefCard[3] = Orientation.NORTH;
				if(dx == 0)//if we aren't left or right of the goal, then randomise preference for east & west.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.EAST;
						prefCard[2] = Orientation.WEST;
					}
					else
					{
						prefCard[2] = Orientation.EAST;
						prefCard[1] = Orientation.WEST;
					}
				}
			}
			else if(dy != 0)//only run this if we are actually above or below the goal, not adjacent.
			{
				//not further vertically
				//Allow randomess to take effect in other if statements in the surrounding blocks if false
				prefCard[1] = Orientation.SOUTH;
				prefCard[2] = Orientation.NORTH;
			}
		}
		else
		{
			//below goal
			//if this branch executes, dy shouldn't == 0; aboveGoal is only true if dy <= 0.
			if(furtherVertically)
			{
				prefCard[0] = Orientation.NORTH;
				prefCard[3] = Orientation.SOUTH;
				if(dx == 0)//if we aren't left or right of the goal, then randomise preference for east & west.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.EAST;
						prefCard[2] = Orientation.WEST;
					}
					else
					{
						prefCard[2] = Orientation.EAST;
						prefCard[1] = Orientation.WEST;
					}
				}
			}
			else
			{
				prefCard[1] = Orientation.NORTH;
				prefCard[2] = Orientation.SOUTH;
			}
		}
		
		if(leftOfGoal)
		{
			if(!furtherVertically)
			{
				prefCard[0] = Orientation.EAST;
				prefCard[3] = Orientation.WEST;
				if(dy == 0)//if we aren't above or below the goal, then randomise preference for north & south.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.NORTH;
						prefCard[2] = Orientation.SOUTH;
					}
					else
					{
						prefCard[2] = Orientation.NORTH;
						prefCard[1] = Orientation.SOUTH;
					}
				}
			}
			else if(dx != 0)//only run this if we are actually left or right of the goal, not above or below it.
			{
				//further vertically
				prefCard[1] = Orientation.EAST;
				prefCard[2] = Orientation.WEST;
			}
		}
		else
		{
			 //right of goal
			 //if this branch executes, dx shouldn't == 0; leftOfGoal is only true if dx <= 0.
			if(!furtherVertically)
			{
				prefCard[0] = Orientation.WEST;
				prefCard[3] = Orientation.EAST;
				if(dy == 0)//if we aren't above or below the goal, then randomise preference for north & south.
				{
					//flip a coin.
					//(is a random number between 1 and 2 divisible by 2?)
					if(( (int)(Math.floor(Math.random()+1)) ) % 2 == 0)
					{
						prefCard[1] = Orientation.NORTH;
						prefCard[2] = Orientation.SOUTH;
					}
					else
					{
						prefCard[2] = Orientation.NORTH;
						prefCard[1] = Orientation.SOUTH;
					}
				}
			}
			else
			{
				prefCard[1] = Orientation.WEST;
				prefCard[2] = Orientation.EAST;
			}
		}
		
		boolean allUnique = true;
		//after all that, check to see if preferred directions are all unique
		for(int i = 0; i < prefCard.length; i++)
		{
			for(int j = 0; j < prefCard.length; j++)
			{
				if(i == j)
				{continue;}
				else
				{
					if(prefCard[i] == prefCard[j])
					{allUnique = false;}
				}
			}
		}
		if(!allUnique)//if they're not all unique, complain
		{System.out.print("Gorad error "+(++logicFails)+": not all preferred directions are unique! \n");}
		
		int blockedDirs=0;
		for(int i = 0; i < prefCard.length; i++)
		{
			if(cardBlock[prefCard[i]])//if this way is blocked, increment blockedDirs and move on
			{
				blockedDirs++;
				continue;
			}
			else if(prefCard[i] == prevSqCard)//if we came from this way, move on
			{
				continue;
			}
			else
			{
				//if it's not blocked, isn't the way we came
				//(and is the best direction due to the array testing the most preferential first),
				//go this way!
				return checkMove(prefCard[i], o);
			}
		}
		if(blockedDirs == 3)
		{
			//now check if the 3 directions which arent the one we came from are blocked; if so, go the we way we came.
			//we are in a dead end. By logic, the only unblocked path is the way we came. Go back that way.
			return checkMove(prevSqCard, o);
		}
		System.out.print("Gorad error "+(++logicFails)+": in nextMove(). randomising direction. \n");
		return (int)(Math.random() * 4);
	}
	
	//card - cardinal direction we want to travel in
	//orie - the current orientation of the bot
	private int checkMove(int card, int orie)
	{
		//simplest case: the cardinal direction we want to go in isn't blocked
		//should never be blocked now, due to block checking taking place in for loop
		//at the end of nextMove().
		if(!cardBlock[card])
		{
			//are we facing that direction as well?
			if(card == orie)
			{
				//YUSS
				//set the direction back to this square to be 180deg to the way we're going now
				prevSqCard = (card + 2) % 4;
				return Direction.FORWARD;
			}
			else if( ((orie+3) % 4) == card)
			{
				//is the preferred cardinal direction to our left?
				//we didn't move on this go, so only turn left, don't change prefSqCard;
				//the direction back to this square shouldnt change, so we remember which way we went
				//when we did actually last move.
				return Direction.LEFT;//turn left
			}
			else if(((orie+1) % 4) == card)
			{
				//to our right?
				return Direction.RIGHT;//turn right
			}
			else
			{
				//behind us?
				//set the direction back to this square to be 180deg to the way we're going now
				prevSqCard = (card + 2) % 4;
				return Direction.BACK;
			}
			
		}
		else
		{
			//this code should no longer execute, due to block testing now being done
			//by a for statement in nextMove().
			//any cardinal direction passed to checkMove() should definitely be unblocked,
			//so if we reach this state, there is an error.
			System.out.print("Gorad error "+(++logicFails)+": the direction we chose wasn't blocked, and now it is!");
		}
		System.out.print("Gorad error "+(++logicFails)+": in checkMove(). randomising direction. \n");
		return (int) (Math.random() * 4);
	}
}

class Square
{
	private boolean[][] view;
	public int x, y;
	public byte timesVisited = 0;
	public Square(int x, int y, boolean[][] v)
	{
		view = v;
		this.x = x;
		this.y = y;
	}
}
