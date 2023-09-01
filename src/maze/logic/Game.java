package maze.logic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import maze.logic.Maze.CellType;

@SuppressWarnings("serial")
public class Game implements Serializable{
	private Maze maze;
	private Hero hero;
	private ArrayList<Dragon> dragons;
	private Sword sword;
	private Random rand;
	private DragonMode dragonMode;

	public enum DragonMode {IMMOBILE, MOVING, MOVING_SLEEPING}

	public enum Element {MAZE_NEUTRAL, MAZE_WALL, MAZE_EXIT, 
		HERO_UNARMED, HERO_ARMED, 
		DRAGON_AWAKE, DRAGON_SLEEPING, HERO_OVER_DRAGON_SLEEPING,
		SWORD, DRAGON_AWAKE_OVER_SWORD, DRAGON_SLEEPING_OVER_SWORD}

	/**
	 * A constructor for the Game class that generates a random maze with dimensions size x size,
	 * places one dragon on the board and sets its behavior to dMode.
	 *
	 * @param  dMode variable of type Game.DragonMode that decides the dragons' behavior
	 * @param  size number of lines/columns of the maze
	 * @return an object of the Game class
	 */
	public Game(DragonMode dMode, int size)
	{
		this(dMode,size,1);
	}
	
	/**
	 * A constructor for the Game class that generates a random maze with dimensions size x size,
	 * places as many dragons as the numDragons parameter on the board and sets their behavior to dMode.
	 *
	 * @param  dMode variable of type Game.DragonMode that decides the dragons' behavior
	 * @param  size number of lines/columns of the maze
	 * @param  numDragons number of dragons on the game
	 */
	public Game(DragonMode dMode, int size, int numDragons)
	{
		//Test for valid arguments
		if(size < 5)
		{
			throw new IllegalArgumentException("Maze size too small. Must be at least 5.");
		}
		if(size % 2 == 0)
		{
			throw new IllegalArgumentException("Maze size is even. Must be odd.");
		}
		//Check if the number of dragons is not excessive
		//(size-1)/2 is the number of cells on the visited array (to prevent adjacent Hero to a Dragon)
		//-2 is due to the sword and hero
		//-1 because on the worst case the hero is adjacent to two free cells on the spaces guarantied to be neutral (= those mapped to the visited cell array)
		int num_max_dragons = (int)Math.pow((size-1)/2,2) - 3;
		if(numDragons > num_max_dragons) 
		{
			throw new IllegalArgumentException("Maze has too many dragons. Must be at most ((size-1)/2)^2 - 3.");
		}

		//Create the pseudo-random number generator
		rand = new Random();
		Date date = new Date();
		rand.setSeed(date.getTime());

		//Build a random maze
		MazeBuilder builder = new MazeBuilder();
		char[][] charMap = builder.buildMaze(size,numDragons);

		//Make copy of charMap
		int lins = charMap.length;
		int cols = charMap[0].length;
		char[][] cop = new char[lins][cols];
		for (int y = 0; y < lins; y++) 
		{
			cop[y] = Arrays.copyOf(charMap[y], cols);
		}

		//Place the pices on the board
		int[] heroPos = findCharOnCharMap(cop,'H');
		hero = new Hero(heroPos[0],heroPos[1]);
		cop[heroPos[1]][heroPos[0]] = ' ';

		dragons = new ArrayList<Dragon>();
		for(int i = 0; i < numDragons; i++)
		{
			int[] dragonPos = findCharOnCharMap(cop,'D');
			Dragon dragon = new Dragon(dragonPos[0],dragonPos[1]);
			dragons.add(dragon);
			cop[dragonPos[1]][dragonPos[0]] = ' ';
		}

		int[] swordPos = findCharOnCharMap(cop,'E');
		sword = new Sword(swordPos[0],swordPos[1]);
		cop[swordPos[1]][swordPos[0]] = ' ';

		//Generate the maze
		maze = new Maze(cop);

		//Set the dragon mode
		dragonMode = dMode;
	}

