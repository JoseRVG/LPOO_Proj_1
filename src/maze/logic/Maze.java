package maze.logic;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Maze implements Serializable{
	public enum CellType {NEUTRAL, WALL, EXIT}

	private CellType[][] mazeMap;

	public Maze(char[][] charMap)
	{
		//Allocate space for the 2D array
		if(charMap.length > 0)
		{
			int lines = charMap.length;
			int cols = charMap[0].length;
			mazeMap = new CellType[lines][cols];	
			
			for(int y = 0; y < lines; y++)
			{
				for(int x = 0; x < cols; x++)
				{
					char elem = charMap[y][x];
					switch(elem)
					{
					case ' ':
						mazeMap[y][x] = CellType.NEUTRAL;
						break;
					case 'X':
						mazeMap[y][x] = CellType.WALL;
						break;
					case 'S':
						mazeMap[y][x] = CellType.EXIT;
						break;
					}
				}
			}
		}
	}

	public CellType getCell(int x, int y)
	{
		return mazeMap[y][x];
	}
	
	public int getMazeLins()
	{
		return mazeMap.length;
	}
	
	public int getMazeCols()
	{
		if(mazeMap.length == 0)
		{
			return 0;
		}
		else
		{
			return mazeMap[0].length;
		}
	}
}
