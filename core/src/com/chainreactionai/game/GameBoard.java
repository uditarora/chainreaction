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
	
	private boolean gameOver;
	private int[][] rectangleWinner;
	private int[][] numAtomsInRectangle;
	private int gameGridSize, numPlayers, currentLevel;
	private Position initialPosition, currentPosition;
	private PositionLevelForBFS initialPositionLevel, currentPositionLevel;
	private LinkedList<Position> positionsQueue;
	private LinkedList<PositionLevelForBFS> positionsLevelForBFSQueue;
	
	private class PositionLevelForBFS {
		private int level;
		private Position position;
		
		private PositionLevelForBFS(Position pos, int lev) {
			position = pos;
			level = lev;
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

	public GameBoard(GameBoard board) {
		this.gameGridSize = board.gameGridSize;
		this.numPlayers = board.numPlayers;
		this.numAtomsInRectangle = new int[this.gameGridSize][this.gameGridSize];
		this.rectangleWinner = new int[this.gameGridSize][this.gameGridSize];

		for (int i = 0; i < this.gameGridSize; ++i) {
			for (int j = 0; j < this.gameGridSize; ++j) {
				this.numAtomsInRectangle[i][j] = board.numAtomsInRectangle[i][j];
				this.rectangleWinner[i][j] = board.rectangleWinner[i][j];
			}
		}
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
		initialPosition = new Position(coordX, coordY);
		positionsQueue = new LinkedList<Position>();
		positionsQueue.add(initialPosition);
		gameOver = false;
		while (positionsQueue.peek() != null && !gameOver) {
			currentPosition = positionsQueue.poll();
			rectangleWinner[currentPosition.coordX][currentPosition.coordY] = player;

			// If the clicked box is corner-most
			if ((currentPosition.coordX == 0 && currentPosition.coordY == 0)
					|| (currentPosition.coordX == 0 && currentPosition.coordY == gameGridSize - 1)
					|| (currentPosition.coordX == gameGridSize - 1 && currentPosition.coordY == 0)
					|| (currentPosition.coordX == gameGridSize - 1 && currentPosition.coordY == gameGridSize - 1)) {
				if (numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] == 2) {
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] = 0;
					rectangleWinner[currentPosition.coordX][currentPosition.coordY] = -1;
					if (currentPosition.coordX == 0
							&& currentPosition.coordY == 0) {
						positionsQueue.add(new Position(
								currentPosition.coordX + 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY + 1));
					} else if (currentPosition.coordX == 0
							&& currentPosition.coordY == gameGridSize - 1) {
						positionsQueue.add(new Position(
								currentPosition.coordX + 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY - 1));
					} else if (currentPosition.coordX == gameGridSize - 1
							&& currentPosition.coordY == 0) {
						positionsQueue.add(new Position(
								currentPosition.coordX - 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY + 1));
					} else {
						positionsQueue.add(new Position(
								currentPosition.coordX - 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY - 1));
					}

				}
			} else if (currentPosition.coordX == 0
					|| currentPosition.coordY == 0
					|| currentPosition.coordX == gameGridSize - 1
					|| currentPosition.coordY == gameGridSize - 1) {
				if (numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] == 3) {
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] = 0;
					rectangleWinner[currentPosition.coordX][currentPosition.coordY] = -1;
					if (currentPosition.coordX == 0) {
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY + 1));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY - 1));
						positionsQueue.add(new Position(
								currentPosition.coordX + 1,
								currentPosition.coordY));
					} else if (currentPosition.coordY == 0) {
						positionsQueue.add(new Position(
								currentPosition.coordX - 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(
								currentPosition.coordX + 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY + 1));
					} else if (currentPosition.coordX == gameGridSize - 1) {
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY + 1));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY - 1));
						positionsQueue.add(new Position(
								currentPosition.coordX - 1,
								currentPosition.coordY));
					} else {
						positionsQueue.add(new Position(
								currentPosition.coordX - 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(
								currentPosition.coordX + 1,
								currentPosition.coordY));
						positionsQueue.add(new Position(currentPosition.coordX,
								currentPosition.coordY - 1));
					}
				}
			} else {
				if (numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] == 4) {
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY] = 0;
					rectangleWinner[currentPosition.coordX][currentPosition.coordY] = -1;
					positionsQueue.add(new Position(currentPosition.coordX - 1,
							currentPosition.coordY));
					numAtomsInRectangle[currentPosition.coordX - 1][currentPosition.coordY] += 1;
					positionsQueue.add(new Position(currentPosition.coordX + 1,
							currentPosition.coordY));
					numAtomsInRectangle[currentPosition.coordX + 1][currentPosition.coordY] += 1;
					positionsQueue.add(new Position(currentPosition.coordX,
							currentPosition.coordY - 1));
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY - 1] += 1;
					positionsQueue.add(new Position(currentPosition.coordX,
							currentPosition.coordY + 1));
					numAtomsInRectangle[currentPosition.coordX][currentPosition.coordY + 1] += 1;
				}
			}
			gameOver = isWinningPosition(player);
		}
	}
	
	public void changeBoard2(int coordX, int coordY, int player) {
		initialPosition = new Position(coordX, coordY);
		positionsLevelForBFSQueue = new LinkedList<PositionLevelForBFS>();
		initialPositionLevel = new PositionLevelForBFS(initialPosition, 0);
		positionsLevelForBFSQueue.add(initialPositionLevel);
		gameOver = false;
		numAtomsInRectangle[coordX][coordY] += 1;
		rectangleWinner[coordX][coordY] = player;
		currentLevel = 0;
	}
	
	public boolean nextBoard(int player) {
		if (positionsLevelForBFSQueue.peek() != null && !gameOver) {
			while(positionsLevelForBFSQueue.peek() != null && positionsLevelForBFSQueue.peek().level == currentLevel) {
				currentPositionLevel = positionsLevelForBFSQueue.poll();
				// If the clicked box is corner-most
				if ((currentPositionLevel.position.coordX == 0 && currentPositionLevel.position.coordY == 0)
						|| (currentPositionLevel.position.coordX == 0 && currentPositionLevel.position.coordY == gameGridSize - 1)
						|| (currentPositionLevel.position.coordX == gameGridSize - 1 && currentPositionLevel.position.coordY == 0)
						|| (currentPositionLevel.position.coordX == gameGridSize - 1 && currentPositionLevel.position.coordY == gameGridSize - 1)) {
					if (numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] >= 2) {
						numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] -= 2;
						if (numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] == 0) {
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] = -1;
						}
						if (currentPositionLevel.position.coordX == 0
								&& currentPositionLevel.position.coordY == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] = player;
						} else if (currentPositionLevel.position.coordX == 0
								&& currentPositionLevel.position.coordY == gameGridSize - 1) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] = player;
						} else if (currentPositionLevel.position.coordX == gameGridSize - 1
								&& currentPositionLevel.position.coordY == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] = player;
						} else {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] = player;
						}
					}
				} else if (currentPositionLevel.position.coordX == 0
						|| currentPositionLevel.position.coordY == 0
						|| currentPositionLevel.position.coordX == gameGridSize - 1
						|| currentPositionLevel.position.coordY == gameGridSize - 1) {
					if (numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] >= 3) {
						numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] -= 3;
						if (numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] == 0) {
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] = -1;
						}
						if (currentPositionLevel.position.coordX == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] = player;
						} else if (currentPositionLevel.position.coordY == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] = player;
						} else if (currentPositionLevel.position.coordX == gameGridSize - 1) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] = player;
						} else {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] += 1;
							rectangleWinner[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] = player;
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] += 1;
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] = player;
						}
					}
				} else {
					if (numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] >= 4) {
						numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] -= 4;
						if (numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] == 0) {
							rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY] = -1;
						}
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX - 1,
								currentPositionLevel.position.coordY), currentPositionLevel.level+1));
						numAtomsInRectangle[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] += 1;
						rectangleWinner[currentPositionLevel.position.coordX - 1][currentPositionLevel.position.coordY] = player;
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX + 1,
								currentPositionLevel.position.coordY), currentPositionLevel.level+1));
						numAtomsInRectangle[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] += 1;
						rectangleWinner[currentPositionLevel.position.coordX + 1][currentPositionLevel.position.coordY] = player;
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX,
								currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
						numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] += 1;
						rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY - 1] = player;
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX,
								currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
						numAtomsInRectangle[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] += 1;
						rectangleWinner[currentPositionLevel.position.coordX][currentPositionLevel.position.coordY + 1] = player;
					}
				}
				gameOver = isWinningPosition(player);
			}
			currentLevel += 1;
			return false;
		}
		return true;
	}

	// This function returns whether the given board is a
	// winning board for the given player.
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

	// This function returns the score for a given player
	// in the board's current state.
	public double score(int player) {
		int runningNumBallsTotal = 0, runningNumBoxesTotal = 0;
		double numBallsWeight = 0.5, numBoxesWeight = 0.5, totalScore;
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (rectangleWinner[i][j] == player) {
					runningNumBallsTotal += numAtomsInRectangle[i][j];
					runningNumBoxesTotal += 1;
				}
			}
		}
		totalScore = (numBallsWeight * runningNumBallsTotal)
				+ (numBoxesWeight * runningNumBoxesTotal);
		return totalScore;
	}

	// Getter function for returning the Game Grid Dimensions
	public int getGameGridSize() {
		return gameGridSize;
	}
	
	// Checks whether a given player has lost the game or not.
	public boolean hasLost(int player) {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (rectangleWinner[i][j] == player && numAtomsInRectangle[i][j] > 0) {
					return false;
				}
			}
		}
		return true;
	}

	// Prints the GameBoard's state for Debugging purposes.
	public void printBoard() {
		System.out.println("Rectangle Winners:");
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				System.out.print(rectangleWinner[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("Num Atoms:");
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				System.out.print(numAtomsInRectangle[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	public boolean isEqual(GameBoard gb) {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (numAtomsInRectangle[i][j] != gb.getNumAtomsInRectangle(i, j)) {
					return false;
				}
				if (rectangleWinner[i][j] != gb.getRectangleWinner(i, j)) {
					return false;
				}
			}
		}
		return true;
	}

}
