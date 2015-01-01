/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

/**
 * @author Kartik Parnami
 * 
 */

public class GameSolverChar implements Runnable {
	private BoardNodeChar initialBoardNode;
	private int mainPlayer, numPlayers, heuristicNumber;
	private int MAX_PLY_LEVEL = 3;
	private double percentageMovesSearched, takeThisMoveOrNot;
	private Random rand;
	private Position answerPosition;
	private boolean isThreadComplete;
	private boolean[] isLost;
	final private boolean DEBUG = false;
	
	// Class to keep the click coordinates which were done
	// by the player to get this GameBoard.
	private class GameBoardAndCoord {
		private GameBoardChar board;
		private Position position;
		
		private GameBoardAndCoord(GameBoardChar gb, int coordX, int coordY) {
			board = gb;
			position = new Position(coordX, coordY);
		}
	}
		
	// Constructor to initialize the GameSolver with a BoardNode
	// which has the current state as the state passed and the default maxPlyLevel.
	public GameSolverChar(GameBoardChar gameBoard, int player, int numberPlayers, boolean [] lostPlayer) {
		initialBoardNode = new BoardNodeChar(gameBoard, 0, player, null, 1);
		initialBoardNode.setScore();
		initialBoardNode.setSelfPropagatingScore(0);
		mainPlayer = player;
		numPlayers = numberPlayers;
		rand = new Random();
	}
	
	//Constructor to initialize the GameSovler with a custom maxPlyLevel
	public GameSolverChar(GameBoardChar gameBoard, int player, int numberPlayers, boolean [] lostPlayer, int maxPlyLevel, double percentageMovesSearched, int heuristicNumber) {
		initialBoardNode = new BoardNodeChar(gameBoard, 0, player, null, heuristicNumber);
		initialBoardNode.setScore();
		initialBoardNode.setSelfPropagatingScore(0);
		mainPlayer = player;
		numPlayers = numberPlayers;
		MAX_PLY_LEVEL = maxPlyLevel;
		this.percentageMovesSearched = percentageMovesSearched;
		this.heuristicNumber = heuristicNumber;
		rand = new Random();
		isThreadComplete = false;
		isLost = new boolean[lostPlayer.length];
		for (int i = 0; i < lostPlayer.length; i += 1) {
			isLost[i] = lostPlayer[i];
		}
	}

