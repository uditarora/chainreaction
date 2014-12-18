/**
 * This class is used to represent the game board and its
 * utility functions.
 */
package com.chainreactionai.game;

import java.util.LinkedList;

/**
 * @author Kartik Parnami
 */

public class GameBoardChar {
	
	private boolean gameOver;
	public char[][] rectangleWinner;
	public char[][] numAtomsInRectangle;
	private int gameGridSize, numPlayers, currentLevel;
	private Position initialPosition;
	private PositionLevelForBFS initialPositionLevel, currentPositionLevel;
	private LinkedList<PositionLevelForBFS> positionsLevelForBFSQueue;
	
	// This is the class which helps in BFS for animation.
	// The level variable helps in expanding one level at a time
	// before sending the board for drawing.
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
	GameBoardChar(int gridSize, int gameNumPlayers) {
		numPlayers = gameNumPlayers;
		gameGridSize = gridSize;
		rectangleWinner = new char[gameGridSize][gameGridSize];
		numAtomsInRectangle = new char[gameGridSize][gameGridSize];
		setDefaultRectangleWinners();
		setDefaultNumAtomsInRectangle();
	}

	// Another constructor which returns a clone of the passed GameBoard.
	public GameBoardChar(GameBoardChar board) {
		this.gameGridSize = board.gameGridSize;
		this.numPlayers = board.numPlayers;
		this.numAtomsInRectangle = new char[this.gameGridSize][this.gameGridSize];
		this.rectangleWinner = new char[this.gameGridSize][this.gameGridSize];

		for (int i = 0; i < this.gameGridSize; ++i) {
			for (int j = 0; j < this.gameGridSize; ++j) {
				this.numAtomsInRectangle[i][j] = board.numAtomsInRectangle[i][j];
				this.rectangleWinner[i][j] = board.rectangleWinner[i][j];
			}
		}
	}
	
