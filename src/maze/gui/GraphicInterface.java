package maze.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Objects;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import maze.logic.Game;
import maze.logic.Game.DragonMode;
import maze.logic.Piece;

public class GraphicInterface {
	private Game game;

	private JFrame frmHost;
	private JLabel lblMazeSize;
	private JTextField fldMazeSize;
	private JLabel lblNumDragons;
	private JTextField fldNumDragons;
	private JLabel lblMode;
	private JComboBox<Game.DragonMode> cmbMode;
	private JButton btnPlay;
	private JButton btnCreate;
	private JButton btnDiscard;
	private JButton btnExitHost;
	private JPanel pnlMazeCreator;
	private JLabel lblLoadFileName;
	private JTextField fldLoadFileName;
	private JButton btnLoad;
	private JLabel lblBackgroundHost;

	private JFrame frmPlay;
	private JButton btnUP;
	private JButton btnLEFT;
	private JButton btnRIGHT;
	private JButton btnDOWN;
	private JLabel lblGameStatus;
	private JButton btnExitPlay;
	private JPanel pnlMaze;
	private JLabel lblSaveFileName;
	private JTextField fldSaveFileName;
	private JButton btnSave;
	private JButton btnDragonHead;
	private JLabel lblDragonsAlive;
	private JLabel lblBackgroundPlay;

	private boolean invisibleDragons;
	private Game.Element[][] elemMap;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GraphicInterface window = new GraphicInterface();
					window.frmHost.setVisible(true);
					window.frmPlay.setVisible(false);
					window.frmHost.requestFocus();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GraphicInterface() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		invisibleDragons = false;

		/* frmHost */
		frmHost = new JFrame();
		frmHost.setTitle("Maze Game");
		frmHost.setBounds(100, 100, 700, 600);
		frmHost.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmHost.setResizable(false);

		lblMazeSize = new JLabel("Maze Size");
		lblMazeSize.setForeground(Color.BLACK);
		lblMazeSize.setBackground(new Color(245, 255, 250));
		lblMazeSize.setBounds(10, 11, 67, 14);
		lblMazeSize.setOpaque(true);

		fldMazeSize = new JTextField();
		fldMazeSize.setBounds(111, 8, 53, 20);
		fldMazeSize.setColumns(10);

		lblNumDragons = new JLabel("N\u00BA Dragons");
		lblNumDragons.setBounds(10, 36, 67, 14);
		lblNumDragons.setOpaque(true);

		fldNumDragons = new JTextField();
		fldNumDragons.setBounds(111, 33, 53, 20);
		fldNumDragons.setColumns(10);

		lblMode = new JLabel("Mode");
		lblMode.setBounds(10, 61, 46, 14);
		lblMode.setOpaque(true);

		cmbMode = new JComboBox<Game.DragonMode>();
		cmbMode.setBounds(111, 64, 151, 20);
		cmbMode.setModel(new DefaultComboBoxModel<Game.DragonMode>(DragonMode.values()));
		cmbMode.setSelectedIndex(0);