	// AI solver - Returns the best move for the given player using minimax
	// algorithm.
	public void run() {
		
		// Generate purely random move if MAX_PLY_LEVEL is 0
		if (MAX_PLY_LEVEL == 0) {
			int gridSizeX = initialBoardNode.board.getGameGridSizeX();
			int gridSizeY = initialBoardNode.board.getGameGridSizeY();
			int randX = rand.nextInt(gridSizeX), randY = rand.nextInt(gridSizeY);
			while (!initialBoardNode.board.isValidMove(randX, randY, mainPlayer)) {
				randX = rand.nextInt(gridSizeX);
				randY = rand.nextInt(gridSizeY);
			}
			answerPosition = new Position(randX, randY);
			isThreadComplete = true;
			return;
		}
		
		BoardNodeChar tempBoardNode, solutionBoardNode;
		ArrayList<BoardNodeChar> bestBoardNodesArr = new ArrayList<BoardNodeChar>();
		double lastPlyMaxScore = -99999999;
		int numberOfBestBoardNodes, chosenBestBoardNodeIndex;
		LinkedList<BoardNodeChar> possibleBoardNodeQueue = new LinkedList<BoardNodeChar>();
		possibleBoardNodeQueue.add(initialBoardNode);
		BoardNodeChar currentBoardNode = possibleBoardNodeQueue.peek();
		if (DEBUG)
			System.out.println("Starting the AI decision");
		while (possibleBoardNodeQueue.peek() != null) {
			currentBoardNode = possibleBoardNodeQueue.poll();
			int currentLevel = currentBoardNode.getLevel();
			if (DEBUG)
				System.out.println("Current Level is " + currentLevel);
			// Checking if MAX_PLY_LEVEL has been reached
			if (currentLevel == MAX_PLY_LEVEL) {
				break;
			}
			// If a winning move is found at the first level, play that move
			if (currentLevel == 1) {
				if (currentBoardNode.board.isWinningPosition(mainPlayer)) {
					bestBoardNodesArr.clear();
					bestBoardNodesArr.add(currentBoardNode);
					break;
				}
			}
			
			if (currentLevel % 2 == 0) {
				// This is where the user is playing his move
				// ie. the max part of minimax algorithm
				for (GameBoardChar b : getAllPossibleMoves(currentBoardNode.board,
						mainPlayer)) {
					double temp = currentBoardNode.getPropagatedScore();
					tempBoardNode = new BoardNodeChar(b, currentLevel + 1,
							mainPlayer, currentBoardNode, heuristicNumber);
					tempBoardNode.setScore();
					tempBoardNode.setSelfPropagatingScore(temp);
					possibleBoardNodeQueue.add(tempBoardNode);
					if ((currentLevel == MAX_PLY_LEVEL - 1)
							&& (tempBoardNode.getPropagatedScore() > lastPlyMaxScore)) {
						bestBoardNodesArr.clear();
						bestBoardNodesArr.add(tempBoardNode);
						lastPlyMaxScore = tempBoardNode.getPropagatedScore();
					} else if ((currentLevel == MAX_PLY_LEVEL - 1)
							&& (tempBoardNode.getPropagatedScore() == lastPlyMaxScore)) {
						bestBoardNodesArr.add(tempBoardNode);
					}
				}
			} else {
				// This is where the opponents will be made to
				// play their best moves.
				tempBoardNode = currentBoardNode;
				int currentPlayer = currentBoardNode.player;
				for (int i = 0; i < numPlayers - 1; i += 1) {
					// Giving the opportunity to all the players to give
					// their best move.
					currentPlayer = (currentPlayer + 1) % numPlayers;
					if (!isLost[currentPlayer]) {
						if (DEBUG)
							System.out.println("Current Player is: "
									+ currentPlayer);
						double temp = tempBoardNode.getPropagatedScore();
						tempBoardNode = getBestPossibleMove(tempBoardNode,
								currentPlayer);
						tempBoardNode.setScore();
						tempBoardNode.setOpponentPropagatingScore(temp);
					}
				}
				// Check if the main player has lost. If yes, decrease the score by 100000.
				if (currentLevel == 1) {
					if (tempBoardNode.board.hasLost(mainPlayer)) {
						tempBoardNode.setSelfPropagatingScore(-100000);
					}
				}
				int nextPlayer = currentPlayer;
				if (DEBUG)
					System.out.println("New node added with current level : " + (currentLevel + 1));
				possibleBoardNodeQueue.add(new BoardNodeChar(tempBoardNode.board,
						currentLevel + 1, nextPlayer, currentBoardNode, heuristicNumber));
				if ((currentLevel == MAX_PLY_LEVEL - 1)
						&& (tempBoardNode.getPropagatedScore() > lastPlyMaxScore)) {
					bestBoardNodesArr.clear();
					bestBoardNodesArr.add(tempBoardNode);
					lastPlyMaxScore = tempBoardNode.getPropagatedScore();
				} else if ((currentLevel == MAX_PLY_LEVEL - 1)
						&& (tempBoardNode.getPropagatedScore() == lastPlyMaxScore)) {
					bestBoardNodesArr.add(tempBoardNode);
				}
			}
		}
		if (DEBUG)
			System.out.println("AI has decided the winning Position.");
		// Picks a random move out of the possible Best Moves and retraces
		// it to the top to get the coordinates of the desired click.
		numberOfBestBoardNodes = bestBoardNodesArr.size();
		
		// Generate purely random move if no solution is found
		if (numberOfBestBoardNodes == 0) {
			int gridSizeX = initialBoardNode.board.getGameGridSizeX();
			int gridSizeY = initialBoardNode.board.getGameGridSizeY();
			int randX = rand.nextInt(gridSizeX), randY = rand.nextInt(gridSizeY);
			while (!initialBoardNode.board.isValidMove(randX, randY, mainPlayer)) {
				randX = rand.nextInt(gridSizeX);
				randY = rand.nextInt(gridSizeY);
			}
			answerPosition = new Position(randX, randY);
			isThreadComplete = true;
		}
		else {
			chosenBestBoardNodeIndex = rand.nextInt(numberOfBestBoardNodes);
			solutionBoardNode = getPredecessorNode(bestBoardNodesArr
					.get(chosenBestBoardNodeIndex));

			if (DEBUG)
				System.out.println("Solution boardNode selected");
			for (GameBoardAndCoord gbc: getAllPossibleMovesWithCoords(initialBoardNode.board, mainPlayer)) {
				if (DEBUG)
					System.out.println("Iterating.");
				if (gbc.board.isEqual(solutionBoardNode.board)) {
					answerPosition = new Position(gbc.position.coordX, gbc.position.coordY);
					isThreadComplete = true;
					break;
				}
			}
		}
	}