	/**
	 * A constructor for the Game class that generates a maze according to charMap
	 * and sets the dragons' behavior to dMode.
	 * <p>
	 * The charMap parameter defines the pieces' position, maze size and number of dragons.
	 *
	 * @param  dMode variable of type Game.DragonMode that decides the dragons' behavior
	 * @param  charMap bidimensional char array that represents the desired maze and piece positions
	 */
	public Game(DragonMode dMode, char[][] charMap)
	{
		//Create the pseudo-random number generator
		rand = new Random();
		Date date = new Date();
		rand.setSeed(date.getTime());

		//Make copy of charMap
		int lins = charMap.length;
		int cols = charMap[0].length;

		//Test for valid arguments
		if(lins != cols)
		{
			throw new IllegalArgumentException("Maze must be a square.");
		}
		if(lins < 5)
		{
			throw new IllegalArgumentException("Maze size too small. Must be at least 5.");
		}
		if(lins % 2 == 0)
		{
			throw new IllegalArgumentException("Maze size is even. Must be odd.");
		}

		char[][] cop = new char[lins][cols];
		for (int y = 0; y < lins; y++) 
		{
			cop[y] = Arrays.copyOf(charMap[y], cols);
		}

		//Place the pices on the board
		int[] heroPos = findCharOnCharMap(cop,'H');
		hero = new Hero(heroPos[0],heroPos[1]);
		cop[heroPos[1]][heroPos[0]] = ' ';

		dragons = new ArrayList<Dragon>();
		int[] dragonPos;
		while( (dragonPos = findCharOnCharMap(cop,'D')) != null)
		{
			Dragon dragon = new Dragon(dragonPos[0],dragonPos[1]);
			dragons.add(dragon);
			cop[dragonPos[1]][dragonPos[0]] = ' ';
		}

		int num_max_dragons = (int)Math.pow((lins-1)/2,2) - 3;
		if(dragons.size() > num_max_dragons) 
		{
			throw new IllegalArgumentException("Maze has too many dragons. Must be at most ((size-1)/2)^2 - 3.");
		}

		int[] swordPos = findCharOnCharMap(cop,'E');
		sword = new Sword(swordPos[0],swordPos[1]);
		cop[swordPos[1]][swordPos[0]] = ' ';

		//Generate the maze
		maze = new Maze(cop);

		//Set the dragon mode
		dragonMode = dMode;
	}

	/**
	 * Searches for the character c on the charMap parameter.
	 * <p>
	 * It is used to look for the pieces' (hero, dragons and sword) position on the board.
	 * <p>
	 * 'H' stands for hero, 'D' stands for dragon, 
	 *
	 * @param  charMap that contains the maze and some of its pieces
	 * @param  c character associated with the piece to look for
	 * @return the position of the piece (or null if the piece doesn't exist)
	 * @see Command line interface character conventions (instructions given by the teachers on the first assignment sheet)
	 */
	public int[] findCharOnCharMap(char[][] charMap, char c)
	{
		int lins = charMap.length;
		int cols = charMap[0].length;

		for(int y = 0; y < lins; y++)
		{
			for(int x = 0; x < cols; x++)
			{
				if(charMap[y][x] == c)
				{
					int[] pos = new int[2];
					pos[0] = x;
					pos[1] = y;
					return pos;
				}
			}
		}
		return null;
	}
	
