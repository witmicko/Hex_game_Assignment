package models;


import play.db.jpa.Model;

import javax.persistence.Entity;

/**
 * Generic 2D Board Game interface.
 * This should NOT be altered.
 * @author Frank
 *
 */

public interface BoardGame {

	/**
	 * Allows the current player to take their turn 
	 * @param x - x coord on board
	 * @param y - y coord on board
	 */
	public void takeTurn(int x, int y);

	/**
	 * Return current player in control of the game
	 * @return int indcating current player
	 */
	public int getCurrentPlayer();

	
	/**
	 * Returns 2D array representing the current state of the board
	 * @return
	 */
	public int[][] getBoard();

	/**
	 * Returns boolean indicating if there's a winner.
	 * @return
	 */
	public boolean isWinner();

}