		btnPlay = new JButton("");
		btnPlay.setBounds(338, 36, 40, 40);
		btnPlay.setIcon(new ImageIcon("images/icons/play.png"));
		btnPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					if(elemMap == null) //random maze
					{
						int size = Integer.parseInt(fldMazeSize.getText());
						int nDragons = Integer.parseInt(fldNumDragons.getText());
						Game.DragonMode dMode = (Game.DragonMode)cmbMode.getSelectedItem();
						game = new Game(dMode,size,nDragons);
						startGame(game);
					}
					else //created maze
					{
						Game.DragonMode dMode = (Game.DragonMode)cmbMode.getSelectedItem();
						game = new Game(dMode,elemMap);
						startGame(game);
					}
				}
				catch(IllegalArgumentException ex)
				{
					String msg = ex.getMessage();
					if(Objects.equals("For input string: \"\"",msg))
					{
						JOptionPane.showMessageDialog(frmHost, "Some fields are empty.");
					}
					else
					{
						JOptionPane.showMessageDialog(frmHost, msg);
					}
					endGame();
					return;
				}
			}
		});

		btnCreate = new JButton("");
		btnCreate.setBounds(420, 186, 40, 40);
		btnCreate.setIcon(new ImageIcon("images/icons/hammer.png"));
		btnCreate.setHorizontalTextPosition(SwingConstants.CENTER);
		btnCreate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmHost.requestFocus();
				try
				{
					int size = Integer.parseInt(fldMazeSize.getText());
					elemMap = initializeElemMap(size);
					pnlMazeCreator.repaint();
				}
				catch(IllegalArgumentException ex)
				{
					String msg = ex.getMessage();
					if(Objects.equals("For input string: \"\"",msg))
					{
						JOptionPane.showMessageDialog(frmHost, "Some fields are empty.");
					}
					else
					{
						JOptionPane.showMessageDialog(frmHost, msg);
					}
					endGame();
					return;
				}
			}
		});

		btnDiscard = new JButton("");
		btnDiscard.setBounds(420, 236, 40, 40);
		btnDiscard.setIcon(new ImageIcon("images/icons/discard.png"));
		btnDiscard.setHorizontalTextPosition(SwingConstants.CENTER);
		btnDiscard.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmHost.requestFocus();
				elemMap = null;
				pnlMazeCreator.repaint();
			}
		});

		btnExitHost = new JButton("");
		btnExitHost.setBounds(397, 36, 40, 40);
		btnExitHost.setBackground(SystemColor.inactiveCaptionBorder);
		btnExitHost.setIcon(new ImageIcon("images/icons/exit.png"));
		btnExitHost.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExitHost.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				System.exit(0);
			}
		});

		pnlMazeCreator = new MazeCreatorPanel();
		pnlMazeCreator.setBounds(10, 100, 400, 400);

		lblLoadFileName = new JLabel("Save file name");
		lblLoadFileName.setBounds(380, 11, 100, 14);
		lblLoadFileName.setOpaque(true);

		fldLoadFileName = new JTextField();
		fldLoadFileName.setBounds(500, 8, 100, 20);
		fldLoadFileName.setColumns(10);

		btnLoad = new JButton("");
		btnLoad.setIcon(new ImageIcon("images/icons/load.png"));
		btnLoad.setBounds(500, 36, 40, 40);
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmHost.requestFocus();
				ObjectInputStream is = null;
				try
				{
					is = new ObjectInputStream(new FileInputStream(fldLoadFileName.getText()));
					game = (Game)is.readObject();
					is.close();
					startGame(game);
				}
				catch(IOException ex)
				{
					JOptionPane.showMessageDialog(frmHost, "Game could not be loaded from the chosen file.");
				}
				catch(ClassNotFoundException ex)
				{
					JOptionPane.showMessageDialog(frmHost, "Logic game package is missing!");
				}
				catch(IllegalArgumentException ex)
				{
					String msg = ex.getMessage();
					if(Objects.equals("For input string: \"\"",msg))
					{
						JOptionPane.showMessageDialog(frmHost, "Some fields are empty.");
					}
					else
					{
						JOptionPane.showMessageDialog(frmHost, msg);
					}
					endGame();
					return;
				}
			}
		});

		lblBackgroundHost = new JLabel("");
		lblBackgroundHost.setHorizontalAlignment(SwingConstants.CENTER);
		lblBackgroundHost.setIcon(new ImageIcon("images/backgrounds/garden-maze.jpg"));
		lblBackgroundHost.setBounds(0, 0, 700, 600);

		//Add all the components to the frame
		frmHost.getContentPane().setLayout(null);
		frmHost.getContentPane().add(lblMazeSize);
		frmHost.getContentPane().add(fldMazeSize);
		frmHost.getContentPane().add(lblNumDragons);
		frmHost.getContentPane().add(fldNumDragons);
		frmHost.getContentPane().add(lblMode);
		frmHost.getContentPane().add(cmbMode);
		frmHost.getContentPane().add(btnPlay);
		frmHost.getContentPane().add(btnCreate);
		frmHost.getContentPane().add(btnDiscard);
		frmHost.getContentPane().add(btnExitHost);
		frmHost.getContentPane().add(pnlMazeCreator);
		frmHost.getContentPane().add(lblLoadFileName);
		frmHost.getContentPane().add(fldLoadFileName);
		frmHost.getContentPane().add(btnLoad);
		frmHost.getContentPane().add(lblBackgroundHost);

		/* frmPlay */
		frmPlay = new JFrame();
		frmPlay.setTitle("Adventure time!");
		frmPlay.setBounds(100, 100, 700, 600);
		frmPlay.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmPlay.setResizable(false);
		frmPlay.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent e) {
				if(game != null)
				{
					if(game.isHeroAlive() && !game.isHeroAtExit())
					{
						switch(e.getKeyCode())
						{
						case KeyEvent.VK_LEFT: 
							lblGameStatus.setText("You have moved the hero left.");
							processArrowButtonPress(Piece.Direction.LEFT); 
							break;

						case KeyEvent.VK_RIGHT: 
							lblGameStatus.setText("You have moved the hero right.");
							processArrowButtonPress(Piece.Direction.RIGHT);
							break;

						case KeyEvent.VK_UP: 
							lblGameStatus.setText("You have moved the hero up.");
							processArrowButtonPress(Piece.Direction.UP);
							break;

						case KeyEvent.VK_DOWN: 
							lblGameStatus.setText("You have moved the hero down.");
							processArrowButtonPress(Piece.Direction.DOWN);
							break;
						}
					}
				}
			}
			// Ignored keyboard events
			public void keyReleased(KeyEvent e) {}
			public void keyTyped(KeyEvent e) {}
		});

		btnUP = new JButton("");
		btnUP.setIcon(new ImageIcon("images/icons/up.png"));
		btnUP.setBounds(516, 124, 77, 70);
		btnUP.setEnabled(false);
		btnUP.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmPlay.requestFocus();
				lblGameStatus.setText("You have moved the hero up.");
				processArrowButtonPress(Piece.Direction.UP);
			}
		});

		btnLEFT = new JButton("");
		btnLEFT.setIcon(new ImageIcon("images/icons/left.png"));
		btnLEFT.setBounds(450, 156, 67, 70);
		btnLEFT.setEnabled(false);
		btnLEFT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmPlay.requestFocus();
				lblGameStatus.setText("You have moved the hero left.");
				processArrowButtonPress(Piece.Direction.LEFT);
			}
		});

		btnRIGHT = new JButton("");
		btnRIGHT.setIcon(new ImageIcon("images/icons/right.png"));
		btnRIGHT.setBounds(592, 156, 73, 70);
		btnRIGHT.setEnabled(false);
		btnRIGHT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmPlay.requestFocus();
				lblGameStatus.setText("You have moved the hero right.");
				processArrowButtonPress(Piece.Direction.RIGHT);
			}
		});

		btnDOWN = new JButton("");
		btnDOWN.setIcon(new ImageIcon("images/icons/down.png"));
		btnDOWN.setBounds(516, 193, 77, 70);
		btnDOWN.setEnabled(false);
		btnDOWN.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmPlay.requestFocus();
				lblGameStatus.setText("You have moved the hero down.");
				processArrowButtonPress(Piece.Direction.DOWN);
			}
		});

		lblGameStatus = new JLabel("Ready to generate a new maze!");
		lblGameStatus.setBounds(20, 520, 380, 14);
		lblGameStatus.setOpaque(true);

		btnExitPlay = new JButton("");
		btnExitPlay.setBounds(397, 36, 46, 39);
		btnExitPlay.setBackground(SystemColor.inactiveCaptionBorder);
		btnExitPlay.setIcon(new ImageIcon("images/icons/exit.png"));
		btnExitPlay.setHorizontalTextPosition(SwingConstants.CENTER);
		btnExitPlay.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				endGame();
				frmHost.requestFocus();
			}
		});

		pnlMaze = new MazePlayPanel();
		pnlMaze.setBounds(10, 100, 400, 400);

		lblSaveFileName = new JLabel("Save file name");
		lblSaveFileName.setBounds(380, 11, 100, 14);
		lblSaveFileName.setOpaque(true);

		fldSaveFileName = new JTextField();
		fldSaveFileName.setBounds(500, 8, 100, 20);
		fldSaveFileName.setColumns(10);

		btnSave = new JButton("");
		btnSave.setIcon(new ImageIcon("images/icons/save.png"));
		btnSave.setBounds(500, 36, 40, 40);
		btnSave.setEnabled(false);
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmPlay.requestFocus();
				ObjectOutputStream os = null;
				try
				{
					os = new ObjectOutputStream(new FileOutputStream(fldSaveFileName.getText()));
					os.writeObject(game);
					os.close();
					JOptionPane.showMessageDialog(frmPlay, "Game saved successfully!");
				}
				catch(IOException ex)
				{
					JOptionPane.showMessageDialog(frmPlay, "Game could not be saved to the chosen file.");
				}
			}
		});

		btnDragonHead = new JButton();
		btnDragonHead.setIcon(new ImageIcon("images/icons/dragon head.png"));
		btnDragonHead.setBounds(20, 32, 50, 50);
		btnDragonHead.setHorizontalAlignment(SwingConstants.CENTER);
		btnDragonHead.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				frmPlay.requestFocus();
				invisibleDragons = !invisibleDragons;
				pnlMaze.repaint();
			}
		});

		lblDragonsAlive = new JLabel();
		lblDragonsAlive.setBounds(90, 40, 50, 14);
		lblDragonsAlive.setHorizontalTextPosition(SwingConstants.CENTER);
		lblDragonsAlive.setOpaque(true);

		lblBackgroundPlay = new JLabel("");
		lblBackgroundPlay.setHorizontalAlignment(SwingConstants.CENTER);
		lblBackgroundPlay.setIcon(new ImageIcon("images/backgrounds/labyrinth.png"));
		lblBackgroundPlay.setBounds(0, 0, 700, 600);

		//Add all the components to the frame
		frmPlay.getContentPane().setLayout(null);
		frmPlay.getContentPane().add(btnUP);
		frmPlay.getContentPane().add(btnLEFT);
		frmPlay.getContentPane().add(btnRIGHT);
		frmPlay.getContentPane().add(btnDOWN);
		frmPlay.getContentPane().add(lblGameStatus);
		frmPlay.getContentPane().add(btnExitPlay);
		frmPlay.getContentPane().add(pnlMaze);
		frmPlay.getContentPane().add(lblSaveFileName);
		frmPlay.getContentPane().add(fldSaveFileName);
		frmPlay.getContentPane().add(btnSave);
		frmPlay.getContentPane().add(btnDragonHead);
		frmPlay.getContentPane().add(lblDragonsAlive);
		frmPlay.getContentPane().add(lblBackgroundPlay);
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

	public String getMazeString()
	{
		Game.Element[][] elemArray = game.createElementArray();
		int lins = elemArray.length;
		int cols = elemArray[0].length;
		StringBuilder mazeString = new StringBuilder();

		for(int y = 0; y < lins; y++)
		{
			for(int x = 0; x < cols; x++)
			{
				Game.Element elem = elemArray[y][x];
				String elemStr = String.format(" %c ", getElementChar(elem));
				mazeString.append(elemStr);
			}
			mazeString.append("\n");
		}

		return mazeString.toString();
	}

	public void processArrowButtonPress(Piece.Direction dir)
	{
		//Move the hero
		game.playTurn(dir);
		lblDragonsAlive.setText(game.getDragonsAlive()+"");
		if(game.getDragonsAlive() == 0)
		{
			lblDragonsAlive.setBackground(Color.GREEN);
		}
		if(game.isHeroAlive() && !game.isHeroAtExit())
		{
			//Print out the maze
			//textArea.setText(getMazeString());
			pnlMaze.repaint();
		}
		else if(game.isHeroAtExit())
		{
			lblGameStatus.setText("Congratulations! You slayed every dragon and found the exit!");
			btnUP.setEnabled(false);
			btnLEFT.setEnabled(false);
			btnDOWN.setEnabled(false);
			btnRIGHT.setEnabled(false);
			btnSave.setEnabled(false);
			btnDragonHead.setEnabled(false);
		}
		else //game.isHeroAlive() == false
		{
			lblGameStatus.setText("Game over. You have been slayed by a dragon...");
			btnUP.setEnabled(false);
			btnLEFT.setEnabled(false);
			btnDOWN.setEnabled(false);
			btnRIGHT.setEnabled(false);
			btnSave.setEnabled(false);
			btnDragonHead.setEnabled(false);
		}
	}

	public void startGame(Game game)
	{
		frmHost.setVisible(false);
		frmPlay.setVisible(true);
		frmPlay.requestFocus();
		btnUP.setEnabled(true);
		btnLEFT.setEnabled(true);
		btnDOWN.setEnabled(true);
		btnRIGHT.setEnabled(true);
		btnExitPlay.setEnabled(true);
		btnSave.setEnabled(true);
		pnlMaze.repaint();
		lblGameStatus.setText("You can now move the hero with the arrow keys!");
		lblDragonsAlive.setText(game.getDragonsAlive()+"");
		if(game.getDragonsAlive() == 0)
		{
			lblDragonsAlive.setBackground(Color.GREEN);
		}
		else
		{
			lblDragonsAlive.setBackground(Color.RED);
		}
		invisibleDragons = false;
	}

	public void endGame()
	{
		game = null;
		frmHost.setVisible(true);
		frmPlay.setVisible(false);
		frmHost.requestFocus();
		btnUP.setEnabled(false);
		btnLEFT.setEnabled(false);
		btnDOWN.setEnabled(false);
		btnRIGHT.setEnabled(false);
		btnExitPlay.setEnabled(false);
		btnSave.setEnabled(false);
		pnlMaze.repaint();
	}

	public JLabel getLblMazeSize() {
		return lblMazeSize;
	}
	public JLabel getLblNumDragons() {
		return lblNumDragons;
	}
	public JLabel getLblMode() {
		return lblMode;
	}
	public JLabel getLblGameStatus() {
		return lblGameStatus;
	}

	public Game.Element[][] initializeElemMap(int size) 
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

		elemMap = new Game.Element[size][size];

		for (int y = 0; y < size; y++) 
		{
			for(int x = 0; x < size; x++)
			{
				if(y == 0 || y == size - 1 || x == 0 || x == size - 1)
				{
					elemMap[y][x] = Game.Element.MAZE_WALL;
				}
				else
				{
					elemMap[y][x] = Game.Element.MAZE_NEUTRAL;
				}
			}
		}
		return elemMap;
	}

	@SuppressWarnings("serial")
	public abstract class MazePanel extends JPanel 
	{
		// in-memory representation of an example image to be displayed in the screen
		private BufferedImage floor;
		private BufferedImage wall;
		private BufferedImage closed_exit;
		private BufferedImage exit;
		private BufferedImage hero_unarmed;
		private BufferedImage hero_armed;
		private BufferedImage dragon_awake;
		private BufferedImage dragon_sleeping;
		private BufferedImage sword;

		// Constructor. Initiates listeners, 
		public MazePanel()
		{
			try {
				floor = ImageIO.read(new File("images/floor.png"));
				wall = ImageIO.read(new File("images/wall.jpg"));
				closed_exit = ImageIO.read(new File("images/closed.png"));
				exit = ImageIO.read(new File("images/exit.png"));
				hero_unarmed = ImageIO.read(new File("images/hero_unarmed.png"));
				hero_armed = ImageIO.read(new File("images/hero_armed.png"));
				dragon_awake = ImageIO.read(new File("images/dragon_awake.png"));
				dragon_sleeping = ImageIO.read(new File("images/dragon_sleeping.png"));
				sword = ImageIO.read(new File("images/sword.png"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g); // clears the backgorund ...
		}

		public void paintMaze(Graphics g, Game.Element[][] elemArray)
		{
			int lins = elemArray.length;
			int cols = elemArray[0].length;

			int pnlWidth = this.getWidth();
			int pnlHeight = this.getHeight();
			int cellWidth = pnlWidth/cols;
			int cellHeight = pnlHeight/lins;

			for(int y = 0; y < lins; y++)
			{
				for(int x = 0; x < cols; x++)
				{
					Game.Element elem = elemArray[y][x];
					switch(elem)
					{
					case MAZE_WALL:
						g.drawImage(wall, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, wall.getWidth(), wall.getHeight(), null);
						break;
					case MAZE_EXIT:
						if(game != null && game.getDragonsAlive() == 0)
						{
							g.drawImage(exit, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, exit.getWidth(), exit.getHeight(), null);
						}
						else
						{
							g.drawImage(closed_exit, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, closed_exit.getWidth(), closed_exit.getHeight(), null);
						}
						break;
					case HERO_UNARMED:
						g.drawImage(hero_unarmed, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, hero_unarmed.getWidth(), hero_unarmed.getHeight(), null);
						break;
					case HERO_ARMED:
						g.drawImage(hero_armed, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, hero_armed.getWidth(), hero_armed.getHeight(), null);
						break;
					case DRAGON_AWAKE:
						if(!invisibleDragons)
						{
							g.drawImage(dragon_awake, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, dragon_awake.getWidth(), dragon_awake.getHeight(), null);
						}
						else
						{
							g.drawImage(floor, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, floor.getWidth(), floor.getHeight(), null);
						}
						break;
					case DRAGON_SLEEPING:
						if(!invisibleDragons)
						{
							g.drawImage(dragon_sleeping, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, dragon_sleeping.getWidth(), dragon_sleeping.getHeight(), null);
						}
						else
						{
							g.drawImage(floor, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, floor.getWidth(), floor.getHeight(), null);
						}
						break;
					case HERO_OVER_DRAGON_SLEEPING:
						if(!invisibleDragons)
						{
							g.drawImage(dragon_sleeping, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, dragon_sleeping.getWidth(), dragon_sleeping.getHeight(), null);
						}
						g.drawImage(hero_unarmed, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, hero_unarmed.getWidth(), hero_unarmed.getHeight(), null);
						break;
					case SWORD:
						g.drawImage(sword, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, sword.getWidth(), sword.getHeight(), null);
						break;
					case DRAGON_AWAKE_OVER_SWORD:
						g.drawImage(sword, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, sword.getWidth(), sword.getHeight(), null);
						if(!invisibleDragons)
						{
							g.drawImage(dragon_awake, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, dragon_awake.getWidth(), dragon_awake.getHeight(), null);
						}
						break;
					case DRAGON_SLEEPING_OVER_SWORD:
						g.drawImage(sword, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, sword.getWidth(), sword.getHeight(), null);
						if(!invisibleDragons)
						{
							g.drawImage(dragon_sleeping, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, dragon_sleeping.getWidth(), dragon_sleeping.getHeight(), null);
						}
						break;
					default: //MAZE_NEUTRAL
						g.drawImage(floor, x*cellWidth, y*cellHeight, x*cellWidth + cellWidth - 1, y*cellHeight + cellHeight - 1, 0, 0, floor.getWidth(), floor.getHeight(), null);
						break;
					}
				}
			}
		}
	}

	@SuppressWarnings("serial")
	public class MazePlayPanel extends MazePanel
	{
		// Constructor. Initiates listeners, 
		public MazePlayPanel()
		{
			super();
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g); // clears the backgorund ...
			if(game != null) //a game is in progress
			{
				System.out.println("Maze printed...");
				paintMaze(g,game.createElementArray());
			}
		}
	}

	@SuppressWarnings("serial")
	public class MazeCreatorPanel extends MazePanel
	{
		public MazeCreatorPanel()
		{
			super();

			MazeCreatorPanel panel = this;

			addMouseListener(new MouseListener()
			{
				public void mousePressed(MouseEvent e) {
					if(elemMap != null)
					{
						int mouseX = e.getX();
						int mouseY = e.getY();

						int pnlWidth = panel.getWidth();
						int pnlHeight = panel.getHeight();

						int lins = elemMap.length;
						int cols = elemMap[0].length;

						int cellWidth = pnlWidth/cols;
						int cellHeight = pnlHeight/lins;

						//Check over which element the mouse currently is
						int x = mouseX/cellWidth;
						int y = mouseY/cellHeight;

						Game.Element elem = elemMap[y][x];

						ArrayList<Game.Element> elems = new ArrayList<Game.Element>();
						elems.add(Game.Element.MAZE_NEUTRAL);
						elems.add(Game.Element.MAZE_WALL);
						elems.add(Game.Element.MAZE_EXIT);
						elems.add(Game.Element.HERO_UNARMED);
						elems.add(Game.Element.DRAGON_AWAKE);
						elems.add(Game.Element.SWORD);
						
						int index = elems.indexOf(elem);
						
						if(e.getButton() == MouseEvent.BUTTON1) 
						{
							elemMap[y][x] = elems.get((index + 1) % elems.size());
						}
						else
						{
							int newIndex = index - 1;
							if(newIndex < 0) //% operator doesn't work as it should...
							{
								newIndex += elems.size();
							}
							elemMap[y][x] = elems.get(newIndex);
						}

						repaint();
					}
				}

				// ignored events
				public void mouseReleased(MouseEvent e) {}
				public void mouseClicked(MouseEvent e) {}
				public void mouseEntered(MouseEvent e) {}
				public void mouseExited(MouseEvent e) {}
			});
		}

		public void paintComponent(Graphics g)
		{
			super.paintComponent(g); // clears the backgorund ...
			if(elemMap != null) //a game is in progress
			{
				System.out.println("Maze printed...");
				paintMaze(g,elemMap);
			}
		}
	}
}
