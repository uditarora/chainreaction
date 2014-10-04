/**
 * 
 */
package com.chainreactionai.game;

import java.util.HashMap;
import java.util.LinkedList;

import com.badlogic.gdx.utils.Array;

/**
 * @author Kartik Parnami
 *
 */

public class GameSolver {
	private GameBoard currentGameBoard, initialGameBoard;
	private int currentPlayer, currentLevel, mainPlayer, numPlayers;
	final private int PLY_LEVEL = 4;
	private Array<GameBoard> allPossibleMoves;
	private HashMap<Integer, GameBoard> hm;
	
	public GameSolver (GameBoard gameBoard, int player, int numberPlayers) {
		initialGameBoard = gameBoard;
		mainPlayer = currentPlayer = player;
		numPlayers = numberPlayers;
	}
	
	public GameBoard getBestGameBoard() {
		LinkedList<GameBoard> possibleGameBoardQueue = new LinkedList<GameBoard>();
		LinkedList<Integer> possibleGameBoardDepthQueue = new LinkedList<Integer>();
		GameBoard tempGameBoard;
		possibleGameBoardQueue.add(initialGameBoard);
		possibleGameBoardDepthQueue.add(0);
		currentGameBoard = possibleGameBoardQueue.peek();
		currentLevel = possibleGameBoardDepthQueue.peek();
		while (true) {
			currentGameBoard = possibleGameBoardQueue.poll();
			currentLevel = possibleGameBoardDepthQueue.poll();
			if(currentLevel == PLY_LEVEL) {
				break;
			}
			if (currentLevel%2 == 0) {
				allPossibleMoves = getAllPossibleMoves(currentGameBoard, mainPlayer);
				for (GameBoard b: allPossibleMoves) {
					b.setPrevGameBoard(currentGameBoard);
					possibleGameBoardQueue.add(b);
					possibleGameBoardDepthQueue.add(currentLevel+1);
				}
			} else {
				currentPlayer = mainPlayer;
				tempGameBoard = currentGameBoard;
				for (int i = 0; i < numPlayers-1; i += 1) {
					currentPlayer = (currentPlayer+1) % numPlayers;
					tempGameBoard = playBestPossibleMove(tempGameBoard, currentPlayer);
				}
				possibleGameBoardQueue.add(tempGameBoard);
				possibleGameBoardDepthQueue.add(currentLevel+1);
				tempGameBoard.setPrevGameBoard(currentGameBoard);
			}
		}
	}
	
	private Array<GameBoard> getAllPossibleMoves(int player) {
		
	}
	
	private GameBoard playBestPossibleMove(GameBoard oldGameBoard, int player) {
		// Udit complete this. 
		return solutionGameBoard;
	}	
}