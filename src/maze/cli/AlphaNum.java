package maze.cli;

import java.util.Scanner;

import maze.logic.Game;
import maze.logic.Piece;

public class AlphaNum {
	private Game game;
	private final char UP_KEY = 'w';
	private final char DOWN_KEY = 's';
	private final char LEFT_KEY = 'a';
	private final char RIGHT_KEY = 'd';


	public static void main(String args[])
	{
		AlphaNum myAlphaNum = new AlphaNum();
		myAlphaNum.run();
	}

	public void run()
	{
		System.out.println("Maze game");

		//Parameters for first week version
		char[][] charMap = {{'X','X','X','X','X','X','X','X','X','X'},
							{'X','H',' ',' ',' ',' ',' ',' ',' ','X'},
							{'X',' ','X','X',' ','X',' ','X',' ','X'},
							{'X','D','X','X',' ','X',' ','X',' ','X'},
							{'X',' ','X','X',' ','X',' ','X',' ','X'},
							{'X',' ',' ',' ',' ',' ',' ','X',' ','S'},
							{'X',' ','X','X',' ','X',' ','X',' ','X'},
							{'X',' ','X','X',' ','X',' ','X',' ','X'},
							{'X','E','X','X',' ',' ',' ',' ',' ','X'},
							{'X','X','X','X','X','X','X','X','X','X'}};

		//Create the scanner object
		Scanner scan = new Scanner(System.in);

		//Ask for the dragon mode
		boolean validInput = false;
		String str;
		while(!validInput)
		{
			System.out.println("How would you like the dragon to behave?");
			System.out.println("1- Immobile dragon.");
			System.out.println("2- Moving dragon.");
			System.out.println("3- Moving and sleeping dragon.");

			//Read input
			str = scan.nextLine();

			if(str.length() >= 1)
			{
				switch(str.charAt(0))
				{
				case '1': 
					game = new Game(Game.DragonMode.IMMOBILE,11,16);
					validInput = true;
					break;
				case '2': 
					game = new Game(Game.DragonMode.MOVING,11,6);
					validInput = true;
					break;
				case '3': 
					game = new Game(Game.DragonMode.MOVING_SLEEPING,11,6);
					validInput = true;
					break;
				}
			}

		}

		//Main game cycle
		while(game.isHeroAlive() && !game.isHeroAtExit())
		{
			//Print out the maze
			printMaze();

			//Read input
			str = scan.nextLine();

			if(str.length() >= 1)
			{
				//Move the hero
				switch(str.charAt(0))
				{
				case UP_KEY:
					game.playTurn(Piece.Direction.UP);
					break;
				case DOWN_KEY:
					game.playTurn(Piece.Direction.DOWN);
					break;
				case LEFT_KEY:
					game.playTurn(Piece.Direction.LEFT);
					break;
				case RIGHT_KEY:
					game.playTurn(Piece.Direction.RIGHT);
					break;
				default:
					continue;
				}
			}
			System.out.println();
		}
		if(!game.isHeroAlive())
		{
			printMaze();
			System.out.println("The hero has been slain by the dragon. Game over...");
		}
		else if(game.isHeroAtExit())
		{
			printMaze();
			System.out.println("The hero has slain the dragon and has left the maze. You won!");
		}
		scan.close();
	}

	public char getElementChar(Game.Element elem)
	{
		char elementChar;
		switch(elem)
		{
		case MAZE_NEUTRAL:
			elementChar = ' ';
			break;
		case MAZE_WALL:
			elementChar = 'X';
			break;
		case MAZE_EXIT:
			elementChar = 'S';
			break;
		case HERO_UNARMED:
			elementChar = 'H';
			break;
		case HERO_ARMED:
			elementChar = 'A';
			break;
		case DRAGON_AWAKE:
			elementChar = 'D';
			break;
		case DRAGON_SLEEPING:
			elementChar = 'd';
			break;
		case HERO_OVER_DRAGON_SLEEPING:
			elementChar = 'o';
			break;
		case SWORD:
			elementChar = 'E';
			break;
		case DRAGON_AWAKE_OVER_SWORD:
			elementChar = 'F';
			break;
		case DRAGON_SLEEPING_OVER_SWORD:
			elementChar = 'f';
			break;
		default:
			elementChar = '?';
			break;
		}
		return elementChar;
	}

	public void printMaze()
	{
		Game.Element[][] elemArray = game.createElementArray();
		int lins = elemArray.length;
		int cols = elemArray[0].length;

		for(int y = 0; y < lins; y++)
		{
			for(int x = 0; x < cols; x++)
			{
				Game.Element elem = elemArray[y][x];
				System.out.format(" %c ", getElementChar(elem));
			}
			System.out.println();
		}
	}
}
