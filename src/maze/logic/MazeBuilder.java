package maze.logic;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Deque;
import java.util.Random;

public class MazeBuilder {

	private Random rand;

	public class Point
	{
		private int x;
		private int y;

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
		int getX()
		{
			return x;
		}
		int getY()
		{
			return y;
		}
	}

	/**
	 * A constructor for the MazeBuilder class.
	 * <p>
	 * This class is used to randomly generate a maze. 
	 */
	public MazeBuilder()
	{		
		//Create the pseudo-random number generator
		rand = new Random();
		Date date = new Date();
		rand.setSeed(date.getTime());
	}
	/**
	 * Generates a maze with size x size dimensions and one dragon.
	 *
	 * @param  size the size of the maze
	 * @return 2D array of chars that represents the maze and the pieces' placement
	 */
	public char [][] buildMaze(int size)
	{
		return buildMaze(size,1);
	}

	/**
	 * Generates a maze with size x size dimensions and numDragons dragons.
	 *
	 * @param  size the size of the maze
	 * @param  numDragons number of dragon to put on the maze
	 * @return 2D array of chars that represents the maze and the pieces' placement 
	 */
	public char [][] buildMaze(int size, int numDragons)
	{
		//Check if the size is valid
		if((size < 5) || (size % 2 == 0))
		{
			return null;
		}

		//Check if the number of dragons is not excessive
		//(size-1)/2 is the number of cells on the visited array (to prevent adjacent Hero to a Dragon)
		//-2 is due to the sword and hero
		//-1 because on the worst case the hero is adjacent to two free cells on the spaces guarantied to be neutral (= those mapped to the visited cell array)
		int num_max_dragons = (int)Math.pow((size-1)/2,2) - 3;
		if(numDragons > num_max_dragons) 
		{
			return null;
		}

		//Alocate space for the new maze
		char[][] charMap = new char[size][size];

		//Convert map to a grid
		gridMap(charMap);

		//Place the exit on the map
		int[] positionBeforeExit = pickExit(charMap);

		//Create the visited cells array
		int visitedSize = (size-1)/2;
		boolean[][] visited = new boolean [visitedSize][visitedSize];

		//Place the positionBeforeExit on the visited cells array
		Point posOnVisited = new Point((positionBeforeExit[0]-1)/2,(positionBeforeExit[1]-1)/2);
		visited[posOnVisited.getY()][posOnVisited.getX()] = true;

		//Create the stack to keep a record of the path taken 
		Deque<Point> stack= new ArrayDeque<Point>();

		stack.push(posOnVisited);

		while(!stack.isEmpty())
		{
			//Retrieve the unvisited neighbours of the current point
			Point current = stack.peek();
			ArrayList<Point> unvisited = getUnvisitedNeighbours(visited, current);

			if(unvisited.size() >= 1) //if the current point has unvisited neighbours
			{
				//Randomly pick the next neighbour
				int dir = Math.abs(rand.nextInt()) % unvisited.size();
				Point neighbour = unvisited.get(dir);

				//Eliminate the wall common to both points
				int wallX;
				int wallY;
				int[] currentOnCharMap = {2*current.getX() + 1, 2*current.getY() + 1};
				int[] neighbourOnCharMap = {2*neighbour.getX() + 1, 2*neighbour.getY() + 1};
				if(neighbour.getX() == current.getX()) //if both points are in the same column
				{
					wallX = currentOnCharMap[0];
					wallY = (neighbourOnCharMap[1] + currentOnCharMap[1]) / 2;
				}
				else //if both points are on the same line
				{
					wallX = (neighbourOnCharMap[0] + currentOnCharMap[0]) / 2;
					wallY = currentOnCharMap[1];
				}
				charMap[wallY][wallX] = ' ';

				//Add the neighbour to the stack
				visited[neighbour.getY()][neighbour.getX()] = true;
				stack.push(neighbour);				
			}
			else //if the current point has no unvisited neighbours
			{
				stack.pop();
			}
		}

		//Place the pieces on the maze
		int numPlaced = 0;

		while(numPlaced < numDragons + 2) //total number of pieces = num of dragons + hero + sword (+2 for hero and sword)
		{
			Point p = new Point(rand.nextInt(size - 2) + 1, rand.nextInt(size - 2) + 1);

			//Check if the piece is on a neutral space
			if(isNeutral(charMap,p.getX(),p.getY()))
			{
				if(numPlaced == 0) //Placing the hero
				{
					charMap[p.getY()][p.getX()] = 'H';
					numPlaced++;
				}
				else if (numPlaced == 1) //Placing the sword
				{
					charMap[p.getY()][p.getX()] = 'E';
					numPlaced++;
				}
				else //numPlaced >= 2
				{
					//If the piece is a dragon, check if it is adjacent to the hero
					if(!isAdjacentToHero(charMap,p.getX(),p.getY()))
					{
						charMap[p.getY()][p.getX()] = 'D';
						numPlaced++;
					}
				}
			}
		}

		return charMap;
	}

