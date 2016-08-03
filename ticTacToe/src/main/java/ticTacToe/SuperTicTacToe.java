package ticTacToe;

import java.util.Scanner;

/**
 * Super Tic Tac Toe game for 2 players extends basic Tic Tac Toe.
 * 
 * In ST3, each "cell" of the game board contains a smaller board.
 * When a player makes a move, the next player moves to the "supercell"
 * matching where they just moved (e.g., if player 1 places an X in the top right cell
 * of the direct middle subgame, the player 2 then places an O in the top right subgame.
 * If a player would move to a subgame that is finished, they can instead move to 
 * any other available subgame. The goal is to win 3 subgames in a row.
 * 
 * Normally, each player can see every piece in every cell. Due to console limitations,
 * in this version they can only see the overall results and the contents of the current cell.
 * 
 * @author Samuel Shapiro <samuel.shapiro123@gmail.com>
 *
 */

public class SuperTicTacToe extends TicTacToeGame {

	static Scanner scanner; //scanner used to get input
	private TicTacToeGame[][] superBoard; //game board containing a 3x3 of Tic Tac Toe games
	private TicTacToeGame currCell; //currently active subgame
	private int[] superPos; //coordinates of current active subgame
	
	/**
	 * Constructor initializes a 3x3 Super Tic Tac Toe game with no winner.
	 */
	public SuperTicTacToe() {
		setSize(3);
		setSuperBoard(getSize());
		setWinner(Fill.EMPTY);
		superPos = new int[2];
	}
	
	public static void main(String[] args) {
		SuperTicTacToe game = new SuperTicTacToe();
		scanner = new Scanner(System.in);
		
		//the first player chooses which subgame to start in
		System.out.print("Enter the starting coordinates in the super board: ");
		String input = scanner.nextLine();
		game.setCurrCell(input);

		//until the game has a winner, keep playing
		do {
			//if the player is moving into a finished game, move somewhere else
			while (game.getCurrCell().getWinner() != Fill.EMPTY) {
				game.printBoard();
				System.out.print("That cell is complete. Choose somewhere else to move to: ");
				input = scanner.nextLine();
				game.setCurrCell(input);
			}
			game.printBoard();
			game.getCurrCell().askPlayer(1, scanner); //gets input from player 1 (X)

			
			System.out.print("\n\n\n\n\n\n");
			
			//checks if player 1 just won or tied. if so, end the 'do while' loop
			game.checkSuperWin(); 
			if (game.getWinner() != Fill.EMPTY)
				break;
			
			//moves the current cell to the equivalent of player 1's move
			game.setCurrCell(game.currCell.getPos());
			while (game.getCurrCell().getWinner() != Fill.EMPTY) {
				game.printBoard();
				System.out.print("That cell is complete. Choose somewhere else to move to: ");
				input = scanner.nextLine();
				game.setCurrCell(input);
			}
			
			//repeats as above, for player 2
			game.printBoard();
			game.getCurrCell().askPlayer(2, scanner);
			game.setCurrCell(game.currCell.getPos());
			
			System.out.print("\n\n\n\n\n\n");
			game.checkSuperWin();
			
		} while (game.getWinner() == Fill.EMPTY);
		game.printBoard();
		if (game.getWinner() == Fill.TIE)
			System.out.println("Game is a tie. No winner."); //if the game is a tie, say so
		else {
			System.out.printf("Player %s wins!%n",
					(game.getWinner().equals(Fill.X) ? "1" : "2")); //if winner is X, player 1 wins
																	//if winner is O, player 2 wins
		}									
		game.closeScanners(); //close all open scanners
	}
	
