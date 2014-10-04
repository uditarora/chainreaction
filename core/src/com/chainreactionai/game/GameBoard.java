/**
 * This class is used to represent the game board and its
 * utility functions.
 */
package com.chainreactionai.game;

/**
 * @author Kartik Parnami
 */
public class GameBoard {
	private int[][] rectangleWinner;
	private int[][] numAtomsInRectangle;
	private int gameGridSize, numPlayers;
	
	GameBoard(int gridSize, int gameNumPlayers) {
		numPlayers = gameNumPlayers;
		gameGridSize = gridSize;
		rectangleWinner = new int[gameGridSize][gameGridSize];
		numAtomsInRectangle = new int[gameGridSize][gameGridSize];
		setDefaultRectangleWinners();
		setDefaultNumAtomsInRectangle();
	}
	
	// This function sets the default winners of the
	// rectangles ie. player -1 (no winner)
	private void setDefaultRectangleWinners () {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				rectangleWinner[i][j] = -1;
			}
		}
	}

	// This function sets the default number of atoms
	// in the rectangles ie. 0
	private void setDefaultNumAtomsInRectangle () {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				numAtomsInRectangle[i][j] = 0;
			}
		}
	}
	
	public int getRectangleWinner (int row, int col) {
		return rectangleWinner[row][col];
	}
	
	public int getNumAtomsInRectangle (int row, int col) {
		return numAtomsInRectangle[row][col];
	}

	// This function checks whether the user-clicked move is
	// valid according to the current board position
	public boolean isValidMove (int coordX, int coordY, int player) {
		if ((rectangleWinner[coordX][coordY] == -1) || (rectangleWinner[coordX][coordY] == player)) {
			return true;
		}
		return false;
	}
	
	// This function changes the board according to the
	// rectangle clicked by user and returns whether further
	// processing is needed on the board due to the given click
	public void changeBoard (int coordX, int coordY, int player) {
		rectangleWinner[coordX][coordY] = player;
		numAtomsInRectangle[coordX][coordY] += 1;
		// If the clicked box is corner-most
		if ((coordX == 0 && coordY == 0) || (coordX == 0 && coordY == gameGridSize-1) || (coordX == gameGridSize-1 && coordY == 0) || (coordX == gameGridSize-1 && coordY == gameGridSize-1)) {
			if (numAtomsInRectangle[coordX][coordY] == 2) {
				numAtomsInRectangle[coordX][coordY] = 0;
				if (coordX == 0 && coordY == 0) {
					changeBoard(coordX + 1, coordY, player);
					changeBoard(coordX, coordY + 1, player);
				} else if (coordX == 0 && coordY == gameGridSize-1) {
					changeBoard(coordX + 1, coordY, player);
					changeBoard(coordX, coordY - 1, player);
				} else if (coordX == gameGridSize-1 && coordY == 0) {
					changeBoard(coordX - 1, coordY, player);
					changeBoard(coordX, coordY + 1, player);
				} else {
					changeBoard(coordX - 1, coordY, player);
					changeBoard(coordX, coordY - 1, player);
				}
				
			}
		} else if (coordX == 0 || coordY == 0 || coordX == gameGridSize-1 || coordY == gameGridSize-1) {
			if (numAtomsInRectangle[coordX][coordY] == 3) {
				numAtomsInRectangle[coordX][coordY] = 0;
				if (coordX == 0) {
					changeBoard(coordX, coordY + 1, player);
					changeBoard(coordX, coordY - 1, player);
					changeBoard(coordX + 1, coordY, player);
				} else if (coordY == 0) {
					changeBoard(coordX - 1, coordY, player);
					changeBoard(coordX + 1, coordY, player);
					changeBoard(coordX, coordY + 1, player);
				} else if (coordX == gameGridSize - 1) {
					changeBoard(coordX, coordY + 1, player);
					changeBoard(coordX, coordY - 1, player);
					changeBoard(coordX - 1, coordY, player);
				} else {
					changeBoard(coordX - 1, coordY, player);
					changeBoard(coordX + 1, coordY, player);
					changeBoard(coordX, coordY - 1, player);
				}
			}
		} else {
			if (numAtomsInRectangle[coordX][coordY] == 4) {
				numAtomsInRectangle[coordX][coordY] = 0;
				changeBoard(coordX - 1, coordY, player);
				changeBoard(coordX + 1, coordY, player);
				changeBoard(coordX, coordY - 1, player);
				changeBoard(coordX, coordY + 1, player);
			}
		}
	}
	
}