	/**
	 * A constructor for the Game class that builds the maze and places the pieces according to elemMap,
	 * and sets the dragon behavior to dMode.
	 *
	 * @param  dMode variable of type Game.DragonMode that decides the dragons' behavior
	 * @param  elemMap bidimensional Game.Element array that represents the desired maze and piece positions
	 * @see Game.Element enum
	 */
	public Game(DragonMode dMode, Game.Element[][] elemMap)
	{
		//Create the pseudo-random number generator
		rand = new Random();
		Date date = new Date();
		rand.setSeed(date.getTime());

		//Retrieve elemMap dimensions
		int lins = elemMap.length;
		int cols = elemMap[0].length;

		//Test for valid arguments
		if(lins != cols)
		{
			throw new IllegalArgumentException("Maze must be a square.");
		}
		if(lins < 5)
		{
			throw new IllegalArgumentException("Maze size too small. Must be at least 5.");
		}
		if(lins % 2 == 0)
		{
			throw new IllegalArgumentException("Maze size is even. Must be odd.");
		}

		//Create the charMap to use to call the Maze constructor and retreive all pieces
		char[][] charMap = new char[lins][cols];
		dragons = new ArrayList<Dragon>();
		boolean includesHero = false;
		boolean includesSword = false;
		boolean includesExit = false;

		for (int y = 0; y < lins; y++) 
		{
			for(int x = 0; x < cols; x++)
			{
				Game.Element elem = elemMap[y][x];
				switch(elem)
				{
				case MAZE_NEUTRAL:
					charMap[y][x] = ' ';
					break;
				case MAZE_WALL:
					charMap[y][x] = 'X';
					break;
				case MAZE_EXIT:
					charMap[y][x] = 'S';
					break;
				case HERO_UNARMED:
					if(includesHero)
					{
						throw new IllegalArgumentException("Maze must contain exactly one hero.");
					}
					hero = new Hero(x,y);
					charMap[y][x] = ' ';
					includesHero = true;
					break;
				case DRAGON_AWAKE:
					Dragon dragon = new Dragon(x,y);
					dragons.add(dragon);
					charMap[y][x] = ' ';
					break;
				case SWORD:
					if(includesSword)
					{
						throw new IllegalArgumentException("Maze must contain exactly one sword.");
					}
					sword = new Sword(x,y);
					charMap[y][x] = ' ';
					includesSword = true;
					break;
				default:
					throw new IllegalArgumentException("Unknown element on the created maze.");
				}

				//Check that the borders are walls or the exit (only one exit allowed)
				if(y == 0 || y == lins - 1 || x == 0 || x == cols-1)
				{
					if(elem != Game.Element.MAZE_WALL)
					{
						if(elem == Game.Element.MAZE_EXIT && !includesExit)
						{
							includesExit = true;
						}
						else
						{
							throw new IllegalArgumentException("Maze must be surrounded by walls and have exactly one exit.");
						}
					}
				}
				else //exit must be at the borders
				{
					if(elem == Game.Element.MAZE_EXIT)
					{
						throw new IllegalArgumentException("Maze exit must be at the borders.");
					}
				}
			}
		}

		//Check if there are too many dragons
		int num_max_dragons = (int)Math.pow((lins-1)/2,2) - 3;
		if(dragons.size() > num_max_dragons) 
		{
			throw new IllegalArgumentException("Maze has too many dragons. Must be at most ((size-1)/2)^2 - 3.");
		}

		//Check if there isn't a hero
		if(hero == null)
		{
			throw new IllegalArgumentException("Maze must contain exactly one hero.");
		}

		//Check if there isn't a sword
		if(sword == null)
		{
			throw new IllegalArgumentException("Maze must contain exactly one sword.");
		}

		//Check if there isn't an exit
		if(!includesExit)
		{
			throw new IllegalArgumentException("Maze must contain an exit.");
		}

		//Check if there isn't a dragon next to the hero
		for(int i = 0; i < dragons.size(); i++)
		{
			Dragon dragon = dragons.get(i);
			if(hero.isAdjacent(dragon))
			{
				throw new IllegalArgumentException("Dragons must not begin adjacent to the hero.");
			}
		}

		//Generate the maze
		maze = new Maze(charMap);

		//Set the dragon mode
		dragonMode = dMode;
	}

	/**
	 * Checks if the hero is still alive.
	 *
	 * @return boolean that indicates if the hero is alive
	 */
	public boolean isHeroAlive()
	{
		return hero.isAlive();
	}

