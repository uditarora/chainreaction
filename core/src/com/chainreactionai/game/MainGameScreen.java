/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 * 
 */

public class MainGameScreen implements Screen {
	SpriteBatch batch;
	final private int GRID_SIZE = 8;
	final private int NUM_STATES_POSSIBLE = 4;
	final private int WIDTH_RECTANGLE = 55;
	final private int HEIGHT_RECTANGLE = 55;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	private OrthographicCamera camera;
	private int NUMBER_OF_PLAYERS;
	private Texture[][] atomImages = new Texture[NUM_STATES_POSSIBLE + 1][8];
	private Array<Rectangle> rectangularGrid;
	private GameBoard gameBoard;
	private int clickCoordX, clickCoordY, currentPlayer, numberOfMovesPlayed;
	private boolean clickOnEdge;
	MyInputProcessor inputProcessor = new MyInputProcessor();
	private boolean[] isCPU, lostPlayer;
	private boolean gameOver, moveCompleted;
	final private boolean DEBUG = true;
	final private boolean DEBUG_CPU = true;

	public MainGameScreen(ArrayList<Boolean> CPU) {
		NUMBER_OF_PLAYERS = CPU.size();
		if (DEBUG_CPU)
			NUMBER_OF_PLAYERS = 2;
		isCPU = new boolean[NUMBER_OF_PLAYERS];
		lostPlayer = new boolean[NUMBER_OF_PLAYERS];
		System.out.println(CPU.size());
		for (int i = 0; i < CPU.size(); i += 1) {
			System.out.println("isCPU[" + i + "] = " + CPU.get(i));
			isCPU[i] = CPU.get(i);
		}
		if (DEBUG_CPU) {
			isCPU[0] = isCPU[1] = true;
		}
		create();
	}

	private void create() {
		batch = new SpriteBatch();
		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		rectangularGrid = new Array<Rectangle>();
		gameBoard = new GameBoard(GRID_SIZE, NUMBER_OF_PLAYERS);
		Gdx.input.setInputProcessor(inputProcessor);
		inputProcessor.unsetTouchDown();
		numberOfMovesPlayed = currentPlayer = 0;

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
		// Three Atom Images Batch
		atomImages[4][0] = new Texture("fourAtomPlayerOne.jpg");
		atomImages[4][1] = new Texture("fourAtomPlayerTwo.jpg");
		atomImages[4][2] = new Texture("fourAtomPlayerThree.jpg");
		atomImages[4][3] = new Texture("fourAtomPlayerFour.jpg");
		atomImages[4][4] = new Texture("fourAtomPlayerFive.jpg");
		atomImages[4][5] = new Texture("fourAtomPlayerSix.jpg");
	}

	// This function loads the dimensions for all the
	// rectangles into the 2-D Array
	private void setDimsForRectangles() {
		for (int i = 0; i < GRID_SIZE; i += 1) {
			for (int j = 0; j < GRID_SIZE; j += 1) {
				Rectangle tempBlock = new Rectangle();
				tempBlock.x = (float) (i * WIDTH_RECTANGLE);
				tempBlock.y = (float) (j * HEIGHT_RECTANGLE);
				tempBlock.height = (float) (HEIGHT_RECTANGLE);
				tempBlock.width = (float) (WIDTH_RECTANGLE);
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

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if (moveCompleted) {
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
					GameSolver solver = new GameSolver(gameBoard, currentPlayer,
							NUMBER_OF_PLAYERS, lostPlayer);
					if (DEBUG)
						System.out.println("GameSolver initialized");
					Position winningMove = solver.getBestGameBoard();
					if(winningMove == null) {
						System.out.println("Error Time.");
					}
					gameBoard.changeBoard2(winningMove.coordX, winningMove.coordY, currentPlayer);
					moveCompleted = false;
					numberOfMovesPlayed += 1;
				}
		
				// process user input
				if (inputProcessor.isTouchedDown() && !gameOver) {
					inputProcessor.unsetTouchDown();
					processInput();
				}
			} else {
				currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
			}
		} else {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			moveCompleted = gameBoard.nextBoard(currentPlayer);
			if (moveCompleted) {
				if (gameBoard.isWinningPosition(currentPlayer)
						&& numberOfMovesPlayed > 1) {
					gameOver = true;
					System.out.println("Player " + currentPlayer
							+ " has won the game!");
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
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		drawGameBoard();
		batch.end();
		if (numberOfMovesPlayed > NUMBER_OF_PLAYERS) {
			for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				if (!lostPlayer[i]) {
					if (gameBoard.hasLost(i)) {
						lostPlayer[i] = true;
					}
				}
			}
		}
	}

	private void processInput() {
		// Checking whether the click is on an edge or a box.
		// If on edge, then reject the click.
		clickOnEdge = false;
		clickCoordX = (int) (inputProcessor.getXCoord() / WIDTH_RECTANGLE);
		if (inputProcessor.getXCoord() % WIDTH_RECTANGLE == 0.0) {
			clickOnEdge = true;
		}
		clickCoordY = (int) ((HEIGHT_SCREEN - inputProcessor.getYCoord()) / HEIGHT_RECTANGLE);
		if ((HEIGHT_SCREEN - inputProcessor.getYCoord()) % HEIGHT_RECTANGLE == 0.0) {
			clickOnEdge = true;
		}

		// If the click is within bounds of any one rectangle
		if (!clickOnEdge) {
			// Checking the move's validity and changing the board
			// accordingly.
			// Also passing the move to the next player.
			if (gameBoard.isValidMove(clickCoordX, clickCoordY,
					currentPlayer)) {
				gameBoard.changeBoard2(clickCoordX, clickCoordY,
						currentPlayer);
				moveCompleted = false;
				numberOfMovesPlayed += 1;
			}
		}
	}
	// Function to draw the game board using the three
	// arrays.
	private void drawGameBoard() {
		Iterator<Rectangle> iter = rectangularGrid.iterator();
		int i, j, count = 0;
		while (iter.hasNext()) {
			Rectangle tempBlock = iter.next();
			i = count / GRID_SIZE;
			j = count % GRID_SIZE;
			if (gameBoard.getRectangleWinner(i, j) == -1) {
				batch.draw(
						atomImages[0][0],
						tempBlock.x, tempBlock.y);
			} else {
				if (gameBoard.getNumAtomsInRectangle(i, j) > 4) {
					batch.draw(
							atomImages[0][gameBoard
									.getRectangleWinner(i, j)], tempBlock.x,
							tempBlock.y);
				} else {
					batch.draw(
							atomImages[gameBoard.getNumAtomsInRectangle(i, j)][gameBoard
									.getRectangleWinner(i, j)], tempBlock.x,
							tempBlock.y);
				}
			}
			count++;
		}
	}

	@Override
	public void dispose() {
		// dispose of all the native resources
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void show() {
	}

	@Override
	public void hide() {
	}
}
