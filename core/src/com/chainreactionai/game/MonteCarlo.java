/**
 * 
 */
package com.chainreactionai.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

/**
 * @author udit
 *
 */
public class MonteCarlo {
	final private int NUMBER_OF_PLAYERS, GRID_SIZE;
	private int[] maxPlyLevels, heuristicNumbers, playerWins;
	private boolean[] lostPlayer;
	final private String OUT_FILE_PATH = "monteCarloResults.txt";
//	final private boolean DEBUG = false;
	private double percentageMovesSearched, incrementValForPercentageMovesSearched;
	
	//Initialize the class with the configuration
	MonteCarlo(int numPlayers, int[] plyLevelList, int[] heuristicNumbers) {
		this.NUMBER_OF_PLAYERS = numPlayers;
		this.maxPlyLevels = plyLevelList;
		this.heuristicNumbers = heuristicNumbers;
		lostPlayer = new boolean[numPlayers];
		playerWins = new int[numPlayers];
		for(int i = 0; i < numPlayers; i += 1) {
			playerWins[i] = 0;
		}
		GRID_SIZE = 7;
	}
	
	//Run given number of simulations and save the output to a file
	public void runSimulations(int numSimulations) {
		boolean gameOver = false;
		int numberOfMovesPlayed = 0;
		
		// Print the configuration
		System.out.println("Number of Players: " + NUMBER_OF_PLAYERS);
		for(int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			System.out.println("Player "+ (i+1)  +" - MaxPlyLevel: "+ maxPlyLevels[i] +", HeuristicNumber: "+ heuristicNumbers[i]);
		}
		
		for(int simulation = 1; simulation <= numSimulations; simulation += 1) {
			
			System.out.println("Simulation " + simulation + ": ");
			GameBoardChar gameBoard = new GameBoardChar(GRID_SIZE, NUMBER_OF_PLAYERS);
			for(int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				lostPlayer[i] = false;
			}
			gameOver = false;
			numberOfMovesPlayed = 0;
			
			//percentageMovesSearched = 1/(double)(maxPlyLevel);
			percentageMovesSearched = 1;
//			incrementValForPercentageMovesSearched = 1/(double)(3*maxPlyLevel*maxPlyLevel);
			incrementValForPercentageMovesSearched = 0;
			
			int currentPlayer = 0;
			
			// Keep on playing till the game ends
			while (!gameOver) {
				// If the player has not lost the game yet
				if (lostPlayer[currentPlayer] == false) {
					// Initializing the GameSolver
					GameSolverChar solver = new GameSolverChar(gameBoard, currentPlayer,
							NUMBER_OF_PLAYERS, lostPlayer, maxPlyLevels[currentPlayer], percentageMovesSearched, heuristicNumbers[currentPlayer]);
	
					// Get the position where we should place the new atom
					// so that the GameSolver's best move is executed.
					solver.run();
					Position winningMove = solver.getAnswerPosition();
					if(winningMove == null) {
						System.out.println("Error Time.");
					}
					// Change the Board
					gameBoard.changeBoard(winningMove.coordX, winningMove.coordY, currentPlayer);

					numberOfMovesPlayed += 1;
					if (gameBoard.isWinningPosition(currentPlayer)
							&& numberOfMovesPlayed > 1) {
						gameOver = true;
						// Print the name of winning player for current simulation
						System.out.println("Player " + (currentPlayer+1) + " won in "+ numberOfMovesPlayed + " moves.");
						playerWins[currentPlayer] += 1;
					}
					percentageMovesSearched += incrementValForPercentageMovesSearched;
					// Giving the chance to the next player to play.
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
					if (numberOfMovesPlayed > NUMBER_OF_PLAYERS) {
						for (int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
							if (!lostPlayer[i]) {
								if (gameBoard.hasLost(i)) {
									lostPlayer[i] = true;
								}
							}
						}
					}
				} else {
					// Giving the chance to the next player to play.
					currentPlayer = (currentPlayer + 1) % NUMBER_OF_PLAYERS;
				}
			}
		}
		
		// Print the final results
		for(int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			System.out.println("Player "+ (i+1) +" won "+ playerWins[i] +" times.");
		}
	}
}
