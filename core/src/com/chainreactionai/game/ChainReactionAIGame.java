package com.chainreactionai.game;

import java.util.Iterator;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class ChainReactionAIGame extends ApplicationAdapter {
	
	SpriteBatch batch;
	final private int GRID_SIZE = 8;
	final private int NUMBER_OF_PLAYERS = 2;
	private OrthographicCamera camera;
	private Texture[][] atomImages = new Texture[4][NUMBER_OF_PLAYERS];
	private Array<Rectangle> gameGrid;
	private int[][] rectangleWinner = new int[GRID_SIZE][GRID_SIZE];
	private int[][] numAtomsInRectangle = new int[GRID_SIZE][GRID_SIZE];
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		
		// Show the world to be 440*480 no matter the 
		// size of the screen
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 440, 480);
		
		// Load default values into arrays
		loadImagesintoArrays();
		setDimsForRectangles();
		setDefaultRectangleWinners();
		setDefaultNumAtomsInRectangle();
	}

	// This function loads the images into the arrays
	// of textures.
	private void loadImagesintoArrays () {
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
	private void setDimsForRectangles () {
		for (int i = 0; i < GRID_SIZE; i += 1) {
			for (int j = 0; j < GRID_SIZE; j += 1) {
				Rectangle tempBlock = new Rectangle();
				tempBlock.x = (float)(i*55);
				tempBlock.y = (float)(j*55);
				tempBlock.height = (float)(55);
				tempBlock.width = (float)(55);
				gameGrid.add(tempBlock);
			}
		}
	}
	
	// This function sets the default winners of the
	// rectangles ie. player -1 (no winner)
	private void setDefaultRectangleWinners () {
		for (int i = 0; i < GRID_SIZE; i += 1) {
			for (int j = 0; j < GRID_SIZE; j += 1) {
				rectangleWinner[i][j] = -1;
			}
		}
	}
	
	// This function sets the default number of atoms
	// in the rectangles ie. 0
	private void setDefaultNumAtomsInRectangle () {
		for (int i = 0; i < GRID_SIZE; i += 1) {
			for (int j = 0; j < GRID_SIZE; j += 1) {
				numAtomsInRectangle[i][j] = 0;
			}
		}
	}
	
	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		// tell the camera to update its matrices.
		camera.update();
		
		// tell the SpriteBatch to render in the
	    // coordinate system specified by the camera.
		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		numAtomsInRectangle[1][2] = 2;
		rectangleWinner[1][2] = 1;
		drawGameBoard();
		batch.end();
	}
	
	// Function to draw the game board using the three
	// arrays.
	private void drawGameBoard () {
		Iterator<Rectangle> iter = gameGrid.iterator();
		int i, j, count = 0;
		while(iter.hasNext()) {
			 Rectangle tempBlock = iter.next();
			 i = count/GRID_SIZE;
			 j = count%GRID_SIZE;
			 if (rectangleWinner[i][j] == -1) {
				batch.draw(atomImages[numAtomsInRectangle[i][j]][0], tempBlock.x, tempBlock.y);
			 } else {
				batch.draw(atomImages[numAtomsInRectangle[i][j]][rectangleWinner[i][j]], tempBlock.x, tempBlock.y);
			 }
			 count++;
	    }
	}
}