	/**
	 * Uses its parameter to generate a 2D char array that represents a gridded maze (of walls and neutral spaces) with no pieces
	 * <p>
	 * The gridded array is used as a base to randomly generate a maze.
	 *
	 * @param  charMap 2D char array to be turned into a grid
	 */
	public void gridMap(char[][] charMap)
	{
		int lins = charMap.length;
		int cols = charMap[0].length;
		for(int y = 0; y < lins; y++)
		{
			for(int x = 0; x < cols; x++)
			{
				if((x % 2 == 1) && (y % 2 == 1))
				{
					charMap[y][x] = ' ';
				}
				else
				{
					charMap[y][x] = 'X';
				}
			}
		}
	}

	/**
	 * Randomly places the exit at the 2D char array parameter that represents a maze in construction
	 *
	 * @param  charMap 2D char array where the exit is to be placed
	 */
	public int[] pickExit(char[][] charMap)
	{
		int face = Math.abs(rand.nextInt()) % 4;
		int lins = charMap.length;
		int cols = charMap[0].length;
		int pos;
		int xBeforeExit = 0;
		int yBeforeExit = 0;
		switch(face)
		{
		case 0: //exit is on the top
			pos = (Math.abs(rand.nextInt()) % (cols - 1)/2);
			charMap[0][2*pos+1] = 'S';
			xBeforeExit = 2*pos+1;
			yBeforeExit = 1;
			break;
		case 1: //exit is on the left
			pos = (Math.abs(rand.nextInt()) % (lins - 1)/2);
			charMap[2*pos+1][0] = 'S';
			xBeforeExit = 1;
			yBeforeExit = 2*pos+1;
			break;
		case 2: //exit is on the bottom
			pos = (Math.abs(rand.nextInt()) % (cols - 1)/2);
			charMap[lins-1][2*pos+1] = 'S';
			xBeforeExit = 2*pos+1;
			yBeforeExit = lins - 2;
			break;
		case 3: //exit is on the right
			pos = (Math.abs(rand.nextInt()) % (lins - 1)/2);
			charMap[2*pos+1][cols-1] = 'S';
			xBeforeExit = cols - 2;
			yBeforeExit = 2*pos+1;
			break;
		}
		int[] positionBeforeExit = new int[]{xBeforeExit, yBeforeExit};
		return positionBeforeExit;
	}

	/**
	 * Retrieves all the unvisited neighbours cells of the 2D boolean array visited at point p.
	 *
	 * @param  visited 2D boolean array that represents which cells have been visited when randomly generating the maze
	 * @param  p point instance of the Point class that represents the coordinates to test for unvisited neighbours
	 * @return ArrayList of Points representing the coordinates of the unvisited neighbours
	 */
	public ArrayList<Point> getUnvisitedNeighbours(boolean[][] visited, Point p)
	{
		//Create the Point array
		ArrayList<Point> unvisited = new ArrayList<Point>();

		//Get Point p's coordinates
		int pX = p.getX();
		int pY = p.getY();

		//Get visited's dimensions
		int lins = visited.length;
		int cols = visited[0].length;

		//Check the northern neighbour
		if(pY >= 1)
		{
			if(visited[pY-1][pX] == false)
			{
				Point north = new Point(pX,pY-1);
				unvisited.add(north);
			}
		}

		//Check the western neighbour
		if(pX >= 1)
		{
			if(visited[pY][pX-1] == false)
			{
				Point west = new Point(pX-1,pY);
				unvisited.add(west);
			}
		}

		//Check the southern neighbour
		if(pY <= lins - 2)
		{
			if(visited[pY+1][pX] == false)
			{
				Point south = new Point(pX,pY+1);
				unvisited.add(south);
			}
		}

		//Check the eastern neighbour
		if(pX <= cols - 2)
		{
			if(visited[pY][pX+1] == false)
			{
				Point east = new Point(pX+1,pY);
				unvisited.add(east);
			}
		}

		return unvisited;
	}

	/**
	 * Checks if a cell is neutral.
	 * <p>
	 * A neutral cell is a cell that doesn't contain neither a wall or the exit, nor a piece.
	 *
	 * @param  charMap 2D char array representing the maze in construction (and its pieces)
	 * @param  x x coordinate of the cell to be tested
	 * @param  y y coordinate of the cell to be tested
	 * @return boolean that indicates if the cell is neutral
	 */
	public boolean isNeutral(char[][] charMap, int x, int y)
	{
		if(charMap[y][x] == ' ')
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	/**
	 * Checks if a cell is adjacent to the cell where the hero piece is placed.
	 *
	 * @param  charMap 2D char array representing the maze in construction (and its pieces)
	 * @param  x x coordinate of the cell to be tested
	 * @param  y y coordinate of the cell to be tested
	 * @return boolean that indicates if the hero is adjacent to that cell that was tested 
	 */
	public boolean isAdjacentToHero(char[][] charMap, int x, int y)
	{
		int lins = charMap.length;
		int cols = charMap[0].length;

		//Check above
		if(y >= 1)
		{
			if(charMap[y-1][x] == 'H')
			{
				return true;
			}
		}

		//Check on the left
		if(x >= 1)
		{
			if(charMap[y][x-1] == 'H')
			{
				return true;
			}
		}

		//Check below
		if(y <= lins-2)
		{
			if(charMap[y+1][x] == 'H')
			{
				return true;
			}
		}

		//Check on the right
		if(x <= cols-2)
		{
			if(charMap[y][x+1] == 'H')
			{
				return true;
			}
		}

		return false;
	}
}
