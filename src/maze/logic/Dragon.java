package maze.logic;

public class Dragon extends Piece {
	private boolean alive;
	private boolean sleeping;
	
	public Dragon(int x, int y)
	{
		super(x,y);
		this.alive = true;
		this.sleeping = false;
	}
	
	public boolean isAlive()
	{
		return alive;
	}
	
	public void die()
	{
		alive = false;
	}
	
	public boolean isSleeping()
	{
		return sleeping;
	}
	
	public void awaken()
	{
		sleeping = false;
	}
	public void sleep()
	{
		sleeping = true;
	}

}
