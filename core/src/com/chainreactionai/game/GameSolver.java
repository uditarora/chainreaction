/**
 * 
 */
package com.chainreactionai.game;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 *
 */

public class GameSolver {
//	private GameBoard currentGameBoard, initialGameBoard;
	private BoardNode initialBoardNode;
//	private int currentPlayer, currentLevel, mainPlayer, numPlayers;
	private int mainPlayer, numPlayers;
	final private int MAX_PLY_LEVEL = 4;
//	private Array<GameBoard> allPossibleMoves;
//	private HashMap<Integer, GameBoard> hm;
	
	private class BoardNode implements Comparable<BoardNode> {
		private GameBoard board;
		private double currentScore, totalScore;
		private int level, player;
		private BoardNode previous;
		private BoardNode(GameBoard gameBoard, int level, int player, BoardNode previousBoard) {
			board = gameBoard;
			this.level = level;
			this.player = player;
			currentScore = gameBoard.score(player);
			previous = previousBoard;
			//Not sure if this is correct. We may need to negate the score at one stage
			if (previous == null)
				totalScore = currentScore;
			else
				totalScore += currentScore;
		}
		public int compareTo (BoardNode that)
        {
            if (this.currentScore > that.currentScore)
                return 1;
            else if (this.currentScore < that.currentScore)
                return -1;
            else
            	return 0;
        }
	}
	
	public GameSolver (GameBoard gameBoard, int player, int numberPlayers) {
//		initialGameBoard = gameBoard;
		initialBoardNode = new BoardNode(gameBoard, 0, player, null);
//		mainPlayer = currentPlayer = player;
		mainPlayer = player;
		numPlayers = numberPlayers;
	}
	
//	public GameBoard getBestGameBoard() {
//		LinkedList<GameBoard> possibleGameBoardQueue = new LinkedList<GameBoard>();
//		LinkedList<Integer> possibleGameBoardDepthQueue = new LinkedList<Integer>();
//		GameBoard tempGameBoard;
//		possibleGameBoardQueue.add(initialGameBoard);
//		possibleGameBoardDepthQueue.add(0);
//		currentGameBoard = possibleGameBoardQueue.peek();
//		currentLevel = possibleGameBoardDepthQueue.peek();
//		while (true) {
//			currentGameBoard = possibleGameBoardQueue.poll();
//			currentLevel = possibleGameBoardDepthQueue.poll();
//			if(currentLevel == PLY_LEVEL) {
//				break;
//			}
//			if (currentLevel%2 == 0) {
//				allPossibleMoves = getAllPossibleMoves(currentGameBoard, mainPlayer);
//				for (GameBoard b: allPossibleMoves) {
//					b.setPrevGameBoard(currentGameBoard);
//					possibleGameBoardQueue.add(b);
//					possibleGameBoardDepthQueue.add(currentLevel+1);
//				}
//			} else {
//				currentPlayer = mainPlayer;
//				tempGameBoard = currentGameBoard;
//				for (int i = 0; i < numPlayers-1; i += 1) {
//					currentPlayer = (currentPlayer+1) % numPlayers;
//					tempGameBoard = playBestPossibleMove(tempGameBoard, currentPlayer);
//				}
//				possibleGameBoardQueue.add(tempGameBoard);
//				possibleGameBoardDepthQueue.add(currentLevel+1);
//				tempGameBoard.setPrevGameBoard(currentGameBoard);
//			}
//		}
//	}
	
	//AI solver - Returns the best move
	public GameBoard getBestGameBoard() {
		LinkedList<BoardNode> possibleBoardNodeQueue = new LinkedList<BoardNode>();
		GameBoard tempGameBoard;
		possibleBoardNodeQueue.add(initialBoardNode);
		BoardNode currentBoardNode = possibleBoardNodeQueue.peek();
		while (true) {
			currentBoardNode = possibleBoardNodeQueue.poll();
			int currentLevel = currentBoardNode.level;
			if(currentLevel == MAX_PLY_LEVEL) {
				break;
			}
			if (currentLevel%2 == 0) {
//				Array<GameBoard> allPossibleMoves = getAllPossibleMoves(currentBoardNode.board, mainPlayer);
				for (GameBoard b: getAllPossibleMoves(currentBoardNode.board, mainPlayer)) {
					int nextPlayer = (currentBoardNode.player + 1) % numPlayers;
					possibleBoardNodeQueue.add(new BoardNode(b, currentLevel+1, nextPlayer, currentBoardNode));
				}
			} else {
				tempGameBoard = currentBoardNode.board;
				int currentPlayer = currentBoardNode.player;
				for (int i = 0; i < numPlayers-1; i += 1) {
					currentPlayer = (currentPlayer+1) % numPlayers;
					tempGameBoard = getBestPossibleMove(tempGameBoard, currentPlayer);
				}
				int nextPlayer = currentPlayer;
				possibleBoardNodeQueue.add(new BoardNode(tempGameBoard, currentLevel+1, nextPlayer, currentBoardNode));
			}
		}
	}
	
	//Returns a list of all possible board positions from a given board for the given player
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
	
	private GameBoard getBestPossibleMove(GameBoard oldGameBoard, int player) {
		// Udit complete this.
		GameBoard solutionGameBoard = oldGameBoard;
		double maxScore = 0, currentScore = 0;
		for (GameBoard board: getAllPossibleMoves(oldGameBoard, player)) {
			currentScore = board.score(player);
			if (currentScore > maxScore) {
				maxScore = currentScore;
				solutionGameBoard = board;
			}
		}
		return solutionGameBoard;
	}
}