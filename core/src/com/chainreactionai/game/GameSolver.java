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
		lastPlyBestBoardNode = null;
		double lastPlyMaxScore = -99999999;
		LinkedList<BoardNode> possibleBoardNodeQueue = new LinkedList<BoardNode>();
//		GameBoard tempGameBoard;
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
				possibleBoardNodeQueue.add(new BoardNode(tempBoardNode.board, currentLevel+1, nextPlayer, currentBoardNode));
				if ((currentLevel == MAX_PLY_LEVEL - 1) && (tempBoardNode.getPropogatedScore() > lastPlyMaxScore)) {
					lastPlyBestBoardNode = tempBoardNode;
				}
			}
		}
		solutionBoardNode = getPredecessorNode(lastPlyBestBoardNode);
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
		for (GameBoard board: getAllPossibleMoves(oldBoardNode.board, player)) {
			currentScore = board.score(player);
			if (currentScore > maxScore) {
				maxScore = currentScore;
				solutionGameBoard = board;
			}
		}
		return new BoardNode(solutionGameBoard, oldBoardNode.level + 1, player, oldBoardNode);
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