package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * This is my Hex class, there are many like it but this one is mine.
 * User: michal
 * Date: 9/28/13
 * Time: 5:14 PM
 */
@Entity
@Table(name = "mygame")
public class Hex extends Model {

    @Lob
    private final int[][] board; // 2D Board. 0 - empty, 1 - Player 1, 2 - Player 2

    private final int n1;
    private final int n2; // height and width of board

    @Lob
    private final WeightedQuickUnionUF wquf; // Union Find data structure to keep track
    // of unions and calculate winner

    private int currentPlayer; // Current player in the game, initialised to 1
    private final int WEST = 0;
    private final int EAST = 1;
    private final int NORTH = 2;
    private final int SOUTH = 3;
    private final int EMPTY = 0;
    private final int player1 = 1;
    private final int player2 = 2;

    /**
     * Parametrized constructor for the Hex class, initialises: empty gameBoard and wquf.
     * @param n1 width of the game board
     * @param n2 height of the game board
     */
    public Hex(int n1, int n2) // create N-by-N grid, with all sites blocked
    {
        this.n1 = n1;
        this.n2 = n2;
        currentPlayer = 1;

        // Create board and set all sites to EMPTY.
        board = new int[n1][n2];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                board[i][j] = EMPTY;
            }
        }
        //Create instance WeightedQuickUnionUF class
        wquf = new WeightedQuickUnionUF(n1 * n2 + 4); //4 special sites
    }

    /**
     * Handles current player`s move, checks if picked site is valid and empty, connects the site with appropriate sites.
     * @param x - x coord on board(hor)
     * @param y - y coord on board(ver)
     */
    public void takeTurn(int x, int y) {
        //check coords are valid
        boolean validCoords = (x < n1 && x >= 0 && y < n2 && y >= 0);

        //check if location is free and set to player's value(1 or 2).
        boolean isEmptySite = (validCoords && board[x][y] == EMPTY);

        if (isEmptySite) {
            board[x][y] = currentPlayer;
            int thisInWquf = boardToWquf(x, y);
            specialSitesCheck(x, y);
            neighbourSitesCheck(x, y, thisInWquf);

            // if no winner get the next player
            if (!isWinner()) nextPlayer();
        }
    }//END takeTurn

    /**
     * Checks if selected site is at the board`s edge, connects it to special sites if it does.
     * @param x - x coord on board(hor)
     * @param y - y coord on board(ver)
     */
    private void specialSitesCheck(final int x, final int y) {
        int thisSite=boardToWquf(x, y);

        if (currentPlayer == player1) {
            if (x == 0) wquf.union(WEST, thisSite);
            else if (x == n1 - 1) wquf.union(EAST, thisSite);
        } else{
            if (y == 0) wquf.union(NORTH, thisSite);
            else if (y == n2 - 1) wquf.union(SOUTH, thisSite);
        }
    }

    /**
     * Checks neighbor sites and connects to this site is they belong to current player.
     * @param x - x coord on board(hor)
     * @param y - y coord on board(ver)
     * @param thisInWquf
     */
    private void neighbourSitesCheck(final int x, final int y, final int thisInWquf) {

        if ((y > 0) && board[x][y - 1] == currentPlayer) {
            wquf.union(boardToWquf(x, y - 1), thisInWquf);
        }

        if ((y < n2 - 1) && board[x][y + 1] == currentPlayer) {
            wquf.union(boardToWquf(x, y + 1), thisInWquf);
        }
        // if x>=n1(width) there is no sites to the right of it.
        if ((x < n1 - 1)) {
            if ((y < n2 - 1) && (board[x + 1][y + 1]) == currentPlayer) {
                wquf.union(boardToWquf(x + 1, y + 1), thisInWquf);
            }
            if (board[x + 1][y] == currentPlayer) {
                wquf.union(boardToWquf(x + 1, y), thisInWquf);
            }
        }
        //if x <= 0 there is no sites to the left of it.
        if (x > 0) {
            if ((y < n2 - 1) && (board[x - 1][y + 1]) == currentPlayer) {
                wquf.union(boardToWquf(x - 1, y + 1), thisInWquf);
            }
            if (board[x - 1][y] == currentPlayer) {
                wquf.union(boardToWquf(x - 1, y), thisInWquf);
            }
        }
    }

    /**
     * Changes current player to next player.
     * @param
     */
    private void nextPlayer() {
        setCurrentPlayer((currentPlayer == player2) ? player1 : player2);
    }

    /*
    * (non-Javadoc)
    *
    * @see BoardGame#isWinner()
    */
    public boolean isWinner() {
        // check if there is a connection between either side of the board.
        // You can do this by using the 'virtual site' approach in the
        // percolation test.
        return wquf.connected(EAST, WEST) || wquf.connected(NORTH, SOUTH);
    }

    /**
     * Standard setter for current player.
     * @param currentPlayer
     */
    private void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Getter for current player
     * @return current player`s number
     */
    public int getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Getter for board
     * @return gameboard
     */
    public int[][] getBoard() {
        return board;
    }

    /**
     * Converts index of 2D array (board) into index of flat array in WQU
     * @param x board[x][y] x index of board
     * @param y board[x][y] y index of board
     * @return equivalent index in flat array
     */
    private int boardToWquf(int x, int y) {
        return x * n1 + y + 4;//+4 for 4 special sites (North-South-West-East)
    }

    /**
     * gets colour of given site
     * @param x board[x][y] x index of board
     * @param y board[x][y] y index of board
     * @return color
     */
    public String getColor(int x, int y) {
        String color = "#dfdfdf";
        if (board[x][y] == 1) color = "blue";
        else if (board[x][y] == 2) color = "red";

        return color;
    }
}


