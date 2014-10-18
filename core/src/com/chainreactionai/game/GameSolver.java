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

public class GameSolver {
	private BoardNode initialBoardNode;
	private int mainPlayer, numPlayers;
	final private int MAX_PLY_LEVEL = 4;
	final private boolean DEBUG = false;

	// Constructor to initialize the GameSolver with a BoardNode
	// which has the current state as the state passed.
	public GameSolver(GameBoard gameBoard, int player, int numberPlayers) {
		initialBoardNode = new BoardNode(gameBoard, 0, player, null);
		initialBoardNode.setScore();
		initialBoardNode.setSelfPropagatingScore(0);
		mainPlayer = player;
		numPlayers = numberPlayers;
	}

	// AI solver - Returns the best move for the given player using minimax
	// algorithm.
	public GameBoard getBestGameBoard() {
		BoardNode tempBoardNode, solutionBoardNode;
		ArrayList<BoardNode> bestBoardNodesArr = new ArrayList<BoardNode>();
		double lastPlyMaxScore = -99999999;
		int numberOfBestBoardNodes, chosenBestBoardNodeIndex;
		Random rand = new Random();
		LinkedList<BoardNode> possibleBoardNodeQueue = new LinkedList<BoardNode>();
		possibleBoardNodeQueue.add(initialBoardNode);
		BoardNode currentBoardNode = possibleBoardNodeQueue.peek();
		while (true) {
			currentBoardNode = possibleBoardNodeQueue.poll();
			int currentLevel = currentBoardNode.getLevel();
			if (DEBUG)
				System.out.println("Current Level is " + currentLevel);
			// Checking if MAX_PLY_LEVEL has been reached
			if (currentLevel == MAX_PLY_LEVEL) {
				break;
			}
			if (currentLevel % 2 == 0) {
				// This is where the user is playing his move
				// ie. the max part of minimax algorithm
				for (GameBoard b : getAllPossibleMoves(currentBoardNode.board,
						mainPlayer)) {
					double temp = currentBoardNode.getPropagatedScore();
					tempBoardNode = new BoardNode(b, currentLevel + 1,
							mainPlayer, currentBoardNode);
					tempBoardNode.setScore();
					tempBoardNode.setSelfPropagatingScore(temp);
					possibleBoardNodeQueue.add(tempBoardNode);
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
					if (DEBUG)
						System.out.println("Current Player is: "
								+ currentPlayer);
					double temp = tempBoardNode.getPropagatedScore();
					tempBoardNode = getBestPossibleMove(tempBoardNode,
							currentPlayer);
					tempBoardNode.setScore();
					tempBoardNode.setOpponentPropagatingScore(temp);
				}
				int nextPlayer = currentPlayer;
				possibleBoardNodeQueue.add(new BoardNode(tempBoardNode.board,
						currentLevel + 1, nextPlayer, currentBoardNode));
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
		//
		numberOfBestBoardNodes = bestBoardNodesArr.size();
		chosenBestBoardNodeIndex = rand.nextInt(numberOfBestBoardNodes);
		solutionBoardNode = getPredecessorNode(bestBoardNodesArr
				.get(chosenBestBoardNodeIndex));
		return solutionBoardNode.board;
	}

	// Returns a list of all possible board positions from a
	// given board for the given player
	private Iterable<GameBoard> getAllPossibleMoves(GameBoard board, int player) {
		Stack<GameBoard> possibleMoves = new Stack<GameBoard>();
		GameBoard tempGameBoard;
		for (int i = 0; i < board.getGameGridSize(); ++i) {
			for (int j = 0; j < board.getGameGridSize(); ++j) {
				if (board.isValidMove(i, j, player)) {
					tempGameBoard = new GameBoard(board);
					tempGameBoard.changeBoard(i, j, player);
					possibleMoves.add(tempGameBoard);
				}
			}
		}
		return possibleMoves;
	}

	// Returns the BoardNode which has the best possible move
	// played by the player passed as parameter.
	private BoardNode getBestPossibleMove(BoardNode oldBoardNode, int player) {
		GameBoard solutionGameBoard = oldBoardNode.board;
		double maxScore = 0, currentScore = 0;
		for (GameBoard board : getAllPossibleMoves(oldBoardNode.board, player)) {
			currentScore = board.score(player);
			if (currentScore > maxScore) {
				maxScore = currentScore;
				solutionGameBoard = board;
			}
		}
		return new BoardNode(solutionGameBoard, oldBoardNode.level + 1, player,
				oldBoardNode);
	}

	// Returns the BoardNode which was the starting point for this
	// branch of search.
	private BoardNode getPredecessorNode(BoardNode board) {
		BoardNode tempBoardNode;
		tempBoardNode = board.previous;
		while (tempBoardNode != initialBoardNode) {
			board = tempBoardNode;
			tempBoardNode = tempBoardNode.previous;
		}
		return board;
	}
}