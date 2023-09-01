package maze.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

import org.junit.Test;

import maze.logic.Game;
import maze.logic.Piece;

public class TestMazeWithStaticDragon {
	final int[] initialHeroPos = {3,1};
	final int[] initialDragonPos = {3,3};
	final int[] initialSwordPos = {1,3};
	final char [][] charMap = {{'X', 'X', 'X', 'X', 'X'},
			{'X', ' ', ' ', 'H', 'S'},
			{'X', ' ', 'X', ' ', 'X'},
			{'X', 'E', ' ', 'D', 'X'},
			{'X', 'X', 'X', 'X', 'X'}};

	@Test //a
	public void testMoveHeroToFreeCell() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		assertArrayEquals(initialHeroPos, game.getHero().getPos());

		game.playTurn(Piece.Direction.LEFT);
		int[] newHeroPos = {2,1};
		assertArrayEquals(newHeroPos, game.getHero().getPos());
	}
	@Test //b
	public void testMoveHeroToWallCell() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		assertArrayEquals(initialHeroPos, game.getHero().getPos());

		game.playTurn(Piece.Direction.UP);
		assertArrayEquals(initialHeroPos, game.getHero().getPos());
	}
	@Test //c
	public void testMoveHeroToSword() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);
		assertEquals(false, game.getHero().isArmed());
		assertArrayEquals(initialSwordPos, game.getSword().getPos());

		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.DOWN);
		game.playTurn(Piece.Direction.DOWN);
		assertEquals(true, game.getHero().isArmed());
	}
	@Test //d
	public void testHeroDies() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);		
		assertEquals(false, game.getHero().isArmed());

		game.playTurn(Piece.Direction.DOWN);
		assertEquals(false, game.isHeroAlive());
	}

	@Test //e
	public void testDragonDies() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);		
		assertEquals(false, game.getHero().isArmed());

		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.DOWN);
		game.playTurn(Piece.Direction.DOWN);
		assertEquals(true, game.getHero().isArmed());
		assertEquals(true, game.getDragon().isAlive());
		game.playTurn(Piece.Direction.RIGHT);
		assertEquals(true, game.isHeroAlive());
		assertEquals(false, game.getDragon().isAlive());
	}

	@Test //f
	public void testDragonDiesAndExit() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);		
		assertEquals(false, game.getHero().isArmed());

		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.DOWN);
		game.playTurn(Piece.Direction.DOWN);
		assertEquals(true, game.getHero().isArmed());
		assertEquals(true, game.getDragon().isAlive());
		game.playTurn(Piece.Direction.RIGHT);
		assertEquals(true, game.isHeroAlive());
		assertEquals(false, game.getDragon().isAlive());
		game.playTurn(Piece.Direction.RIGHT);
		game.playTurn(Piece.Direction.UP);
		game.playTurn(Piece.Direction.UP);
		game.playTurn(Piece.Direction.RIGHT);
		assertEquals(true, game.isHeroAtExit());
	}

	@Test //g
	public void testExitDragonAlive() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);		
		assertEquals(false, game.getHero().isArmed());

		assertEquals(false, game.isHeroAtExit());
		game.playTurn(Piece.Direction.RIGHT);
		assertEquals(false, game.isHeroAtExit());
	}

	@Test //g
	public void testExitArmedDragonAlive() 
	{
		Game game = new Game(Game.DragonMode.IMMOBILE,charMap);		
		assertEquals(false, game.getHero().isArmed());

		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.LEFT);
		game.playTurn(Piece.Direction.DOWN);
		game.playTurn(Piece.Direction.DOWN);
		assertEquals(true, game.getHero().isArmed());
		assertEquals(true, game.getDragon().isAlive());
		game.playTurn(Piece.Direction.UP);
		game.playTurn(Piece.Direction.UP);
		game.playTurn(Piece.Direction.RIGHT);
		game.playTurn(Piece.Direction.RIGHT);
		assertEquals(true, game.isHeroAlive());
		assertEquals(true, game.getDragon().isAlive());
		game.playTurn(Piece.Direction.RIGHT);
		assertEquals(true, game.getDragon().isAlive());
		assertEquals(false, game.isHeroAtExit());
	}
}