	/**
	 * Checks if a dragon is still alive.
	 *
	 * @return boolean that indicates if the dragon is alive
	 */
	public boolean isADragonAlive()
	{
		for(int i = 0; i < dragons.size(); i++)
		{
			Dragon dragon = dragons.get(i);
			if(dragon.isAlive())
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the hero is at the maze exit.
	 *
	 * @return boolean that indicates if the hero is at the exit
	 */
	public boolean isHeroAtExit()
	{
		int [] heroPos = hero.getPos();
		if(maze.getCell(heroPos[0], heroPos[1]) == Maze.CellType.EXIT)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Builds a bidimensional Game.Element array that consists of a copy of the maze and of the pieces' positions on the board.
	 *
	 * @return the maze board (2D Game.Element array) with all the pieces in place
	 */
	public Element[][] createElementArray()
	{
		//Create the array
		int lins = maze.getMazeLins();
		int cols = maze.getMazeCols();
		Element [][] elemArray = new Element[lins][cols];

		//Check the maze
		for(int y = 0; y < lins; y++)
		{
			for(int x = 0; x < cols; x++)
			{
				Maze.CellType type = maze.getCell(x,y);
				switch (type)
				{
				case NEUTRAL:
					elemArray[y][x] = Element.MAZE_NEUTRAL;
					break;
				case WALL:
					elemArray[y][x] = Element.MAZE_WALL;
					break;
				case EXIT:
					elemArray[y][x] = Element.MAZE_EXIT;
					break;
				}
			}
		}

		//Retrive the pieces' position
		int[] heroPos = hero.getPos();
		int[] swordPos = sword.getPos();

		//Check the hero
		if(hero.isAlive())
		{
			if(hero.isArmed()) 
			{
				elemArray[heroPos[1]][heroPos[0]] = Element.HERO_ARMED;
			}
			else
			{
				elemArray[heroPos[1]][heroPos[0]] = Element.HERO_UNARMED;
			}
		}

		//Check the sword
		if(!hero.isArmed())
		{
			elemArray[swordPos[1]][swordPos[0]] = Element.SWORD;
		}

		//Check the dragons
		for(int i = 0; i < dragons.size(); i++)
		{
			Dragon dragon = dragons.get(i);
			int[] dragonPos = dragon.getPos();
			if(dragon.isAlive())
			{
				if(dragon.isSleeping())
				{
					if(hero.isOver(dragon))
					{
						elemArray[dragonPos[1]][dragonPos[0]] = Element.HERO_OVER_DRAGON_SLEEPING;
					}
					else if(sword.isOver(dragon))
					{
						elemArray[dragonPos[1]][dragonPos[0]] = Element.DRAGON_SLEEPING_OVER_SWORD;
					}
					else //dragon is not over anything
					{
						elemArray[dragonPos[1]][dragonPos[0]] = Element.DRAGON_SLEEPING;
					}
				}
				else //if dragon is awake
				{
					if(sword.isOver(dragon))
					{
						elemArray[dragonPos[1]][dragonPos[0]] = Element.DRAGON_AWAKE_OVER_SWORD;
					}
					else
					{
						elemArray[dragonPos[1]][dragonPos[0]] = Element.DRAGON_AWAKE;
					}
				}
			}
		}

		return elemArray;
	}

	/**
	 * Moves the hero in the game
	 *
	 * @param  dir direction that the hero will move
	 * @return boolean that indicates if the hero has moved successfully on the chosen direction
	 */
	public boolean moveHero(Piece.Direction dir)
	{
		int [] heroPos = hero.getPos();
		CellType type;

		switch(dir)
		{
		case UP:
			if(heroPos[1] == 0) return false;
			else type = maze.getCell(heroPos[0],heroPos[1]-1);
			break;
		case DOWN:
			if(heroPos[1] == maze.getMazeLins() - 1) return false;
			else type = maze.getCell(heroPos[0],heroPos[1] + 1);
			break;
		case LEFT:
			if(heroPos[0] == 0) return false;
			else type = maze.getCell(heroPos[0] - 1,heroPos[1]);
			break;
		case RIGHT:
			if(heroPos[0] == maze.getMazeCols() - 1) return false;
			else type = maze.getCell(heroPos[0] + 1,heroPos[1]);
			break;
		default:
			return false;
		}

		switch(type)
		{
		case NEUTRAL:
			hero.move(dir);
			break;
		case WALL:
			return false;
		case EXIT:
			if(isADragonAlive()) return false;
			else hero.move(dir);
			break;
		default:
			return false;
		}
		return true;
	}

	/**
	 * Moves the dragon randomly in the game
	 *
	 * @param  dragon the dragon to be moved
	 * @return boolean that indicates if the dragon has moved successfully on the chosen direction
	 */
	public boolean moveDragon(Dragon dragon)
	{
		int action = Math.abs(rand.nextInt()) % 5;
		int [] dragonPos = dragon.getPos();

		switch(action)
		{
		case 0: //dragon doesnt move
			break;
		case 1: //dragon moves upwards
			if(dragonPos[1] != 0 && maze.getCell(dragonPos[0],dragonPos[1]-1) == Maze.CellType.NEUTRAL)
			{
				dragon.move(Piece.Direction.UP);
			}
			break;
		case 2: //dragon moves downwards
			if(dragonPos[1] != 0 && maze.getCell(dragonPos[0],dragonPos[1]+1) == Maze.CellType.NEUTRAL)
			{
				dragon.move(Piece.Direction.DOWN);
			}
			break;
		case 3: //dragon moves leftwards
			if(dragonPos[0] != 0 && maze.getCell(dragonPos[0]-1,dragonPos[1]) == Maze.CellType.NEUTRAL)
			{
				dragon.move(Piece.Direction.LEFT);
			}
			break;
		case 4: //dragon moves rightwards
			if(dragonPos[0] != 0 && maze.getCell(dragonPos[0]+1,dragonPos[1]) == Maze.CellType.NEUTRAL)
			{
				dragon.move(Piece.Direction.RIGHT);
			}
			break;
		}
		return true;
	}

	/**
	 * Randomly attempts to change a dragon's state(awake or asleep)
	 *
	 * @param  dragon the dragon whose state may change
	 */
	public void changeDragonState(Dragon dragon)
	{
		int decision = Math.abs(rand.nextInt()) % 4;

		if(decision == 0)
		{
			if(dragon.isSleeping())
			{
				dragon.awaken();
			}
			else
			{
				dragon.sleep();
			}
		}
		return;
	}

	/**
	 * Plays a game turn.
	 * <p>
	 * A game turn consists of moving the hero, checking it he is over the sword,
	 * moving each dragon (if they're awake) and changing their dragon state, and check if the a dragon is adjacent to the hero.
	 * <p>
	 * When the hero is adjacent to the dragon, if hero is armed, he kills the dragon.
	 * Else, if the dragon is awake, it will kill the hero.
	 *
	 * @param  heroDirection the direction the hero will move
	 */
	public void playTurn(Piece.Direction heroDirection)
	{
		//Move the hero
		switch(heroDirection)
		{
		case UP:
			moveHero(Piece.Direction.UP);
			break;
		case DOWN:
			moveHero(Piece.Direction.DOWN);
			break;
		case LEFT:
			moveHero(Piece.Direction.LEFT);
			break;
		case RIGHT:
			moveHero(Piece.Direction.RIGHT);
			break;
		}

		//Check if the hero is over the sword
		if(hero.isOver(sword) && !hero.isArmed())
		{
			hero.arm();
		}

		//Change each dragon state and move it
		for(int i = 0; i < dragons.size(); i++)
		{
			Dragon dragon = dragons.get(i);
			if(dragon.isAlive())
			{
				switch(dragonMode)
				{
				case IMMOBILE:
					break;
				case MOVING:
					moveDragon(dragon);
					break;
				case MOVING_SLEEPING:
					if(dragon.isSleeping())
					{
						changeDragonState(dragon);
					}
					else
					{
						moveDragon(dragon);
						changeDragonState(dragon);
					}
					break;
				}
			}

			//Check if the hero is next/over to the dragon
			if((hero.isAdjacent(dragon) || hero.isOver(dragon)) && dragon.isAlive())
			{
				if(hero.isArmed())
				{
					dragon.die();
				}
				else //if hero is unarmed
				{
					if(!dragon.isSleeping())
					{
						hero.die();
					}
				}
			}
		}
	}
	
	/**
	 * Getter method for the hero object of the game.
	 *
	 * @return the game's instance of the Hero class
	 */
	public Hero getHero()
	{
		return hero;
	}
	
	/**
	 * Getter method for get the first dragon object of the game 
	 * (used in games with only one dragon, for compatiblity reasons).
	 *
	 * @return one of the game's instances of the Dragon class
	 */
	public Dragon getDragon()
	{
		return getDragon(0);
	}
	
	/**
	 * Getter method for get a dragon object of the game.
	 *
	 * @param  index index of the dragon on the dragons array
	 * @return one of the game's instances of the Dragon class
	 */
	public Dragon getDragon(int index)
	{
		if(index < dragons.size())
		{
			return dragons.get(index);
		}
		else
		{
			return null;
		}
	}
	
	/**
	 * Getter method for get a sword object of the game.
	 *
	 * @return the game's instance of the Sword class
	 */
	public Sword getSword()
	{
		return sword;
	}
	
	/**
	 * Getter method for get a maze object of the game.
	 *
	 * @return the game's instance of the Maze class
	 */
	public Maze getMaze()
	{
		return maze;
	}
	/**
	 * Returns the number of dragons still alive in the game.
	 *
	 * @return the number of dragons still alive
	 */
	public int getDragonsAlive()
	{
		int dragonson=0;
		for(int i=0;i<dragons.size();i++)
		{
			Dragon dragon=dragons.get(i);
			if(dragon.isAlive())
			{
				dragonson++;
			}
		}
		return dragonson;
	}

}