	//
	public void cloneBoard(GameBoardChar board) {
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
				rectangleWinner[i][j] = '/';
			}
		}
	}

	// This function sets the default number of atoms
	// in the rectangles ie. 0
	private void setDefaultNumAtomsInRectangle() {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				numAtomsInRectangle[i][j] = '0';
			}
		}
	}

	// This function returns the current winning player
	// for the given rectangle.
	public int getRectangleWinner(int row, int col) {
		return (rectangleWinner[row][col] - '0');
	}

	// This function returns the number of atoms currently
	// present in a rectangle.
	public int getNumAtomsInRectangle(int row, int col) {
		return (numAtomsInRectangle[row][col] - '0');
	}
	
	public void setRectangleWinner(int row, int col, int winner) {
		rectangleWinner[row][col] = (char)(winner + (int)('0'));
	}
	
	public void setNumAtomsInRectangle(int row, int col, int number) {
		numAtomsInRectangle[row][col] = (char)(number + (int)('0'));
	}

	// This function checks whether the user-clicked move is
	// valid according to the current board position
	public boolean isValidMove(int coordX, int coordY, int player) {
		if ((getRectangleWinner(coordX, coordY) == -1)
				|| (getRectangleWinner(coordX, coordY) == player)) {
			return true;
		}
		return false;
	}

	// This function changes the board according to the
	// rectangle clicked by user and recursively calls
	// itself according to the input and the number of atoms
	// currently in the rectangle using BFS
	public void changeBoard(int coordX, int coordY, int player) {
		gameOver = isWinningPosition(player);
		if (gameOver) {
			return;
		}
		setRectangleWinner(coordX, coordY, player);
		setNumAtomsInRectangle(coordX, coordY, getNumAtomsInRectangle(coordX, coordY) + 1);
		// If the clicked box is corner-most
		if ((coordX == 0 && coordY == 0)
				|| (coordX == 0 && coordY == gameGridSize - 1)
				|| (coordX == gameGridSize - 1 && coordY == 0)
				|| (coordX == gameGridSize - 1 && coordY == gameGridSize - 1)) {
			if (getNumAtomsInRectangle(coordX, coordY) == 2) {
				setNumAtomsInRectangle(coordX, coordY, 0);
				setRectangleWinner(coordX, coordY, -1);
				if (coordX == 0 && coordY == 0) {
					changeBoard(coordX + 1, coordY, player);
					changeBoard(coordX, coordY + 1, player);
				} else if (coordX == 0 && coordY == gameGridSize - 1) {
					changeBoard(coordX + 1, coordY, player);
					changeBoard(coordX, coordY - 1, player);
				} else if (coordX == gameGridSize - 1 && coordY == 0) {
					changeBoard(coordX - 1, coordY, player);
					changeBoard(coordX, coordY + 1, player);
				} else {
					changeBoard(coordX - 1, coordY, player);
					changeBoard(coordX, coordY - 1, player);
				}

			}
		} else if (coordX == 0 || coordY == 0 || coordX == gameGridSize - 1
				|| coordY == gameGridSize - 1) {
			if (getNumAtomsInRectangle(coordX, coordY) == 3) {
				setNumAtomsInRectangle(coordX, coordY, 0);
				setRectangleWinner(coordX, coordY, -1);
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
			if (getNumAtomsInRectangle(coordX, coordY) == 4) {
				setNumAtomsInRectangle(coordX, coordY, 0);
				setRectangleWinner(coordX, coordY, -1);
				changeBoard(coordX - 1, coordY, player);
				changeBoard(coordX + 1, coordY, player);
				changeBoard(coordX, coordY - 1, player);
				changeBoard(coordX, coordY + 1, player);
			}
		}
	}

	// This is the initialization function for changing the GameBoard
	// with animation.
	public void changeBoard2(int coordX, int coordY, int player) {
		initialPosition = new Position(coordX, coordY);
		positionsLevelForBFSQueue = new LinkedList<PositionLevelForBFS>();
		initialPositionLevel = new PositionLevelForBFS(initialPosition, 0);
		positionsLevelForBFSQueue.add(initialPositionLevel);
		gameOver = false;
		setNumAtomsInRectangle(coordX, coordY, (getNumAtomsInRectangle(coordX, coordY) + 1));
		setRectangleWinner(coordX, coordY, player);
		currentLevel = 0;
	}
	
	// This board expands the board by one level of BFS and returns
	// to the drawing board to give animation effect. Also returns 
	// whether the BFS is complete yet or not.
	public boolean nextBoard(int player) {
		if (positionsLevelForBFSQueue.peek() != null && !gameOver) {
			while(positionsLevelForBFSQueue.peek() != null && positionsLevelForBFSQueue.peek().level == currentLevel) {
				currentPositionLevel = positionsLevelForBFSQueue.poll();
				// If the clicked box is corner-most
				if ((currentPositionLevel.position.coordX == 0 && currentPositionLevel.position.coordY == 0)
						|| (currentPositionLevel.position.coordX == 0 && currentPositionLevel.position.coordY == gameGridSize - 1)
						|| (currentPositionLevel.position.coordX == gameGridSize - 1 && currentPositionLevel.position.coordY == 0)
						|| (currentPositionLevel.position.coordX == gameGridSize - 1 && currentPositionLevel.position.coordY == gameGridSize - 1)) {
					if (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) >= 2) {
						setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY, (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) - 2));
						if (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) == 0) {
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY, -1);
						}
						if (currentPositionLevel.position.coordX == 0
								&& currentPositionLevel.position.coordY == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, player);
						} else if (currentPositionLevel.position.coordX == 0
								&& currentPositionLevel.position.coordY == gameGridSize - 1) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, player);
						} else if (currentPositionLevel.position.coordX == gameGridSize - 1
								&& currentPositionLevel.position.coordY == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, player);
						} else {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, player);
						}
					}
				} else if (currentPositionLevel.position.coordX == 0
						|| currentPositionLevel.position.coordY == 0
						|| currentPositionLevel.position.coordX == gameGridSize - 1
						|| currentPositionLevel.position.coordY == gameGridSize - 1) {
					if (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) >= 3) {
						setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) - 3);
						if (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) == 0) {
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY, -1);
						}
						if (currentPositionLevel.position.coordX == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, player);
						} else if (currentPositionLevel.position.coordY == 0) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, player);
						} else if (currentPositionLevel.position.coordX == gameGridSize - 1) {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, player);
						} else {
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX - 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX + 1,
									currentPositionLevel.position.coordY), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, player);
							positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
									currentPositionLevel.position.coordX,
									currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
							setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1) + 1);
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, player);
						}
					}
				} else {
					if (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) >= 4) {
						setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) - 4);
						if (getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY) == 0) {
							setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY, -1);
						}
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX - 1,
								currentPositionLevel.position.coordY), currentPositionLevel.level+1));
						setNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY) + 1);
						setRectangleWinner(currentPositionLevel.position.coordX - 1, currentPositionLevel.position.coordY, player);
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX + 1,
								currentPositionLevel.position.coordY), currentPositionLevel.level+1));
						setNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, getNumAtomsInRectangle(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY) + 1);
						setRectangleWinner(currentPositionLevel.position.coordX + 1, currentPositionLevel.position.coordY, player);
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX,
								currentPositionLevel.position.coordY - 1), currentPositionLevel.level+1));
						setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1) + 1);
						setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY - 1, player);
						positionsLevelForBFSQueue.add(new PositionLevelForBFS(new Position(
								currentPositionLevel.position.coordX,
								currentPositionLevel.position.coordY + 1), currentPositionLevel.level+1));
						setNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, getNumAtomsInRectangle(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1) + 1);
						setRectangleWinner(currentPositionLevel.position.coordX, currentPositionLevel.position.coordY + 1, player);
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
		int numWinning = 0;
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (getRectangleWinner(i, j) != player
						&& getRectangleWinner(i, j) != -1) {
					return false;
				}
				else if (getRectangleWinner(i, j) == player)
					numWinning += 1;
			}
		}
		if (numWinning > 0)
			return true;
		else
			return false;
	}

	// This function returns the score for a given player
	// in the board's current state.
	public double score(int player, int level) {
		double totalScore = 0;
		if (level == 0) {
			int runningNumBallsTotal = 0, runningNumBoxesTotal = 0;
			double numBallsWeight = 0.5, numBoxesWeight = 0.5;
			for (int i = 0; i < gameGridSize; i += 1) {
				for (int j = 0; j < gameGridSize; j += 1) {
					if (getRectangleWinner(i, j) == player) {
						runningNumBallsTotal += getNumAtomsInRectangle(i, j);
						runningNumBoxesTotal += 1;
					}
				}
			}
			totalScore = (numBallsWeight * runningNumBallsTotal)
					+ (numBoxesWeight * runningNumBoxesTotal);
		}
		else if (level == 1) {
			int runningNumBoxesTotal = 0, runningNumSplittableTotal = 0, runningNumMultipleTotal = 0, currNumAtoms = 0;
			// First Iteration
			//double numBoxesWeight = 0.3, numSplittableWeight = 0.3, numMultipleWeight = 0.4;
			// Second Iteration
			double numBoxesWeight = 0, numSplittableWeight = 0, numMultipleWeight = 1;
			for (int i = 0; i < gameGridSize; i += 1) {
				for (int j = 0; j < gameGridSize; j += 1) {
					if (getRectangleWinner(i, j) == player) {
						runningNumBoxesTotal += 1;
						currNumAtoms = getNumAtomsInRectangle(i, j);
						if (currNumAtoms > 1) {
							runningNumMultipleTotal += 1;
							if (isSplittableNode(i, j, player)) {
								runningNumSplittableTotal += 1;
							}
						}
						if ((i == 0 && j == 0)
								|| (i == 0 && j == gameGridSize - 1)
								|| (i == gameGridSize - 1 && j == 0)
								|| (i == gameGridSize - 1 && j == gameGridSize - 1)) {
							runningNumSplittableTotal += 1;
						}
					}
				}
			}
			totalScore = (numBoxesWeight * runningNumBoxesTotal) + (numMultipleWeight * runningNumMultipleTotal) +
					(numSplittableWeight * runningNumSplittableTotal);
		}
		else if (level == 2) {
			int runningNumBoxesTotal = 0, runningNumSplittableTotal = 0, runningNumMultipleTotal = 0, currNumAtoms = 0;
			// First Iteration
			//double numBoxesWeight = 0.1, numSplittableWeight = 0.5, numMultipleWeight = 0.4;
			//Second Iteration
			double numBoxesWeight = 0, numSplittableWeight = 1, numMultipleWeight = 0;
			for (int i = 0; i < gameGridSize; i += 1) {
				for (int j = 0; j < gameGridSize; j += 1) {
					if (getRectangleWinner(i, j) == player) {
						runningNumBoxesTotal += 1;
						currNumAtoms = getNumAtomsInRectangle(i, j);
						if (currNumAtoms > 1) {
							runningNumMultipleTotal += 1;
							if (isSplittableNode(i, j, player)) {
								runningNumSplittableTotal += 1;
							}
						}
						if ((i == 0 && j == 0)
								|| (i == 0 && j == gameGridSize - 1)
								|| (i == gameGridSize - 1 && j == 0)
								|| (i == gameGridSize - 1 && j == gameGridSize - 1)) {
							runningNumSplittableTotal += 1;
						}
					}
				}
			}
			totalScore = (numBoxesWeight * runningNumBoxesTotal) + (numMultipleWeight * runningNumMultipleTotal) +
					(numSplittableWeight * runningNumSplittableTotal);
		}
		else if (level == 3) {
			int runningNumBoxesTotal = 0, runningNumSplittableTotal = 0, runningNumMultipleTotal = 0, currNumAtoms = 0;
			// First Iteration
			//double numBoxesWeight = 0.5, numSplittableWeight = 0.2, numMultipleWeight = 0.3;
			// Second Iteration
			double numBoxesWeight = 1, numSplittableWeight = 0, numMultipleWeight = 0;
			for (int i = 0; i < gameGridSize; i += 1) {
				for (int j = 0; j < gameGridSize; j += 1) {
					if (getRectangleWinner(i, j) == player) {
						runningNumBoxesTotal += 1;
						currNumAtoms = getNumAtomsInRectangle(i, j);
						if (currNumAtoms > 1) {
							runningNumMultipleTotal += 1;
							if (isSplittableNode(i, j, player)) {
								runningNumSplittableTotal += 1;
							}
						}
						if ((i == 0 && j == 0)
								|| (i == 0 && j == gameGridSize - 1)
								|| (i == gameGridSize - 1 && j == 0)
								|| (i == gameGridSize - 1 && j == gameGridSize - 1)) {
							runningNumSplittableTotal += 1;
						}
					}
				}
			}
			totalScore = (numBoxesWeight * runningNumBoxesTotal) + (numMultipleWeight * runningNumMultipleTotal) +
					(numSplittableWeight * runningNumSplittableTotal);
		}
		else if (level == 12) {
			int runningNumBoxesTotal = 0, runningNumSplittableTotal = 0, runningNumMultipleTotal = 0, currNumAtoms = 0;
			// First Iteration
			double numBoxesWeight = 0.1, numSplittableWeight = 0.45, numMultipleWeight = 0.45;
			// Second Iteration
//			double numBoxesWeight = 1, numSplittableWeight = 0, numMultipleWeight = 0;
			for (int i = 0; i < gameGridSize; i += 1) {
				for (int j = 0; j < gameGridSize; j += 1) {
					if (getRectangleWinner(i, j) == player) {
						runningNumBoxesTotal += 1;
						currNumAtoms = getNumAtomsInRectangle(i, j);
						if (currNumAtoms > 1) {
							runningNumMultipleTotal += 1;
							if (isSplittableNode(i, j, player)) {
								runningNumSplittableTotal += 1;
								runningNumMultipleTotal -= 1;
							}
						}
						if ((i == 0 && j == 0)
								|| (i == 0 && j == gameGridSize - 1)
								|| (i == gameGridSize - 1 && j == 0)
								|| (i == gameGridSize - 1 && j == gameGridSize - 1)) {
							runningNumSplittableTotal += 1;
							runningNumMultipleTotal -= 1;
						}
					}
				}
			}
			totalScore = (numBoxesWeight * runningNumBoxesTotal) + (numMultipleWeight * runningNumMultipleTotal) +
					(numSplittableWeight * runningNumSplittableTotal);
		}
		else if (level == 20) {
			int runningNumBoxesTotal = 0, runningNumSplittableTotal = 0, runningNumMultipleTotal = 0, currNumAtoms = 0;
			// First Iteration
			double numBoxesWeight = 0.12, numSplittableWeight = 0.44, numMultipleWeight = 0.44;
			// Second Iteration
//			double numBoxesWeight = 1, numSplittableWeight = 0, numMultipleWeight = 0;
			for (int i = 0; i < gameGridSize; i += 1) {
				for (int j = 0; j < gameGridSize; j += 1) {
					if (getRectangleWinner(i, j) == player) {
						runningNumBoxesTotal += 1;
						currNumAtoms = getNumAtomsInRectangle(i, j);
						if (currNumAtoms > 1) {
							runningNumMultipleTotal += 1;
							if (isSplittableNode(i, j, player)) {
								runningNumSplittableTotal += 1;
								runningNumMultipleTotal -= 1;
							}
						}
						if ((i == 0 && j == 0)
								|| (i == 0 && j == gameGridSize - 1)
								|| (i == gameGridSize - 1 && j == 0)
								|| (i == gameGridSize - 1 && j == gameGridSize - 1)) {
							runningNumSplittableTotal += 1;
							runningNumMultipleTotal -= 1;
						}
					}
				}
			}
			totalScore = (numBoxesWeight * runningNumBoxesTotal) + (numMultipleWeight * runningNumMultipleTotal) +
					(numSplittableWeight * runningNumSplittableTotal);
		}
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
				if (getRectangleWinner(i, j) == player && getNumAtomsInRectangle(i, j) > 0) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Returns the number of players playing the game
	public int getNumPlayers() {
		return numPlayers;
	}

	// Prints the GameBoard's state for Debugging purposes.
	public void printBoard() {
		System.out.println("Rectangle Winners:");
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				System.out.print(getRectangleWinner(i, j));
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println("Num Atoms:");
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				System.out.print(getNumAtomsInRectangle(i, j));
				System.out.print(" ");
			}
			System.out.println();
		}
	}
	
	// Checks if two GameBoards are equal or not.
	public boolean isEqual(GameBoardChar gb) {
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (getNumAtomsInRectangle(i, j) != gb.getNumAtomsInRectangle(i, j)) {
					return false;
				}
				if (getRectangleWinner(i, j) != gb.getRectangleWinner(i, j)) {
					return false;
				}
			}
		}
		return true;
	}
	
	// Returns if the node is having an atom of the player in question.
	public boolean isNodeFilled(int player, int coordX, int coordY) {
		if ((getNumAtomsInRectangle(coordX, coordY) > 0) && (getRectangleWinner(coordX, coordY) == player)) {
			return true;
		}
		return false;
	}
	
	// Returns if the node will split if a new atom is added to it.
	public boolean isSplittableNode (int coordX, int coordY, int player) {
		if (getRectangleWinner(coordX, coordY) == player && getNumAtomsInRectangle(coordX, coordY) > 0) {
			if ((coordX == 0 && coordY == 0)
					|| (coordX == 0 && coordY == gameGridSize - 1)
					|| (coordX == gameGridSize - 1 && coordY == 0)
					|| (coordX == gameGridSize - 1 && coordY == gameGridSize - 1)) {
				if (getNumAtomsInRectangle(coordX, coordY) == 1) {
					return true;
				}
			} else if (coordX == 0 || coordY == 0 || coordX == gameGridSize - 1
					|| coordY == gameGridSize - 1) {
				if (getNumAtomsInRectangle(coordX, coordY) == 2) {
					return true;
				}
			} else {
				if (getNumAtomsInRectangle(coordX, coordY) == 3) {
					return true;
				}
			}
			return false;
		} else {
			return false;
		}
	}
	
	// This function returns the possible branching for
	// a player in the current board scope.
	public double getBranchingFactor (int player) {
		double branchingFactor = 0;
		for (int i = 0; i < gameGridSize; i += 1) {
			for (int j = 0; j < gameGridSize; j += 1) {
				if (isValidMove(i, j, player)) {
					branchingFactor += 1;
				}
			}
		}
		branchingFactor /= (gameGridSize*gameGridSize);
		return branchingFactor;
	}
	
}