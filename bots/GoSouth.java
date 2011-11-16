package bots;
import maize.*;

public class GoSouth implements Bot
{
	public String getName()
	{
		return "Go South";
	}
	
	public String getDescription()
	{
		return "Goes South.";
	}
	
	public int nextMove(boolean[][] view, int x, int y, int o, int fx, int fy)
	{
		return Direction.LEFT;
	}
}
