package ticTacToe;

import java.util.Arrays;
import java.util.Scanner;

/**
 * For Java programming class, Tic Tac Toe game for two players.
 * 
 * @author Samuel Shapiro <samuel.shapiro123@gmail.com>
 *
 */

public class TicTacToeGame {
	static Scanner scanner; //scanner is used to get input on placement from users
	private Scanner scan; //scanner used to fix errors in input
	
	public enum Fill {EMPTY, X, O, TIE} //enum Fill places either an X or an O in a cell
	
	private int size; //determines size of the board. defaults to 3
	private Fill[][] board; // the board is an array of Fills (X's and O's)
	private Fill winner; //determines winner of game
	private String placement; //input String for places a Fill
	private int[] pos; //int version of placement
	public int turnCount;
	
	/**
	 * Constructor. Defaults to a 3x3 game with no winner.
	 */
	public TicTacToeGame() {
		setSize(3);
		setBoard(getSize());
		setWinner(Fill.EMPTY);
	}
	
	/**
	 * Constructor with variable board size.
	 * @param size - size of board
	 */
	public TicTacToeGame(int size) {
		setSize(size);
		setBoard(getSize());
		setWinner(Fill.EMPTY);
	}
	
	public static void main(String[] args) {
		TicTacToeGame game = new TicTacToeGame();
		scanner = new Scanner(System.in);
		game.setTurnCount(0); //used to check if board is full
		do { //until a winner is determined, keep asking for placements
			game.printBoard();
			game.askPlayer(1, scanner);
			game.setTurnCount(game.getTurnCount()+1);
			
			System.out.print("\n\n\n\n\n\n"); //prints a bunch of empty lines to "clear" the console
			
			if (game.getTurnCount() == game.getSize() * game.getSize()) //if all spaces have been filled, game is a Tie
				game.setWinner(Fill.TIE);
			
			if (game.getWinner() != Fill.EMPTY) //if player 1 wins, don't ask player 2
				break;
			
			//repeats the above placement, but with O instead of X
			game.printBoard();
			game.askPlayer(2, scanner);
			game.setTurnCount(game.getTurnCount()+1);
			
			System.out.print("\n\n\n\n\n\n");
			
			if (game.getTurnCount() == game.getSize() * game.getSize())
				game.setWinner(Fill.TIE);
			
			
		} while (game.getWinner() == Fill.EMPTY);
		
		game.printBoard();
		if (game.getWinner() == Fill.TIE)
			System.out.println("Game is a tie. No winner.");
		else {
			System.out.printf("Player %s wins!%n",
					(game.getWinner().equals(Fill.X) ? "1" : "2")); //if winner is X, player 1 wins
																	//if winner is O, player 2 wins
		}
		game.closeScanners();
	}
	
	/**
	 * method used to close all open scanners at end of game
	 */
	public void closeScanners() {
		scanner.close();
		try { //if scan was never opened, don't close it
			scan.close();
		}
		catch(NullPointerException npe) {
		}
	}
	
	/**
	 * Method used to get player input
	 * @param player - either 1 or 2, depending on whose turn it is
	 * @param scan - scanner used for input
	 */
	public void askPlayer(int player, Scanner scan) {
		System.out.printf("Player %d, enter a row/column position: ", player);
		placement = scan.nextLine();
		pos = setPos(placement);
		
		//if attempting to place in a non-empty spot, gets new input
		while (getBoard()[pos[0]][pos[1]] != Fill.EMPTY) {
			System.out.print("That space is already filled. Try again: ");
			placement = scan.nextLine();
			pos = setPos(placement);
		}
		
		getBoard()[pos[0]][pos[1]] = (player == 1 ? Fill.X : Fill.O);
		checkWin(pos[0], pos[1]);
	}
	
	/**
	 * Method to convert String input to usable ints
	 * @param place - String of user input
	 * @return pos[] as int array version of input
	 */
	public int[] setPos(String place) {
		scan = new Scanner(System.in); //scan is used for additional inputs if error is found
		String[] arr = place.split(" "); //splits input into String numbers
		int count = arr.length;
		int[] pos = new int[count];
		for (int i = 0; i < count; i++) {
			try{
				pos[i] = Integer.parseInt(arr[i]);
			}
			//if input isn't actually a number, asks user to replace with a number
			catch(NumberFormatException nfe){
				System.out.printf("'%s' is not a number. Please enter a single coordinate: ", arr[i]);
				placement = scan.nextLine();
				arr[i] = placement;
				i = -1;
			}
		}
		
		//if there aren't 2 coordinates (row and column), gets new input
		if (pos.length != 2){ 
			System.out.print("Incorrect number of coordinates. Try again: ");
			placement = scan.nextLine();
			pos = setPos(placement);
		}
		
		//if row and column aren't in valid size range, gets new input
		if (pos[0] < 0 || pos[0] > getSize()-1 || pos[1] < 0 || pos[1] > getSize()-1) {
			System.out.print("That is not a valid row/column space. Try again: ");
			placement = scan.nextLine();
			pos = setPos(placement);
		}
		
		

		return pos;
	}
	
