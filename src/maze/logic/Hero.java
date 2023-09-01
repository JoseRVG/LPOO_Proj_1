package maze.logic;

public class Hero extends Piece {
	private boolean alive;
	private boolean armed;
	
	public Hero(int x, int y)
	{
		super(x,y); //to call the Piece constructor
		this.alive = true;
		this.armed = false;
	}
	public boolean isAlive()
	{
		return alive;
	}
	public boolean isArmed()
	{
		return armed;
	}
	public void die()
	{
		alive = false;
	}
	public void arm()
	{
		armed = true;
	}
	
}
