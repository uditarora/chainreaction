/**
 * 
 */
package com.chainreactionai.game;

import java.io.PrintWriter;


/**
 * @author udit
 *
 */
public class MonteCarlo {
	final private int NUMBER_OF_PLAYERS, GRID_SIZE_X, GRID_SIZE_Y;
	private int[] difficultyLevels, heuristicNumbers, playerWins;
	private boolean[] lostPlayer;
	//final private String OUT_FILE_PATH = "monteCarloResults.txt";
	//final private boolean DEBUG = false;
	private double percentageMovesSearched, incrementValForPercentageMovesSearched;
	private GameBoardChar gameBoard;
	private int currentPlayer;
	private boolean printEachFlag;
	private PrintWriter out;
	
	//Initialize the class with the configuration
	MonteCarlo(int numPlayers, int[] difficultyLevelList, int[] heuristicNumbers, boolean printEachFlag, PrintWriter out) {
		this.NUMBER_OF_PLAYERS = numPlayers;
		this.difficultyLevels = difficultyLevelList;
		this.heuristicNumbers = heuristicNumbers;
		lostPlayer = new boolean[numPlayers];
		playerWins = new int[numPlayers];
		for(int i = 0; i < numPlayers; i += 1) {
			playerWins[i] = 0;
		}
		GRID_SIZE_X = 6;
		GRID_SIZE_Y = 8;
		this.printEachFlag = printEachFlag;
		this.out = out;
	}
	
	// Run given number of simulations and save the output to a file
	public void runSimulations(int numSimulations) {
		boolean gameOver = false;
		int numberOfMovesPlayed = 0;
		
		// Print the configuration
		System.out.println("\nLevel "+difficultyLevels[0]+" vs Level "+difficultyLevels[1]);
		out.println("\nLevel "+difficultyLevels[0]+" vs Level "+difficultyLevels[1]);
		System.out.println("Number of Players: " + NUMBER_OF_PLAYERS);
		out.println("Number of Players: " + NUMBER_OF_PLAYERS);
		for(int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
			System.out.println("Player "+ (i+1)  +" - Difficulty Level: "+ difficultyLevels[i] +", HeuristicNumber: "+ heuristicNumbers[i]);
			out.println("Player "+ (i+1)  +" - Difficulty Level: "+ difficultyLevels[i] +", HeuristicNumber: "+ heuristicNumbers[i]);
		}
		
		for(int simulation = 1; simulation <= numSimulations; simulation += 1) {
			
			if (printEachFlag) {
				System.out.println("Simulation " + simulation + ": ");
//				out.println("Simulation " + simulation + ": ");
			}
			gameBoard = new GameBoardChar(GRID_SIZE_X, GRID_SIZE_Y, NUMBER_OF_PLAYERS);
			for(int i = 0; i < NUMBER_OF_PLAYERS; i += 1) {
				lostPlayer[i] = false;
			}
			gameOver = false;
			numberOfMovesPlayed = 0;
			
			//percentageMovesSearched = 1/(double)(maxPlyLevel);
			percentageMovesSearched = 1;
//			incrementValForPercentageMovesSearched = 1/(double)(3*maxPlyLevel*maxPlyLevel);
			incrementValForPercentageMovesSearched = 0;
			
			currentPlayer = 0;
			
			// Keep on playing till the game ends
			while (!gameOver) {
				// If the player has not lost the game yet
				if (lostPlayer[currentPlayer] == false) {
					// Initializing the GameSolver
					GameSolverChar solver = new GameSolverChar(gameBoard, currentPlayer,
							NUMBER_OF_PLAYERS, lostPlayer, getCurrentPlyLevel(difficultyLevels[currentPlayer]), percentageMovesSearched, heuristicNumbers[currentPlayer]);
	
					// Get the position where we should place the new atom
					// so that the GameSolver's best move is executed.
					solver.run();
					Position winningMove = solver.getAnswerPosition();
					if(winningMove == null) {
						System.out.println("Error Time.");
						out.println("Error Time.");
					}
					// Change the Board
					gameBoard.changeBoard(winningMove.coordX, winningMove.coordY, currentPlayer);

					numberOfMovesPlayed += 1;
					if (gameBoard.isWinningPosition(currentPlayer)
							&& numberOfMovesPlayed > 1) {
						gameOver = true;
						// Print the name of winning player for current simulation
						if (printEachFlag) {
							System.out.println("Player " + (currentPlayer+1) + " won in "+ numberOfMovesPlayed + " moves.");
//							out.println("Player " + (currentPlayer+1) + " won in "+ numberOfMovesPlayed + " moves.");
						}
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
			out.println("Player "+ (i+1) +" won "+ playerWins[i] +" times.");
		}
//		out.close();
	}

	private int getCurrentPlyLevel(int difficultyLevel) {
		if (difficultyLevel == 0 || difficultyLevel == 1)
			return difficultyLevel;
		double branchingFactor = gameBoard.getBranchingFactor(currentPlayer);
		
		if (difficultyLevel > 100)		// for DEBUG_CPU purposes
			return (difficultyLevel - 100);
		
		switch(difficultyLevel) {
		case 2:		// combination of 1ply and 2ply
			if (branchingFactor < 0.5)
				return 2;
			else
				return 1;
		case 3:		// combination of 1ply and 3ply
			if (branchingFactor < 0.5)
				return 3;
			else
				return 1;
		case 4:		// purely 3ply
			return 3;
		case 5:		// combination of 2 and 3 ply
			if (branchingFactor < 0.2)
				return 3;
			else
				return 2;
		case 6:		// purely 2ply
			return 2;
		case 7:		// combination of 2 and 4 ply
			if (branchingFactor < 0.4)
				return 4;
			else
				return 2;
		case 8:		// combination of 2 and 4 ply, with more chances for 4ply
			if (branchingFactor < 0.8)
				return 4;
			else
				return 2;
		case 9:		// purely 4ply
			return 4;
		case 10:
			return 4;		// purely 4ply with full search
			
		default:
			return difficultyLevel;				
		}
	}
}