	/**
	 * prints the superboard and the current active board to the console
	 */
	public void printBoard() {
		System.out.print("  SUPER BOARD     ");
		for (int i = 0; i < getSize(); i ++) {
			System.out.printf(" %d  ", i); //prints column numbers along the top
		}
		System.out.println();
		
		for (int row = 0; row < getSize(); row++) {
			System.out.printf("%d ", row); //prints row numbers down the side
			for (int col = 0; col < getSize(); col ++) {
				if (row == superPos[0] && col == superPos[1]) {
					System.out.print("|||"); //prints '|||' into the current cell
				}
				else {
					//prints the results of each subgame in each other supercell
					printCell(superBoard[row][col].getWinner());
				}
				if (col != getSize()-1) {
					System.out.print("|");
				}
			}
			System.out.print("     ");
			for (int col = 0; col < getSize(); col++) {
				printCell(currCell.getBoard()[row][col]); //prints what is in each cell of the board
				if (col != getSize()-1) {
					System.out.print("|");
				}
			}
			if (row != getSize() - 1) {
				System.out.println("\n  -----------     -----------");
			}
			
		}
		System.out.println();
	}
	
	/**
	 * If every subgame is either won or tied with no overall winner,
	 * this method sets the winner to a Tie game.
	 */
	public void checkForTie() {
		for (int row = 0; row < getSize(); row++) {
			for (int col = 0; col < getSize(); col++) {
				if (superBoard[row][col].getWinner() == Fill.EMPTY)
					return;
			}
		}
		setWinner(Fill.TIE);
	}
	
	/**
	 * Method uses sub-methods to check if the overall game has a 3-in-a-row
	 * in the current cell's row, column, and diagonals after each move.
	 */
	public void checkSuperWin() {
		checkSuperRow();
		checkSuperCol();
		
		//check diagonals only if the current cell is on one
		if (superPos[0] == superPos[1])
			checkSuperDiag();
		if (superPos[0] == getSize()-1-superPos[1])
			checkSuperAntiDiag();
		
		//if nobody won this turn, check for a tie
		if (getWinner() == Fill.EMPTY)
			checkForTie();
	}
	
	/**
	 * method to check current supercell's row for win
	 */
	private void checkSuperRow() {
		for (TicTacToeGame box : superBoard[superPos[0]]) {
			if (box.getWinner() != currCell.getWinner())
				return;
		}
		setWinner(currCell.getWinner());
	}
	
	/**
	 * method to check current supercell's column for win
	 */
	private void checkSuperCol() {
		for (TicTacToeGame[] box : superBoard) {
			if (box[superPos[1]].getWinner() != currCell.getWinner())
				return;
		}
		setWinner(currCell.getWinner());
	}
	
	/**
	 * method to check top left to bottom right of supergame for win
	 */
	private void checkSuperDiag() {
		for (int i = 0; i < getSize(); i++) {
			if (superBoard[i][i].getWinner() != currCell.getWinner())
				return;
		}
		setWinner(currCell.getWinner());
	}
	
	/**
	 * method to check bottom left to top right of supergame for win
	 */
	private void checkSuperAntiDiag() {
		for (int i = 0; i < getSize(); i++) {
			if (superBoard[i][getSize()-1-i].getWinner() != currCell.getWinner())
				return;
		}
		setWinner(currCell.getWinner());
	}
	
	/**
	 * Getter and setter for superBoard
	 * 
	 * Setter takes in a size, then creates a size x size game, with
	 * each cell containing a size x size subgame
	 */
	public TicTacToeGame[][] getSuperBoard() {
		return superBoard;
	}
	public void setSuperBoard(int size) {
		this.superBoard = new TicTacToeGame[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				superBoard[i][j] = new TicTacToeGame(size); //places a game in each cell
				superBoard[i][j].setBoard(size); //defaults each game to be filled with EMPTY's
			}
			
		}
	}

	/**
	 * getter and setter for currentCell
	 * setter takes a string input then converts it to an int[] with setPos(),
	 * then puts that through second setter to turn input into coordinates
	 */
	public TicTacToeGame getCurrCell() {
		return currCell;
	}
	public void setCurrCell(String input) {
		this.superPos = setPos(input);
		setCurrCell(superPos);
	}
	public void setCurrCell(int[] pos) {
		this.superPos = pos;
		this.currCell = superBoard[this.superPos[0]][this.superPos[1]];
	}

	/**
	 * getter and setter for superPos
	 */
	public int[] getSuperPos() {
		return superPos;
	}
	public void setSuperPos(int[] superPos) {
		this.superPos = superPos;
	}

	
}
