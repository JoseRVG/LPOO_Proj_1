package maze.logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Piece implements Serializable{
	private int x;
	private int y;
	
	public enum Direction {UP, DOWN, LEFT, RIGHT}
	
	public Piece(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	public int[] getPos()
	{
		int[] pos = new int[2];
		pos[0] = x;
		pos[1] = y;
		return pos;
	}
	public void setPos(int [] newPos)
	{
		x = newPos[0];
		y = newPos[1];
	}
	public void move(Direction dir)
	{
		switch(dir)
		{
		case UP:
			y = y - 1;
			break;
		case DOWN:
			y = y + 1;
			break;
		case LEFT:
			x = x - 1;
			break;
		case RIGHT:
			x = x + 1;
			break;
		}
	}
	public boolean isOver(Piece p)
	{
		if(x == p.x && y == p.y)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	public boolean isAdjacent(Piece p)
	{
		if(isOver(p))
		{
			return false;
		}
		else if( (y == p.y && Math.abs(x-p.x) <= 1) || (x == p.x && Math.abs(y-p.y) <= 1) )
		{
			return true;
		}
		else return false;
	}
}
