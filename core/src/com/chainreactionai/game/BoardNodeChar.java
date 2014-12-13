/**
 * 
 */
package com.chainreactionai.game;

/**
 * @author Parnami
 * 
 */

// Class to represent a GameBoard and all its related information.
public class BoardNodeChar implements Comparable<BoardNodeChar> {

	public GameBoardChar board;
	private double score, totalScore;
	public int level, player, heuristicNumber;
	public BoardNodeChar previous;

	BoardNodeChar(GameBoardChar gameBoard, int level, int player,
			BoardNodeChar previousBoard, int heuristicNumber) {
		board = gameBoard;
		this.level = level;
		this.player = player;
		this.heuristicNumber = heuristicNumber;
		previous = previousBoard;
	}

	public int compareTo(BoardNodeChar that) {
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
		score = board.score(player, heuristicNumber);
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
	// propagating from above.
	public void setSelfPropagatingScore(double prevScore) {
		totalScore = prevScore + score;
	}

	// This is for the moves made by the opponents who are playing
	// the game ie. subtracting the score of the node and the score
	// propagating from above.
	public void setOpponentPropagatingScore(double prevScore) {
		totalScore = prevScore - score;
	}

	// Returns the propagated score.
	public double getPropagatedScore() {
		return totalScore;
	}

	// Prints the GameBoard's state for Debugging purposes.
	public void printBoard() {
		board.printBoard();
	}
}