	// Returns a list of all possible board positions from a
	// given board for the given player
	private Iterable<GameBoardChar> getAllPossibleMoves(GameBoardChar board, int player) {
		Stack<GameBoardChar> possibleMoves = new Stack<GameBoardChar>();
		GameBoardChar tempGameBoard;
		for (int i = 0; i < board.getGameGridSizeX(); ++i) {
			for (int j = 0; j < board.getGameGridSizeY(); ++j) {
				if (board.isValidMove(i, j, player)) {
					takeThisMoveOrNot = rand.nextDouble();
					if (board.isNodeFilled(player, i, j)) {
						takeThisMoveOrNot = 0;
					}
					if (takeThisMoveOrNot <= percentageMovesSearched) {
						tempGameBoard = new GameBoardChar(board);
						tempGameBoard.changeBoard(i, j, player);
						possibleMoves.add(tempGameBoard);
					}
				}
			}
		}
		return possibleMoves;
	}

	// Returns a list of all possible board positions from a
	// given board for the given player along with the coords
	// of the click used to get there.
	private Iterable<GameBoardAndCoord> getAllPossibleMovesWithCoords(GameBoardChar board, int player) {
		Stack<GameBoardAndCoord> possibleMoves = new Stack<GameBoardAndCoord>();
		GameBoardAndCoord tempGameBoardAndCoord;
		for (int i = 0; i < board.getGameGridSizeX(); ++i) {
			for (int j = 0; j < board.getGameGridSizeY(); ++j) {
				if (board.isValidMove(i, j, player)) {
					tempGameBoardAndCoord = new GameBoardAndCoord(new GameBoardChar(board), i, j);
					tempGameBoardAndCoord.board.changeBoard(i, j, player);
					possibleMoves.add(tempGameBoardAndCoord);
				}
			}
		}
		return possibleMoves;
	}
	
	// Returns the BoardNode which has the best possible move
	// played by the player passed as parameter.
	private BoardNodeChar getBestPossibleMove(BoardNodeChar oldBoardNode, int player) {
		GameBoardChar solutionGameBoard = oldBoardNode.board;
		double maxScore = 0, currentScore = 0;
		for (GameBoardChar board : getAllPossibleMoves(oldBoardNode.board, player)) {
			// If a winning position is found, assume that the opponent will play that move only.
			if (board.isWinningPosition(player)) {
				solutionGameBoard = board;
				break;
			}
			currentScore = board.score(player, 1);
			if (currentScore > maxScore) {
				maxScore = currentScore;
				solutionGameBoard = board;
			}
		}
		return new BoardNodeChar(solutionGameBoard, oldBoardNode.level + 1, player,
				oldBoardNode, heuristicNumber);
	}

	// Returns the BoardNode which was the starting point for this
	// branch of search.
	private BoardNodeChar getPredecessorNode(BoardNodeChar board) {
		BoardNodeChar tempBoardNode;
		tempBoardNode = board.previous;
		while (tempBoardNode != initialBoardNode) {
			board = tempBoardNode;
			tempBoardNode = tempBoardNode.previous;
		}
		return board;
	}
	
	public boolean getIsThreadComplete () {
		return isThreadComplete;
	}
	
	public Position getAnswerPosition () {
		return answerPosition;
	}
}