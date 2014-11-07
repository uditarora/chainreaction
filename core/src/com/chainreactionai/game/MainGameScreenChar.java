/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * @author Kartik Parnami
 * 
 */

public class MainGameScreenChar implements Screen {
	SpriteBatch batch;
	final private int GRID_SIZE = 8;
	final private int NUM_STATES_POSSIBLE = 4;
	final private float WIDTH_RECTANGLE = (float)(55);
	final private float HEIGHT_RECTANGLE = (float)(55);
	final private int WIDTH_SCREEN = 452;
	final private int HEIGHT_SCREEN = 480;
	final private int HEIGHT_PAUSE_BUTTON = 27;
	final private int WIDTH_PAUSE_BUTTON = 55;
	final private int HEIGHT_PAUSE_MENU_BUTTONS = 60;
	final private int WIDTH_PAUSE_MENU_BUTTONS = 150;
	private OrthographicCamera camera;
	private int NUMBER_OF_PLAYERS;
	private Texture pauseButtonImg = new Texture("pauseButton.jpg"), gameBackground = new Texture("gameBackground.jpg");
	private Texture[][] atomImages = new Texture[NUM_STATES_POSSIBLE + 1][8];
	private Texture[][] highlightedAtomImages = new Texture[NUM_STATES_POSSIBLE + 1][8];
	private Array<Rectangle> rectangularGrid;
	private GameBoardChar gameBoard;
	private int clickCoordX, clickCoordY, currentPlayer, numberOfMovesPlayed, gameState, maxPlyLevel;
	private float heightUpscaleFactor, widthUpscaleFactor;
	private double percentageMovesSearched, incrementValForPercentageMovesSearched;
	private boolean clickOnEdge;
	MyInputProcessor inputProcessor = new MyInputProcessor();
	private boolean[] isCPU, lostPlayer;
	private int[] maxPlyLevels;
	private boolean gameOver, moveCompleted;
	private ChainReactionAIGame myGame;
	private Stage stage = new Stage();
	private Table table = new Table();
	private Viewport viewport;
	private Skin skin = new Skin(Gdx.files.internal("data/uiskin.json"), new TextureAtlas(Gdx.files.internal("data/uiskin.atlas")));
	private TextButton resumeButton, exitButton, newGameButton;
	private Position highlightPos = new Position(-1, -1);
	private long prevTime, newTime;
	private FileHandle handle = Gdx.files.external("data/myfile.txt");
	private ShapeRenderer shapeRenderer = new ShapeRenderer();
	// All debug printing should go under this flag.
	final private boolean DEBUG = true;
	final private boolean DEBUG_CPU = true;

