package maze.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.fail;

import java.util.Arrays;

import org.junit.Test;

import maze.logic.Game;
import maze.logic.Piece;

public class TestMazeWithMovingDragon {
	final int[] initialHeroPos = {3,1};
	final int[] initialDragonPos = {3,3};
	final int[] initialSwordPos = {1,3};
	final char [][] charMap = {{'X', 'X', 'X', 'X', 'X'},
			{'X', ' ', ' ', 'H', 'S'},
			{'X', ' ', 'X', ' ', 'X'},
			{'X', 'E', ' ', 'D', 'X'},
			{'X', 'X', 'X', 'X', 'X'}};


	@Test (timeout = 1000) //1 sec
	public void testMoveDragonToFreeCell() 
	{
		Game game = new Game(Game.DragonMode.MOVING,charMap);

		final int[] newDragonPosLeft = {2,3};
		final int[] newDragonPosUp = {3,2};

		boolean testedNoMove = false;
		boolean testedMoveLeft = false;
		boolean testedMoveUp = false;

		while(!testedNoMove || !testedMoveLeft || !testedMoveUp)
		{
			assertArrayEquals(initialDragonPos, game.getDragon().getPos());
			game.playTurn(Piece.Direction.UP);
			if(Arrays.equals(initialDragonPos, game.getDragon().getPos()))
			{
				testedNoMove = true;
			}
			else if (Arrays.equals(newDragonPosLeft, game.getDragon().getPos()))
			{
				testedMoveLeft = true;
			}
			else if (Arrays.equals(newDragonPosUp, game.getDragon().getPos()))
			{
				testedMoveUp = true;
			}
			else
			{
				fail("Dragon moved to invalid position.");
			}
			game.getDragon().setPos(initialDragonPos);
		}
	}
}


