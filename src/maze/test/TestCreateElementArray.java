package maze.test;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

import maze.logic.Game;
import maze.logic.Piece;

public class TestCreateElementArray {
	final char [][] charMap = {{'X', 'X', 'X', 'X', 'X'},
			{'X', ' ', ' ', 'H', 'S'},
			{'X', ' ', 'X', ' ', 'X'},
			{'X', 'E', ' ', 'D', 'X'},
			{'X', 'X', 'X', 'X', 'X'}};
	
	final char [][] charMap2 = {{'X', 'X', 'X', 'X', 'X'},
			{'X', ' ', ' ', 'H', 'S'},
			{'X', ' ', 'X', 'X', 'X'},
			{'X', 'E', 'X', 'D', 'X'},
			{'X', 'X', 'X', 'X', 'X'}};
	
	final char [][] charMap3 = {{'X', 'X', 'X', 'X', 'X'},
			{'X', ' ', ' ', 'H', 'S'},
			{'X', ' ', 'X', 'X', 'X'},
			{'X', 'X', 'D', 'E', 'X'},
			{'X', 'X', 'X', 'X', 'X'}};

	@Test
	public void testElementArrayDimensions() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		Game.Element[][] elemArray = game.createElementArray();
		assertEquals(5, elemArray.length);
		for(int y = 0; y < 4; y++)
		{
			assertEquals(5, elemArray[y].length);
		}
	}

	@Test
	public void testRetrieveMazeCellElements() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		Game.Element[][] elemArray = game.createElementArray();
		assertEquals(Game.Element.MAZE_WALL, elemArray[0][0]);
		assertEquals(Game.Element.MAZE_NEUTRAL, elemArray[1][1]);
		assertEquals(Game.Element.MAZE_EXIT, elemArray[1][4]);
	}

	@Test
	public void testRetrieveBasicPieceElement() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		Game.Element[][] elemArray = game.createElementArray();
		assertEquals(Game.Element.HERO_UNARMED, elemArray[1][3]);
		assertEquals(Game.Element.DRAGON_AWAKE, elemArray[3][3]);
		assertEquals(Game.Element.SWORD, elemArray[3][1]);
	}
	
	@Test
	public void testRetrieveHeroOverSwordElement() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.DOWN);
		game.playTurn(Piece.Direction.DOWN);
		Game.Element[][] elemArray = game.createElementArray();
		assertEquals(Game.Element.HERO_ARMED, elemArray[3][1]);
	}
	
	@Test (timeout = 1000) //1 sec
	public void testRetriveDragonSleepingElement()
	{
		boolean testedDragonSleeping = false;
		while(!testedDragonSleeping)
		{
			Game game = new Game(Game.DragonMode.MOVING_SLEEPING,charMap2);
			game.playTurn(Piece.Direction.UP);
			if(game.getDragon().isSleeping())
			{
				Game.Element[][] elemArray = game.createElementArray();
				assertEquals(Game.Element.DRAGON_SLEEPING,elemArray[3][3]);
				testedDragonSleeping = true;
			}
		}
	}
	
	@Test (timeout = 1000) //1 sec
	public void testRetriveDragonAwakeOverSwordElement()
	{
		Game game = new Game(Game.DragonMode.MOVING,charMap3);
		final int[] newDragonPosRight = {3,3};
		while(!Arrays.equals(game.getDragon().getPos(),newDragonPosRight))
		{
			game.playTurn(Piece.Direction.RIGHT);
		}
		Game.Element[][] elemArray = game.createElementArray();
		assertEquals(Game.Element.DRAGON_AWAKE_OVER_SWORD, elemArray[3][3]);
	}
	
	@Test (timeout = 1000) //1 sec
	public void testRetriveDragonSleepingOverSwordElement()
	{
		final int[] newDragonPosRight = {3,3};
		boolean testedDragonSleepingOverSword = false;
		while(!testedDragonSleepingOverSword)
		{
			Game game = new Game(Game.DragonMode.MOVING_SLEEPING,charMap3);
			game.playTurn(Piece.Direction.RIGHT);
			if(game.getDragon().isSleeping() && Arrays.equals(game.getDragon().getPos(),newDragonPosRight))
			{
				Game.Element[][] elemArray = game.createElementArray();
				assertEquals(Game.Element.DRAGON_SLEEPING_OVER_SWORD,elemArray[3][3]);
				testedDragonSleepingOverSword = true;
			}
		}
	}
}