	// Constructor to initialize which player is CPU and which is human.
	// Also sets difficulty levels for CPU players.
	public MainGameScreenChar(ChainReactionAIGame game, ArrayList<Boolean> CPU, ArrayList<Integer> plyLevelList) {
		myGame = game;
		NUMBER_OF_PLAYERS = CPU.size();
		if (DEBUG_CPU)
			NUMBER_OF_PLAYERS = 2;
		isCPU = new boolean[NUMBER_OF_PLAYERS];
		lostPlayer = new boolean[NUMBER_OF_PLAYERS];
		maxPlyLevels = new int[NUMBER_OF_PLAYERS];
		
		//Simulating with only CPU Players for testing
		if (DEBUG_CPU) {
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				isCPU[i] = true;
				maxPlyLevels[i] = 6;
			}
			//maxPlyLevels[0] = 2;
		}
		else {
			if (DEBUG)
				System.out.println(CPU.size());
			
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				isCPU[i] = CPU.get(i);
				if (isCPU[i]) {
					maxPlyLevels[i] = plyLevelList.get(i);
					System.out.println("isCPU[" + i + "] = " + isCPU[i] + " with Ply Level = " + maxPlyLevels[i]);
				} else {
					System.out.println("isCPU[" + i + "] = " + isCPU[i]);
				}
			}
		}
		setGameState(0);
		create();
	}

	private void create() {
		batch = new SpriteBatch();
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		viewport = new Viewport() {
		};
		viewport.setCamera(camera);
		
		// Initializing stuff.
		rectangularGrid = new Array<Rectangle>();
		gameBoard = new GameBoardChar(GRID_SIZE, NUMBER_OF_PLAYERS);
		Gdx.input.setInputProcessor(inputProcessor);
		inputProcessor.unsetTouchDown();
		numberOfMovesPlayed = currentPlayer = 0;
		// Up-scale Factors are used to get proper sized buttons
		// upscaled or downscaled according to the Screen Dimensions
		heightUpscaleFactor = ((float)(ChainReactionAIGame.HEIGHT))/HEIGHT_SCREEN;
		widthUpscaleFactor = ((float)(ChainReactionAIGame.WIDTH))/WIDTH_SCREEN;
		maxPlyLevel = 0;
		prevTime = System.currentTimeMillis();
		for (int i = 0; i < maxPlyLevels.length; i += 1) {
			if (maxPlyLevels[i] > maxPlyLevel) {
				maxPlyLevel = maxPlyLevels[i];
			}
		}
		percentageMovesSearched = 1/(double)(maxPlyLevel);
		incrementValForPercentageMovesSearched = 1/(double)(3*maxPlyLevel*maxPlyLevel);
		resumeButton = new TextButton(new String("Resume"), skin);
		newGameButton = new TextButton(new String("New Game"), skin);
		exitButton = new TextButton("Exit", skin);
		handle.writeString("--------------------------------------------------------------------------\r\n", true);
		
		// Populating the Pause menu with the buttons.
		table.add(resumeButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*heightUpscaleFactor).padBottom(2).row();
		table.add(newGameButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*heightUpscaleFactor).padBottom(2).row();
		table.add(exitButton).size(WIDTH_PAUSE_MENU_BUTTONS*widthUpscaleFactor, HEIGHT_PAUSE_MENU_BUTTONS*heightUpscaleFactor).padBottom(2).row();
		table.setFillParent(true);
		
		//Attaching click handlers to the pause menu buttons.
		resumeButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				resume();
			}
		});
		newGameButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				shiftToNewGameScreen();
			}
		});
		exitButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});
		
		// Load default values into arrays
		loadImagesintoArrays();
		setDimsForRectangles();
		setNoPlayerHasLost();
		gameOver = false;
		moveCompleted = true;
	}

	// This function loads the images into the arrays
	// of textures.
	private void loadImagesintoArrays() {
		// Background image loaded into first column
		atomImages[0][0] = new Texture("background.jpg");
		atomImages[0][1] = new Texture("background.jpg");
		atomImages[0][2] = new Texture("background.jpg");
		atomImages[0][3] = new Texture("background.jpg");
		atomImages[0][4] = new Texture("background.jpg");
		atomImages[0][5] = new Texture("background.jpg");
		// One Atom Images Batch
		atomImages[1][0] = new Texture("oneAtomPlayerOne.jpg");
		atomImages[1][1] = new Texture("oneAtomPlayerTwo.jpg");
		atomImages[1][2] = new Texture("oneAtomPlayerThree.jpg");
		atomImages[1][3] = new Texture("oneAtomPlayerFour.jpg");
		atomImages[1][4] = new Texture("oneAtomPlayerFive.jpg");
		atomImages[1][5] = new Texture("oneAtomPlayerSix.jpg");
		// Two Atom Images Batch
		atomImages[2][0] = new Texture("twoAtomPlayerOne.jpg");
		atomImages[2][1] = new Texture("twoAtomPlayerTwo.jpg");
		atomImages[2][2] = new Texture("twoAtomPlayerThree.jpg");
		atomImages[2][3] = new Texture("twoAtomPlayerFour.jpg");
		atomImages[2][4] = new Texture("twoAtomPlayerFive.jpg");
		atomImages[2][5] = new Texture("twoAtomPlayerSix.jpg");
		// Three Atom Images Batch
		atomImages[3][0] = new Texture("threeAtomPlayerOne.jpg");
		atomImages[3][1] = new Texture("threeAtomPlayerTwo.jpg");
		atomImages[3][2] = new Texture("threeAtomPlayerThree.jpg");
		atomImages[3][3] = new Texture("threeAtomPlayerFour.jpg");
		atomImages[3][4] = new Texture("threeAtomPlayerFive.jpg");
		atomImages[3][5] = new Texture("threeAtomPlayerSix.jpg");
		// Four Atom Images Batch
		atomImages[4][0] = new Texture("fourAtomPlayerOne.jpg");
		atomImages[4][1] = new Texture("fourAtomPlayerTwo.jpg");
		atomImages[4][2] = new Texture("fourAtomPlayerThree.jpg");
		atomImages[4][3] = new Texture("fourAtomPlayerFour.jpg");
		atomImages[4][4] = new Texture("fourAtomPlayerFive.jpg");
		atomImages[4][5] = new Texture("fourAtomPlayerSix.jpg");
		// Background highlighted image loaded into first column
		highlightedAtomImages[0][0] = new Texture("backgroundHigh.jpg");
		highlightedAtomImages[0][1] = new Texture("backgroundHigh.jpg");
		highlightedAtomImages[0][2] = new Texture("backgroundHigh.jpg");
		highlightedAtomImages[0][3] = new Texture("backgroundHigh.jpg");
		highlightedAtomImages[0][4] = new Texture("backgroundHigh.jpg");
		highlightedAtomImages[0][5] = new Texture("backgroundHigh.jpg");
		// One Atom highlighted Images Batch
		highlightedAtomImages[1][0] = new Texture("oneAtomPlayerOneHigh.jpg");
		highlightedAtomImages[1][1] = new Texture("oneAtomPlayerTwoHigh.jpg");
		highlightedAtomImages[1][2] = new Texture("oneAtomPlayerThreeHigh.jpg");
		highlightedAtomImages[1][3] = new Texture("oneAtomPlayerFourHigh.jpg");
		highlightedAtomImages[1][4] = new Texture("oneAtomPlayerFiveHigh.jpg");
		highlightedAtomImages[1][5] = new Texture("oneAtomPlayerSixHigh.jpg");
		// Two Atom highlighted Images Batch
		highlightedAtomImages[2][0] = new Texture("twoAtomPlayerOneHigh.jpg");
		highlightedAtomImages[2][1] = new Texture("twoAtomPlayerTwoHigh.jpg");
		highlightedAtomImages[2][2] = new Texture("twoAtomPlayerThreeHigh.jpg");
		highlightedAtomImages[2][3] = new Texture("twoAtomPlayerFourHigh.jpg");
		highlightedAtomImages[2][4] = new Texture("twoAtomPlayerFiveHigh.jpg");
		highlightedAtomImages[2][5] = new Texture("twoAtomPlayerSixHigh.jpg");
		// Three Atom highlighted Images Batch
		highlightedAtomImages[3][0] = new Texture("threeAtomPlayerOneHigh.jpg");
		highlightedAtomImages[3][1] = new Texture("threeAtomPlayerTwoHigh.jpg");
		highlightedAtomImages[3][2] = new Texture("threeAtomPlayerThreeHigh.jpg");
		highlightedAtomImages[3][3] = new Texture("threeAtomPlayerFourHigh.jpg");
		highlightedAtomImages[3][4] = new Texture("threeAtomPlayerFiveHigh.jpg");
		highlightedAtomImages[3][5] = new Texture("threeAtomPlayerSixHigh.jpg");
		//Four Atom highlighted Images Batch
		highlightedAtomImages[4][0] = new Texture("fourAtomPlayerOneHigh.jpg");
		highlightedAtomImages[4][1] = new Texture("fourAtomPlayerTwoHigh.jpg");
		highlightedAtomImages[4][2] = new Texture("fourAtomPlayerThreeHigh.jpg");
		highlightedAtomImages[4][3] = new Texture("fourAtomPlayerFourHigh.jpg");
		highlightedAtomImages[4][4] = new Texture("fourAtomPlayerFiveHigh.jpg");
		highlightedAtomImages[4][5] = new Texture("fourAtomPlayerSixHigh.jpg");
	}

	// This function loads the dimensions for all the
	// rectangles into the 2-D Array
	private void setDimsForRectangles() {
		for (int i = 0; i < GRID_SIZE; i += 1) {
			for (int j = 0; j < GRID_SIZE; j += 1) {
				Rectangle tempBlock = new Rectangle();
				tempBlock.x = (float) (i * WIDTH_RECTANGLE) + 4;
				tempBlock.y = (float) (j * HEIGHT_RECTANGLE) + 4;
				tempBlock.height = (float) (HEIGHT_RECTANGLE);
				tempBlock.width = (float) (WIDTH_RECTANGLE);
				System.out.println(tempBlock.x + " " + tempBlock.y);
				rectangularGrid.add(tempBlock);
			}
		}
	}
	
	// Populates the lostPlayer array to show that no player
	// has lost the game initially.
	private void setNoPlayerHasLost() {
		for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			lostPlayer[i] = false;
		}
	}

	// This function sets the gameState to given argument val.
	private void setGameState(int state) {
		gameState = state;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// process user input
		if (inputProcessor.isTouchedDown() && !gameOver) {
			inputProcessor.unsetTouchDown();
			// Log the clicks for Debugging
			if(DEBUG) {
				Gdx.app.log("Click found at", inputProcessor.getXCoord() + " " + inputProcessor.getYCoord());
				Gdx.app.log("Click found at", normalizeClickCoord(inputProcessor.getXCoord(), ChainReactionAIGame.WIDTH, WIDTH_SCREEN) + " " + normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN));
			}
			if (gameState == 0) {
				// Used to process the clicks
				if (moveCompleted) {
					if (lostPlayer[currentPlayer] == false) {
						processUserInputForMove();
					}
				}
				// Used to process pause button click
				processPauseAction();
			}
		}
		
		// If game is not paused
		if (gameState == 0) {
			// If the move is done after the animation
			if (moveCompleted) {
				// If the player has not lost the game yet
				if (lostPlayer[currentPlayer] == false) { 
					// Check if current player is CPU and play its move
					if (isCPU[currentPlayer] && !gameOver) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (DEBUG)
							System.out.println("Reached CPU");
						// Initializing the GameSolver
						newTime = System.currentTimeMillis();
						Gdx.app.log("mainPlayerAndPercentageMoves","MainPlayer: " + currentPlayer + " with percentageMovesSearched: " + percentageMovesSearched + " numMovesPlayed: " + numberOfMovesPlayed + " and Time taken : " + ((newTime - prevTime)/1000));
						handle.writeString("MainPlayer: " + currentPlayer + " with percentageMovesSearched: " + percentageMovesSearched + " numMovesPlayed: " + numberOfMovesPlayed + " and Time taken : " + ((newTime - prevTime)/1000) +"\r\n", true);
						GameSolverChar solver = new GameSolverChar(gameBoard, currentPlayer,
								NUMBER_OF_PLAYERS, lostPlayer, maxPlyLevels[currentPlayer], percentageMovesSearched);
						prevTime = System.currentTimeMillis();
						if (DEBUG)
							System.out.println("GameSolver initialized");
						// Get the position where we should place the new ball
						// so that the GameSolver's best move is executed.
						Position winningMove = solver.getBestGameBoard();
						if(winningMove == null) {
							System.out.println("Error Time.");
						}
						// Initialize the animation for changing the Board.
						gameBoard.changeBoard2(winningMove.coordX, winningMove.coordY, currentPlayer);
						// Store these coordinates so they can be shown as highlighted.
						highlightPos.coordX = winningMove.coordX;
						highlightPos.coordY = winningMove.coordY;
						// Set the moveCompleted flag to be false, so as to start the animation.
						moveCompleted = false;
						numberOfMovesPlayed += 1;
						percentageMovesSearched += incrementValForPercentageMovesSearched;
					}
				} else {
					// Giving the chance to the next player to play.
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
				}
			} else {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				// Animates board to show new board with next level of BFS
				// calls done. Returns whether the BFS is complete or not.
				moveCompleted = gameBoard.nextBoard(currentPlayer);
				// Empties the highlight position so every recursive split
				// is not highlighted.
				highlightPos.coordX = -1;
				highlightPos.coordY = -1;
				// Check after the move if it was the winning move or not.
				if (moveCompleted) {
					if (gameBoard.isWinningPosition(currentPlayer)
							&& numberOfMovesPlayed > 1) {
						gameOver = true;
						System.out.println("Player " + currentPlayer
								+ " has won the game!");
						myGame.setScreen(new GameEndScreen(myGame, currentPlayer, numberOfMovesPlayed));
					}
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
					System.out.println("Move time.");
				}
			}
	
			// Rendering here to have board updated
			// after every player's move.
			// Tell the camera to update its matrices.
			camera.update();
	
			// Tell the SpriteBatch to render in the
			// coordinate system specified by the camera.
			batch = (SpriteBatch)stage.getBatch();
			batch.setProjectionMatrix(camera.combined);
			batch.begin();
			//batch.draw(gameBackground, 0, 0);
			batch.end();
			shapeRenderer.setProjectionMatrix(camera.combined);
			shapeRenderer.begin(ShapeType.Line);
			shapeRenderer.setColor(1, 1, 0, 1);
			drawGameBoard2();
			shapeRenderer.end();
			//batch.setProjectionMatrix(camera.combined);
			//batch.begin();
			//drawGameBoard();
			//batch.end();
			// Check if any player has lost the game and doesn't permit
			// it to play any further.
			if (numberOfMovesPlayed > NUMBER_OF_PLAYERS) {
				for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
					if (!lostPlayer[i]) {
						if (gameBoard.hasLost(i)) {
							lostPlayer[i] = true;
						}
					}
				}
			}
		} else {
			// If the game is paused, add the pause menu to the stage.
			stage.addActor(table);
			stage.act();
			stage.draw();
			Gdx.input.setInputProcessor(stage);
		}
	}

	// This function processes the User input for a new move and changes
	// the board accordingly.
	private void processUserInputForMove() {
		// Checking whether the click is on an edge or a box.
		// If on edge, then reject the click.
		if (normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN) < HEIGHT_SCREEN - WIDTH_SCREEN || normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN) > ((HEIGHT_SCREEN - WIDTH_SCREEN) + (GRID_SIZE*HEIGHT_RECTANGLE)))
			return;
		clickOnEdge = false;
		clickCoordX = (int) (normalizeClickCoord(inputProcessor.getXCoord(), ChainReactionAIGame.WIDTH, WIDTH_SCREEN) / WIDTH_RECTANGLE);
		if (normalizeClickCoord(inputProcessor.getXCoord(), ChainReactionAIGame.WIDTH, WIDTH_SCREEN) % WIDTH_RECTANGLE == 0.0) {
			clickOnEdge = true;
		}
		clickCoordY = (int) ((HEIGHT_SCREEN - normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN)) / HEIGHT_RECTANGLE);
		if ((HEIGHT_SCREEN - normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN)) % HEIGHT_RECTANGLE == 0.0) {
			clickOnEdge = true;
		}

		// If the click is within bounds of any one rectangle
		if (!clickOnEdge) {
			// Checking the move's validity and changing the board
			// accordingly.
			// Also passing the move to the next player.
			if (!isCPU[currentPlayer] && gameBoard.isValidMove(clickCoordX, clickCoordY,
					currentPlayer)) {
				gameBoard.changeBoard2(clickCoordX, clickCoordY,
						currentPlayer);
				highlightPos.coordX = clickCoordX;
				highlightPos.coordY = clickCoordY;
				moveCompleted = false;
				numberOfMovesPlayed += 1;
				percentageMovesSearched += incrementValForPercentageMovesSearched;
			}
		}
	}
	
	// This function processes the user input if the user 
	// tries to pause the game.
	private void processPauseAction() {
		// Checks if the click is on the pause button, else returns
		if (normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN) > HEIGHT_SCREEN - (GRID_SIZE * HEIGHT_RECTANGLE) || normalizeClickCoord(inputProcessor.getYCoord(), ChainReactionAIGame.HEIGHT, HEIGHT_SCREEN) < HEIGHT_SCREEN - (GRID_SIZE * HEIGHT_RECTANGLE) - HEIGHT_PAUSE_BUTTON) {
			return;
		}
		if (normalizeClickCoord(inputProcessor.getXCoord(), ChainReactionAIGame.WIDTH, WIDTH_SCREEN) > WIDTH_PAUSE_BUTTON || normalizeClickCoord(inputProcessor.getXCoord(), ChainReactionAIGame.WIDTH, WIDTH_SCREEN) < 0) {
			return;
		}
		pause();
	}
	
	// Function to draw the game board using the three
	// arrays.
	private void drawGameBoard() {
		Iterator<Rectangle> iter = rectangularGrid.iterator();
		int i, j, count = 0;
		batch.draw(gameBackground, 0, 0);
		// Goes through all the rectangles pre-fed into the 
		// rectangularGrid and draws them to the board.
		while (iter.hasNext()) {
			Rectangle tempBlock = iter.next();
			i = count / GRID_SIZE;
			j = count % GRID_SIZE;
			// Checks if a given rectangle has to be highlighted indicating a move.
			if (highlightPos.coordX == i && highlightPos.coordY == j) {
				if (gameBoard.getRectangleWinner(i, j) == -1) {
					batch.draw(
							highlightedAtomImages[0][0],
							tempBlock.x, tempBlock.y);
				} else {
					if (gameBoard.getNumAtomsInRectangle(i, j) > 4) {
						batch.draw(
								highlightedAtomImages[0][gameBoard.getRectangleWinner(i, j)], tempBlock.x,
								tempBlock.y);
					} else {
						batch.draw(
								highlightedAtomImages[gameBoard.getNumAtomsInRectangle(i, j)][gameBoard.getRectangleWinner(i, j)], tempBlock.x,
								tempBlock.y);
					}
				}
				count++;
			} else {
				if (gameBoard.getRectangleWinner(i, j) == -1) {
					batch.draw(
							atomImages[0][0],
							tempBlock.x, tempBlock.y);
				} else {
					if (gameBoard.getNumAtomsInRectangle(i, j) > 4) {
						batch.draw(
								atomImages[0][gameBoard.getRectangleWinner(i, j)], tempBlock.x,
								tempBlock.y);
					} else {
						batch.draw(
								atomImages[gameBoard.getNumAtomsInRectangle(i, j)][gameBoard.getRectangleWinner(i, j)], tempBlock.x,
								tempBlock.y);
					}
				}
				count++;
			}
		}
		// Draws the pause button to the right place on the screen.
		batch.draw(pauseButtonImg, 0, (GRID_SIZE*HEIGHT_RECTANGLE));
	}
	
	private void drawGameBoard2() {
		Iterator<Rectangle> iter = rectangularGrid.iterator();
		int i, j, count = 0;
		shapeRenderer.setColor(Color.BLACK);
		filledRect(0, 0, WIDTH_SCREEN, HEIGHT_SCREEN);
		shapeRenderer.setColor(Color.RED);
		// Goes through all the rectangles pre-fed into the 
		// rectangularGrid and draws them to the board.
		while (iter.hasNext()) {
			Rectangle tempBlock = iter.next();
			i = count / GRID_SIZE;
			j = count % GRID_SIZE;
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE);
			shapeRenderer.line(tempBlock.x + 4, tempBlock.y + 4, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + 4);
			shapeRenderer.line(tempBlock.x + 4, tempBlock.y + 4, tempBlock.x + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x + 4, tempBlock.y + HEIGHT_RECTANGLE + 4, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + 4, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x, tempBlock.y, tempBlock.x + 4, tempBlock.y + 4);
			shapeRenderer.line(tempBlock.x, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + 4);
			shapeRenderer.line(tempBlock.x + WIDTH_RECTANGLE, tempBlock.y + HEIGHT_RECTANGLE, tempBlock.x + WIDTH_RECTANGLE + 4, tempBlock.y + HEIGHT_RECTANGLE + 4);
			// Checks if a given rectangle has to be highlighted indicating a move.
		}
		// Draws the pause button to the right place on the screen.
		//batch.draw(pauseButtonImg, 0, (GRID_SIZE*HEIGHT_RECTANGLE));
	}
	
	private void filledRect(int coordX, int coordY, int width, int height) {
		for (int i = 0; i < height; i += 1) {
			shapeRenderer.line(0, i, WIDTH_SCREEN, i);
		}
	}
	
	// Normalizes the coordinates of a click to the correct coordinates
	// according to the screen size and the game dimensions.
	private float normalizeClickCoord(float coordVal, float screenVal, float normalizeTo) {
		return ((coordVal*normalizeTo)/screenVal);
	}

	// This function shifts the Screen to the NumPlayersScreen.
	private void shiftToNewGameScreen() {
		myGame.setScreen(new NumPlayersScreen(myGame));
	}
	
	@Override
	public void dispose() {
		// dispose of all the native resources
	}

	// On window resize changes the dimensions for 
	// continued pleasure (if you know what I mean :P)
	@Override
	public void resize(int width, int height) {
		ChainReactionAIGame.WIDTH = width;
		ChainReactionAIGame.HEIGHT = height;
	}

	// Sets game state to 1 thereby Pausing the game.
	@Override
	public void pause() {
		setGameState(1);
	}

	// Sets game state to 0 thereby resuming the game.
	@Override
	public void resume() {
		setGameState(0);
		Gdx.input.setInputProcessor(inputProcessor);
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}
}