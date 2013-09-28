package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Created with IntelliJ IDEA.
 * User: michal
 * Date: 9/28/13
 * Time: 5:14 PM
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Table(name = "mahgame")
public class Hex extends Model {

    @Lob
    private final int[][] board; // 2D Board. 0 - empty, 1 - Player 1, 2 - Player 2

    private final int n1;
    private final int n2; // height and width of board

    @Lob
    private final WeightedQuickUnionUF wquf; // Union Find data structure to keep track
    // of unions and calculate winner

    private int currentPlayer; // Current player in the game, initialised to 1

    /**
     * Constructor for Class Hex, creates 2D array representing board, initialises
     *
     * @param n1
     * @param n2
     */

    public Hex(int n1, int n2) // create N-by-N grid, with all sites blocked
    {
        this.n1 = n1;
        this.n2 = n2;
        currentPlayer = 1;

        // TODO: Create instance of board
        board = new int[n1][n2];
        for (int i = 0; i < n1; i++) {
            for (int j = 0; j < n2; j++) {
                board[i][j] = 0;
            }
        }

        // TODO: Create instance WeightedQuickUnionUF class
        wquf = new WeightedQuickUnionUF(n1 * n2 + 4); //4 special sites
        //wqu[0,1]=special sites for Player1 (imaginary East and West)
        //wqu[2,3]=special sites for Player2 (imaginary North and South)

    }

    /**
     * @param x - x coord on board
     * @param y - y coord on board
     */
   // @Override
    public void takeTurn(int x, int y) {
        //check coords are valid ranges: 0 >= x < n1 AND 0 >= y < n2
        boolean validCoords = (x < n1 && x >= 0 && y < n2 && y >= 0);

        //will not throw OutOfBounds because it will stop checking condition is coords are invalid ( AND operator )
        boolean emptySite = (validCoords && board[x][y] == 0);

        //check if location is free and set to player's value(1 or 2).
        if (emptySite) board[x][y] = currentPlayer;

        if (validCoords && emptySite) {
            //calculate location and neighbours location in WeightedQuickUnionUF data structure
            int indexInWQUF = boardToWquf(x, y);

            //create unions to special sites in WeightedQuickUnionUF that also contain current players value
            specialSitesCheck(x, y);

            //create unions to neighbour sites in WeightedQuickUnionUF that also contain current players value
            neighbourSitesCheck(x, y, indexInWQUF);
        }

        // in invalid coords or not empty site repeat players round
        if (!validCoords || !emptySite) nextPlayer("rpt");

            // if no winner get the next player
        else if (!isWinner()) nextPlayer("nxt");
    }//END takeTurn

    /**
     * @param x
     * @param y
     */
    private void specialSitesCheck(final int x, final int y) {
        if (currentPlayer == 1 && x == 0) {
            wquf.union(0, boardToWquf(x, y));
        }
        if (currentPlayer == 1 && x == n1 - 1) {
            wquf.union(1, boardToWquf(x, y));
        }

        if (currentPlayer == 2 && y == 0) {
            wquf.union(2, boardToWquf(x, y));
        }
        if (currentPlayer == 2 && y == n2 - 1) {
            wquf.union(3, boardToWquf(x, y));
        }
    }

    /**
     * @param x
     * @param y
     * @param indexInWQUF
     */
    private void neighbourSitesCheck(final int x, final int y, final int indexInWQUF) {
        if (y > 0 && board[x][y - 1] == currentPlayer) {
            wquf.union(boardToWquf(x, y - 1), indexInWQUF);
        }
        if (x < n1 - 1 && y < n2 - 1 && board[x + 1][y + 1] == currentPlayer) {
            wquf.union(boardToWquf(x + 1, y + 1), indexInWQUF);
        }
        if (x < n1 - 1 && board[x + 1][y] == currentPlayer) {
            wquf.union(boardToWquf(x + 1, y), indexInWQUF);
        }
        if (y < n2 - 1 && board[x][y + 1] == currentPlayer) {
            wquf.union(boardToWquf(x, y + 1), indexInWQUF);
        }
        if (x > 0 && y < n2 - 1 && board[x - 1][y + 1] == currentPlayer) {
            wquf.union(boardToWquf(x - 1, y + 1), indexInWQUF);
        }
        if (x > 0 && board[x - 1][y] == currentPlayer) {
            int one = boardToWquf(x - 1, y);
            wquf.union(one, indexInWQUF);
        }
    }

    /**
     * @param nextPlayer
     */
    private void nextPlayer(final String nextPlayer) {
        switch (nextPlayer) {
            case "nxt":
                setCurrentPlayer((currentPlayer == 2) ? 1 : 2);
                break;
            case "rpt":
                break;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see BoardGame#isWinner()
     */
    //@Override
    public boolean isWinner() {
        // TODO:check if there is a connection between either side of the board.
        // You can do this by using the 'virtual site' approach in the
        // percolation test.
        boolean isWinner = false;
        if (currentPlayer == 1) isWinner = wquf.connected(0, 1);
        if (currentPlayer == 2) isWinner = wquf.connected(2, 3);

        //printBoard();
        return isWinner;
    }

    /**
     * @param currentPlayer
     */
    private void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * @return
     */
   // @Override
    public int getCurrentPlayer() {
        return currentPlayer;
    }


    /**
     * @return
     */
    //@Override
    public int[][] getBoard() {
        return board;
    }

    /**
     * THIS IS OPTIONAL:
     * Modify the main method if you wish to suit your implementation.
     * This is just an example of a test implementation.
     * For example you may want to display the board after each turn.
     *
     * @param args
     */
//    public static void main(String[] args) {
//
//        BoardGame hexGame = new Hex(5, 5);
//
//        while (!hexGame.isWinner()) {
//            printBoard(hexGame.getBoard());
//            System.out.println("It's player " + hexGame.getCurrentPlayer() + "'s turn");
//            System.out.println("Enter x and y location:");
//            int x = StdIn.readInt();
//            int y = StdIn.readInt();
//
//            hexGame.takeTurn(x, y);
//
//        }
//
//        System.out.println("It's over. Player " + hexGame.getCurrentPlayer()
//                + " wins!");
//
//    }

    /**
     * @param x
     * @param y
     * @return
     */
    private int boardToWquf(int x, int y) {
        return x * n1 + y + 4;//+4 for 4 special sites (North-South-West-East)
    }

    /**
     *
     */
    private static void printBoard(int[][] board) {

        String str = "_____";
        for (int i = 0; i < board.length; i++) {
            String s = (i < 10) ? "0" : "";
            str += "|_" + s + i + "_";
        }
        str += "|";

        System.out.println(str);
        for (int i = 0; i < board[0].length; i++) {
            String s = (i < 10) ? "0" : "";
            System.out.print("|_" + s + i + "_");

            for (int j = 0; j < board.length; j++) {
                System.out.print("|__" + board[j][i] + "_");
            }
            System.out.println("|");
        }
    }

    public String getColor(int x, int y){
        String color="#dfdfdf";
        if (board[x][y]==1)color="blue";
        else if (board[x][y]==2)color="red";

        return color;
    }

}


