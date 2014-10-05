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
	private BoardNode initialBoardNode;
	private int mainPlayer, numPlayers;
	final private int MAX_PLY_LEVEL = 4;
	
	// Private class to represent a GameBoard and all its related information.
	private class BoardNode implements Comparable<BoardNode> {
		
		private GameBoard board;
		private double score, totalScore;
		private int level, player;
		private BoardNode previous;
		
		private BoardNode(GameBoard gameBoard, int level, int player, BoardNode previousBoard) {
			board = gameBoard;
			this.level = level;
			this.player = player;
			previous = previousBoard;
		}
		
		public int compareTo (BoardNode that)
        {
            if (this.score > that.score)
                return 1;
            else if (this.score < that.score)
                return -1;
            else
            	return 0;
        }
		
		// Sets the score of the BoardNode for a given player
		// by using the GameBoard's internal functions
		public void setScore() {
			score = board.score(player);
		}
		
		// This returns the level of depth at which this BoardNode
		// is in the search tree.
		public int getLevel() {
			return level;
		}
		
		// This returns the score of the node irrespective of the 
		// score propogating from above.
		public double getNodeScore() {
			return score;
		}
		
		// This is for the moves made by the player who is playing
		// the game ie. adding the score of the node and the score
		// propogating from above.
		public void setSelfPropogatingScore(double prevScore) {
			totalScore = prevScore + score;
		}
		
		// This is for the moves made by the opponents who are playing
		// the game ie. subtracting the score of the node and the score
		// propogating from above.
		public void setOpponentPropogatingScore(double prevScore) {
			totalScore = prevScore - score;
		}
		
		// Returns the propogated score.
		public double getPropogatedScore() {
			return totalScore;
		}
	}
	
	public GameSolver (GameBoard gameBoard, int player, int numberPlayers) {
		initialBoardNode = new BoardNode(gameBoard, 0, player, null);
		initialBoardNode.setScore();
		initialBoardNode.setSelfPropogatingScore(0);
		mainPlayer = player;
		numPlayers = numberPlayers;
	}
	
	//AI solver - Returns the best move
	public GameBoard getBestGameBoard() {
		BoardNode tempBoardNode, lastPlyBestBoardNode, solutionBoardNode;
		double lastPlyMaxScore = -99999999;
		LinkedList<BoardNode> possibleBoardNodeQueue = new LinkedList<BoardNode>();
		GameBoard tempGameBoard;
		possibleBoardNodeQueue.add(initialBoardNode);
		BoardNode currentBoardNode = possibleBoardNodeQueue.peek();
		while (true) {
			currentBoardNode = possibleBoardNodeQueue.poll();
			int currentLevel = currentBoardNode.getLevel();
			if(currentLevel == MAX_PLY_LEVEL) {
				break;
			}
			if (currentLevel%2 == 0) {
				for (GameBoard b: getAllPossibleMoves(currentBoardNode.board, mainPlayer)) {
					double temp = currentBoardNode.getPropogatedScore();
					tempBoardNode = new BoardNode(b, currentLevel+1, mainPlayer, currentBoardNode);
					tempBoardNode.setScore();
					tempBoardNode.setSelfPropogatingScore(temp);
					possibleBoardNodeQueue.add(tempBoardNode);
				}
			} else {
				tempBoardNode = currentBoardNode;
				int currentPlayer = currentBoardNode.player;
				for (int i = 0; i < numPlayers-1; i += 1) {
					currentPlayer = (currentPlayer+1) % numPlayers;
					double temp = tempBoardNode.getPropogatedScore();
					tempBoardNode = getBestPossibleMove(tempBoardNode, currentPlayer);
					tempBoardNode.setScore();
					tempBoardNode.setOpponentPropogatingScore(temp);
				}
				int nextPlayer = currentPlayer;
				possibleBoardNodeQueue.add(new BoardNode(tempGameBoard, currentLevel+1, nextPlayer, currentBoardNode));
				if((currentLevel == MAX_PLY_LEVEL - 1) && (tempBoardNode.getPropogatedScore()<lastPlyMaxScore)) {
					lastPlyBestBoardNode = tempBoardNode;
				}
			}
		}
		solutionBoardNode = getPredecessorNode(lastPlyBestBoardNode);
		return solutionBoardNode.board;
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
	
	// Returns the BoardNode which has the best possible move
	// played by the player passed as parameter.
	private GameBoard getBestPossibleMove(GameBoard oldGameBoard, int player) {
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
	
	// Returns the BoardNode which was the starting point for this
	// branch of search.
	private BoardNode getPredecessorNode (BoardNode board) {
		BoardNode tempBoardNode;
		tempBoardNode = board.previous;
		while(tempBoardNode != initialBoardNode) {
			board = tempBoardNode;
			tempBoardNode = tempBoardNode.previous;
		}
		return board;
	}
}