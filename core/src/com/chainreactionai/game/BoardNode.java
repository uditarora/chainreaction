/**
 * 
 */
package com.chainreactionai.game;

/**
 * @author Parnami
 * 
 */

// Class to represent a GameBoard and all its related information.
public class BoardNode implements Comparable<BoardNode> {

	private GameBoard board;
	private double score, totalScore;
	private int level, player;
	private BoardNode previous;

	BoardNode(GameBoard gameBoard, int level, int player,
			BoardNode previousBoard) {
		board = gameBoard;
		this.level = level;
		this.player = player;
		previous = previousBoard;
	}

	public int compareTo(BoardNode that) {
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