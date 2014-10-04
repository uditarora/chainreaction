package com.chainreactionai.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class ChainReactionAIGame implements ApplicationListener {

	SpriteBatch batch;
	final private int GRID_SIZE = 8;
	final private int NUMBER_OF_PLAYERS = 2;
	final private int NUM_STATES_POSSIBLE = 4;
	final private int WIDTH_RECTANGLE = 55;
	final private int HEIGHT_RECTANGLE = 55;
	final private int WIDTH_SCREEN = 440;
	final private int HEIGHT_SCREEN = 480;
	private OrthographicCamera camera;
	private Texture[][] atomImages = new Texture[NUM_STATES_POSSIBLE][NUMBER_OF_PLAYERS];
	private Array<Rectangle> gameGrid;
	private GameBoard gameBoard;
	private int clickCoordX, clickCoordY, playerWithChance;
	private boolean clickOnEdge;
	MyInputProcessor inputProcessor = new MyInputProcessor();

	@Override
	public void create() {
		batch = new SpriteBatch();

		// Show the world to be 440*480 no matter the
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, WIDTH_SCREEN, HEIGHT_SCREEN);
		gameGrid = new Array<Rectangle>();
		gameBoard = new GameBoard(GRID_SIZE, NUMBER_OF_PLAYERS);
		Gdx.input.setInputProcessor(inputProcessor);
		inputProcessor.negateTouchDown();
		playerWithChance = 0;

		// Load default values into arrays
		loadImagesintoArrays();
		setDimsForRectangles();
	}

	// This function loads the images into the arrays
	// of textures.
	private void loadImagesintoArrays() {
		// Background image loaded into first column
		atomImages[0][0] = new Texture("background.jpg");
		atomImages[0][1] = new Texture("background.jpg");
		// One Atom Images Batch
		atomImages[1][0] = new Texture("oneAtomPlayerOne.jpg");
		atomImages[1][1] = new Texture("oneAtomPlayerTwo.jpg");
		// Two Atom Images Batch
		atomImages[2][0] = new Texture("twoAtomPlayerOne.jpg");
		atomImages[2][1] = new Texture("twoAtomPlayerTwo.jpg");
		// Three Atom Images Batch
		atomImages[3][0] = new Texture("threeAtomPlayerOne.jpg");
		atomImages[3][1] = new Texture("threeAtomPlayerTwo.jpg");
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
				gameGrid.add(tempBlock);
			}
		}
	}

	@Override
	public void render() {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();

		// tell the SpriteBatch to render in the
		// coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		drawGameBoard();
		batch.end();
		
		// process user input
		if (inputProcessor.isTouchedDown()) {
			inputProcessor.negateTouchDown();
			clickOnEdge = false;			
			clickCoordX = (int)(inputProcessor.getXCoord()/WIDTH_RECTANGLE);
			if (inputProcessor.getXCoord()%WIDTH_RECTANGLE == 0.0) {
				clickOnEdge = true;
			}
			clickCoordY = (int)((480 - inputProcessor.getYCoord())/HEIGHT_RECTANGLE);
			if ((480 - inputProcessor.getYCoord())%HEIGHT_RECTANGLE == 0.0) {
				clickOnEdge = true;
			}
			
			// If the click is within bounds of any one rectangle
			if (!clickOnEdge) {
				// Checking the move's validity and changing the board accordingly.
				// Also passing the chance to the next player.
				if (gameBoard.isValidMove(clickCoordX, clickCoordY, playerWithChance)) {
					gameBoard.changeBoard(clickCoordX, clickCoordY, playerWithChance);
					playerWithChance = (playerWithChance + 1)%NUMBER_OF_PLAYERS;
				}
			}
		}
	}

	// Function to draw the game board using the three
	// arrays.
	private void drawGameBoard() {
		Iterator<Rectangle> iter = gameGrid.iterator();
		int i, j, count = 0;
		while (iter.hasNext()) {
			Rectangle tempBlock = iter.next();
			i = count / GRID_SIZE;
			j = count % GRID_SIZE;
			if (gameBoard.getRectangleWinner(i, j) == -1) {
				batch.draw(
						atomImages[gameBoard.getNumAtomsInRectangle(i, j)][0],
						tempBlock.x, tempBlock.y);
			} else {
				batch.draw(
						atomImages[gameBoard.getNumAtomsInRectangle(i, j)][gameBoard
								.getRectangleWinner(i, j)], tempBlock.x,
						tempBlock.y);
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

}