	/**
	 * Prints the current game board to the console. Prints column numbers along top
	 * and row numbers down the side.
	 */
	public void printBoard() {

		//prints coordinates above each column
		System.out.print("  ");
		for (int i = 0; i < getSize(); i ++) {
			System.out.printf(" %d  ", i);
		}
		System.out.println();
		
		for (int row = 0; row < getSize(); row++) {
			System.out.printf("%d ", row); //prints coordinates beside each row
			for (int col = 0; col < getSize(); col++) {
				printCell(getBoard()[row][col]); //prints what is in each cell of the board
				if (col != getSize()-1) {
					System.out.print("|");
				}
			}
			System.out.println();
			if (row != getSize() - 1) {
				System.out.print("  --");
				for (int i = 0; i < getSize(); i++) {
					System.out.print("---");
				}
				System.out.println();
			}
		}
	}
	
	/**
	 * Prints the contents of the cell
	 * @param content - enum Fill of what's in the given cell
	 */
	public void printCell(Fill content) {
		switch (content) {
		case X: System.out.print(" X "); break;
		case O: System.out.print(" O "); break;
		case TIE: System.out.print(" T "); break; //only used in SuperTicTacToe
		default: System.out.print("   ");
		}
	}
	
	/**
	 * Checks to see if the most recent move has won the game
	 * @param row - row coordinate of input
	 * @param col - column coordinate of input
	 */
	public void checkWin(int row, int col) {
		checkRow(row, col);
		checkCol(row, col);
		
		//if the input is on a diagonal line, check winner for that diagonal
		if (row == col)
			checkDiag(row, col);
		if (row == getSize()-1-col)
			checkAntiDiag(row,col);
	}
	
	/**
	 * Checks if the input's row is all the same Fill
	 */
	private void checkRow(int row, int col) {
		for (Fill box: getBoard()[row]) {
			if (box != getBoard()[row][col])
				return;
		}
		setWinner(getBoard()[row][col]);
	}
	
	/**
	 * Checks if the input's column is all the same Fill
	 */
	private void checkCol(int row, int col) {
		for (Fill[] box: getBoard()) {
			if (box[col] != getBoard()[row][col])
				return;
		}
		setWinner(getBoard()[row][col]);
	}
	
	/**
	 * If the input was on the top-left to bottom-right diagonal, check if that diagonal
	 * is all the same Fill
	 */
	private void checkDiag(int row, int col) {
		for (int i = 0; i < getSize(); i++){
			if (getBoard()[i][i] != getBoard()[row][col])
				return;
		}
		setWinner(getBoard()[row][col]);
	}
	
	/**
	 * If the input was on the top-right to bottom-left diagonal, check if that diagonal
	 * is all the same Fill
	 */
	private void checkAntiDiag(int row, int col) {
		for (int i = 0; i < getSize(); i++) {
			if (getBoard()[i][getSize()-1-i] != getBoard()[row][col])
				return;
		}
		setWinner(getBoard()[row][col]);
	}
	

	/**
	 * 
	 * getter and setter for size
	 */
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * getter and setter for the board.
	 * setter takes a size input and makes a square board of that size
	 * 
	 */
	public Fill[][] getBoard() {
		return board;
	}
	public void setBoard(int size) {
		this.board = new Fill[size][size];
		
		//fills the board with EMPTYs
		for (Fill[] row : board) {
			Arrays.fill(row, Fill.EMPTY);
		}
	}

	/**
	 * getter and setter for winner
	 * if any of the checkWin() submethods find a winner, sets it to be 
	 * the most recent input Fill
	 * 
	 */
	public Fill getWinner() {
		return winner;
	}
	public void setWinner(Fill winner) {
		this.winner = winner;
	}
	
	public int[] getPos() {
		return pos;
	}

	public int getTurnCount() {
		return turnCount;
	}

	public void setTurnCount(int turnCount) {
		this.turnCount = turnCount;
	}

}
