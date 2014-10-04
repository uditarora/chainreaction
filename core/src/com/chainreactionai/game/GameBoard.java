/**
 * This class is used to represent the game board and its
 * utility functions.
 */
package com.chainreactionai.game;

import java.util.LinkedList;

/**
 * @author Kartik Parnami
 */
public class GameBoard {
	private int[][] rectangleWinner;
	private int[][] numAtomsInRectangle;
	private int gameGridSize, numPlayers;
	private GameBoard prevGameBoard;
	
	private class Position {
		private int coordX;
		private int coordY;
		
		private Position(int x, int y) {
			this.coordX = x;
			this.coordY = y;
		}
	}

	// Constructor to initialize the grid size, number of players
	// and setting the default values for rectangle winners and
	// number of atoms in each rectangle.
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
	private void setDefaultRectangleWinners() {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				rectangleWinner[i][j] = -1;
			}
		}
	}

	// This function sets the default number of atoms
	// in the rectangles ie. 0
	private void setDefaultNumAtomsInRectangle() {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				numAtomsInRectangle[i][j] = 0;
			}
		}
	}

	// This function returns the current winning player
	// for the given rectangle.
	public int getRectangleWinner(int row, int col) {
		return rectangleWinner[row][col];
	}

	// This function returns the number of atoms currently
	// present in a rectangle.
	public int getNumAtomsInRectangle(int row, int col) {
		return numAtomsInRectangle[row][col];
	}

	// This function checks whether the user-clicked move is
	// valid according to the current board position
	public boolean isValidMove(int coordX, int coordY, int player) {
		if ((rectangleWinner[coordX][coordY] == -1)
				|| (rectangleWinner[coordX][coordY] == player)) {
			return true;
		}
		return false;
	}

	// This function changes the board according to the
	// rectangle clicked by user and recursively calls
	// itself according to the input and the number of atoms
	// currently in the rectangle using BFS
	public void changeBoard(int coordX, int coordY, int player) {
		Position initialPosition = new Position(coordX, coordY);
		LinkedList<Position> positionsQueue = new LinkedList<Position>();
		positionsQueue.add(initialPosition);
		Position currentPosition;
		while (positionsQueue.peek() != null) {
			currentPosition = positionsQueue.poll();
			rectangleWinner[currentPosition.coordX][currentPosition.coordY] = player;
			numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] += 1;
			
			// If the clicked box is corner-most
			if ((currentPosition.coordX == 0 && currentPosition.coordY == 0)
					|| (currentPosition.coordX == 0 && currentPosition.coordY == gameGridSize - 1)
					|| (currentPosition.coordX == gameGridSize - 1 && currentPosition.coordY == 0)
					|| (currentPosition.coordX == gameGridSize - 1 && currentPosition.coordY == gameGridSize - 1)) {
				if (numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] == 2) {
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] = 0;
					rectangleWinner[currentPosition.coordX][currentPosition.coordY] = -1;
					if (currentPosition.coordX == 0 && currentPosition.coordY == 0) {
						positionsQueue.add(new Position(currentPosition.coordX + 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY + 1));
					} else if (currentPosition.coordX == 0 && currentPosition.coordY == gameGridSize - 1) {
						positionsQueue.add(new Position(currentPosition.coordX + 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY - 1));
					} else if (currentPosition.coordX == gameGridSize - 1 && currentPosition.coordY == 0) {
						positionsQueue.add(new Position(currentPosition.coordX - 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY + 1));
					} else {
						positionsQueue.add(new Position(currentPosition.coordX - 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY - 1));
					}
	
				}
			} else if (currentPosition.coordX == 0 || currentPosition.coordY == 0 || currentPosition.coordX == gameGridSize - 1
					|| currentPosition.coordY == gameGridSize - 1) {
				if (numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] == 3) {
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] = 0;
					rectangleWinner[currentPosition.coordX][currentPosition.coordY] = -1;
					if (currentPosition.coordX == 0) {
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY + 1));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY - 1));
						positionsQueue.add(new Position(currentPosition.coordX + 1, currentPosition.coordY));
					} else if (currentPosition.coordY == 0) {
						positionsQueue.add(new Position(currentPosition.coordX - 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX + 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY + 1));
					} else if (currentPosition.coordX == gameGridSize - 1) {
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY + 1));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY - 1));
						positionsQueue.add(new Position(currentPosition.coordX - 1, currentPosition.coordY));
					} else {
						positionsQueue.add(new Position(currentPosition.coordX - 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX + 1, currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY - 1));
					}
				}
			} else {
				if (numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] == 4) {
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] = 0;
					rectangleWinner[currentPosition.coordX][currentPosition.coordY] = -1;
					positionsQueue.add(new Position(currentPosition.coordX - 1, currentPosition.coordY));
					positionsQueue.add(new Position(currentPosition.coordX + 1, currentPosition.coordY));
					positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY - 1));
					positionsQueue.add(new Position(currentPosition.coordX, currentPosition.coordY + 1));
				}
			}
		}
	}

	public boolean isWinningPosition(int player) {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (rectangleWinner[i][j] != player
						&& rectangleWinner[i][j] != -1) {
					return false;
				}
			}
		}
		return true;
	}
	
	public GameBoard getPrevGameBoard() {
		return prevGameBoard;
	}
	
	public void setPrevGameBoard(GameBoard tempGameBoard) {
		prevGameBoard = tempGameBoard;
	}

}
