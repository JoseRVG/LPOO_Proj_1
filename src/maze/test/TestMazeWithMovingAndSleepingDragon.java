package maze.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import java.util.Arrays;

import org.junit.Test;

import maze.logic.Game;
import maze.logic.Piece;

public class TestMazeWithMovingAndSleepingDragon {
	final int[] initialHeroPos = {3,1};
	final int[] initialDragonPos = {3,3};
	final int[] initialSwordPos = {1,3};
	final char [][] charMap = {{'X', 'X', 'X', 'X', 'X'},
			{'X', ' ', ' ', 'H', 'S'},
			{'X', ' ', 'X', ' ', 'X'},
			{'X', 'E', ' ', 'D', 'X'},
			{'X', 'X', 'X', 'X', 'X'}};

	@Test (timeout = 5000) //1 sec
	public void testNoMoveSleepingDragon() 
	{
		boolean testedSleeping = false;
		while(!testedSleeping)
		{
			Game game = new Game(Game.DragonMode.MOVING_SLEEPING,charMap);
			assertArrayEquals(initialDragonPos, game.getDragon().getPos());
			game.playTurn(Piece.Direction.UP);
			int[] newDragonPos = game.getDragon().getPos();
			if(game.getDragon().isSleeping())
			{
				game.playTurn(Piece.Direction.UP);
				assertArrayEquals(newDragonPos,game.getDragon().getPos());
				testedSleeping = true;
			}
		}
	}
	
	@Test (timeout = 5000) //1 sec
	public void testNoDamagingSleepingDragon() 
	{
		boolean testedSleeping = false;
		
		final int[] newDragonPosUp = {3,2};

		while(!testedSleeping)
		{
			Game game = new Game(Game.DragonMode.MOVING_SLEEPING,charMap);
			assertArrayEquals(initialHeroPos, game.getHero().getPos());
			assertArrayEquals(initialDragonPos, game.getDragon().getPos());
			game.playTurn(Piece.Direction.DOWN);
			if(game.getDragon().isSleeping() && Arrays.equals(newDragonPosUp,game.getDragon().getPos()))
			{
				assertEquals(true,game.isHeroAlive());
				testedSleeping = true;
			}
		}
	}
}


