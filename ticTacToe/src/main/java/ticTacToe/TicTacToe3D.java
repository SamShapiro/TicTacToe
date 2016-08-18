package ticTacToe;

import java.util.Arrays;
import java.util.Scanner;

/**
 * 3D Tic Tac Toe for 2 players.
 * @author Samuel Shapiro <samuel.shapiro123@gmail.com>
 *
 */

public class TicTacToe3D extends TicTacToeGame {

	private TicTacToeGame[] board3D; //the game board is an array of tic tac toe games
	
	//special tic tac toe boards which are used to check for a winner
	private TicTacToeGame topDown;
	private TicTacToeGame side;
	
	//the four 3D diagonals that pass through the center of the board
	private static int[][] diagOne;
	private static int[][] diagTwo;
	private static int[][] diagThree;
	private static int[][] diagFour;
	
	private int[] pos3D; //int[] of [row, column, depth] of last move
	private int[] pos; //int[] of [row, column] of last move
	private String placement; //String used to get user input
	
	
	/**
	 * Constructor. initializes a new 4x4x4 board (consisting of 4 4x4 boards)
	 */
	public TicTacToe3D() {
		setSize(4);
		pos = new int[2];
		pos3D = new int[3];
		setBoard3D(getSize());
		setWinner(Fill.EMPTY);
		
		topDown = new TicTacToeGame(getSize());
		side = new TicTacToeGame(getSize());
		createDiags(getSize());
	}
	
	
	public static void main (String[] args) {
		scanner = new Scanner(System.in);
		TicTacToe3D game = new TicTacToe3D();
		
		do { //keep asking for moves until the game is over
			game.printBoard();
			game.askPlayer(1, scanner);
			
			System.out.print("\n\n\n\n\n\n");
			
			//if all boards are tied and there is no winner, the game is a tie
			if (game.board3D[0].getWinner() == Fill.TIE
					&& game.board3D[1].getWinner() == Fill.TIE
					&& game.board3D[2].getWinner() == Fill.TIE
					&& game.getWinner() == Fill.EMPTY)
				game.setWinner(Fill.TIE);
			
			if (game.getWinner() != Fill.EMPTY) //if player 1 wins, don't ask player 2
				break;
			
			//repeat as above, with player 2
			game.printBoard();
			game.askPlayer(2, scanner);
			
			System.out.print("\n\n\n\n\n\n");
			
			if (game.board3D[0].getWinner() == Fill.TIE
					&& game.board3D[1].getWinner() == Fill.TIE
					&& game.board3D[2].getWinner() == Fill.TIE
					&& game.getWinner() == Fill.EMPTY)
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
		
		game.closeScanners(); //close all open scanners
	}
	
	/**
	 * Overrides closeScanners from TicTacToeGame. Makes sure to close
	 * scanners in every open game.
	 */
	public void closeScanners() {
		scanner.close();
		for (TicTacToeGame dep: board3D) {
			dep.closeScanners();
		}
		topDown.closeScanners();
		side.closeScanners();
	}
	
	/**
	 * Overrides askPlayer. in addition to row and column, also asks for depth
	 */
	public void askPlayer(int player, Scanner scan) {
		System.out.printf("Player %d, enter a depth/row/column position: ", player);
		placement = scan.nextLine();
		String[] place = placement.split(" ");
		
		//ensures that correct number of arguments are given
		while (place.length != 3) {
			System.out.print("Incorrect number of arguments. Try again: ");
			place = scan.nextLine().split(" ");
		}
		String askPlacement = place[1] + " " + place[2]; //string used to get pos for game at depth
		
		//makes sure that depth is between 0 and 3
		while (place[0].matches("[0-3]") == false) {
			System.out.print("Depth is not valid. Replace depth: ");
			place[0] = scanner.next();
		}
		
		
		int depth = Integer.parseInt(place[0]);
		
		//prevents players from making a move on a full board
		while (board3D[depth].getWinner() == Fill.TIE) {
			System.out.print("That depth is full. Replace depth: ");
			depth = scanner.nextInt();
			while (depth < 0 || depth > getSize()) {
				System.out.print("Depth is not valid. Replace depth: ");
				depth = scanner.nextInt();
			}
		}

		pos = setPos(askPlacement);
		
		pos3D[0] = pos[0];
		pos3D[1] = pos[1];
		
		pos3D[2] = depth;
		
		//prevents a player from making a move on a filled space
		while (board3D[depth].getBoard()[pos3D[0]][pos3D[1]] != Fill.EMPTY) {
			//if space is filled, player can change row and column, but not depth
			System.out.print("That space is already filled. Give a new row/column position: ");
			placement = scan.nextLine();
			pos = setPos(placement);
		}
		
		//fills the selected space
		board3D[pos3D[2]].getBoard()[pos3D[0]][pos3D[1]] = (player == 1 ? Fill.X : Fill.O);
		
		//if player's move fills the board at depth, set that board to a tie
		board3D[depth].setTurnCount(board3D[depth].getTurnCount()+1);
		if (board3D[depth].getTurnCount() == getSize() * getSize())
			board3D[depth].setWinner(Fill.TIE);
		
		checkWin3D(); //check if the move won the game
		
	}
	
	/**
	 * Method prints all four boards to simulate a single 3D board
	 */
	public void printBoard() {
		System.out.print("  ");
		for (int i = 0; i < getSize(); i++) {
			for (int j = 0; j < getSize(); j++) {
				System.out.printf(" %d  ", j); //prints column numbers above each board
			}
			System.out.print("  ");
		}
		System.out.println();
		
		for (int row = 0; row < getSize(); row++) {
			System.out.printf("%d ", row); //prints row numbers down the left side
			for (int sub = 0; sub < getSize(); sub++) {
				for (int col = 0; col < getSize(); col++) {
					printCell(board3D[sub].getBoard()[row][col]);
					if (col != getSize() - 1) {
						System.out.print("|"); //divider between columns
					}
				}
				System.out.print("   ");
			}
			if (row != getSize() - 1) {
				//divider between rows
				System.out.println("\n   -------------     -------------     -------------     -------------");
			}
				
		}
		System.out.println();
	}
	
	/**
	 * Method used to determine if the latest move has won the game.
	 */
	public void checkWin3D() {
		
		//checks if the player has one on the front facing board of the current depth
		board3D[pos3D[2]].checkWin(pos3D[0], pos3D[1]);
		if (board3D[pos3D[2]].getWinner() == Fill.X || board3D[pos3D[2]].getWinner() == Fill.O) {
			setWinner(board3D[pos3D[2]].getWinner());
			return;
		}
		
		//checks if the player has one on the top-down view at the current row
		setTopDown();
		topDown.checkWin(pos3D[2], pos3D[0]);
		if (topDown.getWinner() == Fill.X || topDown.getWinner() == Fill.O) {
			setWinner(topDown.getWinner());
			return;
		}
		
		//checks if player has one on the side-facing view at the current column
		setSide();
		side.checkWin(pos3D[1], pos3D[2]);
		if (side.getWinner() == Fill.X || side.getWinner() == Fill.O) {
			setWinner(side.getWinner());
			return;
		}
		onDiag(); //checks if player has one on one of the 3D diagonals
		
	}
	
	/**
	 * Two method useds to create a new board by looking at the 3D board from above and
	 * from the side. These are used to check if the player has won on either of those 
	 * two planes.
	 */
	public void setTopDown() {
		for (int col = 0; col < getSize(); col++) {
			for (int dep = 0; dep < getSize(); dep++) {
				topDown.getBoard()[col][dep] = board3D[dep].getBoard()[pos3D[0]][col];
			}
		}
	}
	public void setSide() {
		for (int row = 0; row < getSize(); row++) {
			for (int dep = 0; dep < getSize(); dep++) {
				side.getBoard()[row][dep] = board3D[dep].getBoard()[row][pos3D[1]];
			}
		}
	}
	
	/**
	 * Checks if the last move was on one of the 3D diagonals. If so,
	 * checks if the move is a winning move on that diagonal.
	 */
	public void onDiag() {
		for (int[] ary : diagOne) {
			if (Arrays.equals(ary, pos3D)) {
				checkWinOnDiag(diagOne);
			}
		}
		for (int[] ary : diagTwo) {
			if (Arrays.equals(ary, pos3D)) {
				checkWinOnDiag(diagTwo);
			}
		}
		for (int[] ary : diagThree) {
			if (Arrays.equals(ary, pos3D)) {
				checkWinOnDiag(diagThree);
			}
		}
		for (int[] ary : diagFour) {
			if (Arrays.equals(ary, pos3D)) {
				checkWinOnDiag(diagFour);
			}
		}
	}
	
	
	/**
	 * Series of methods create four inner diagonals, based on size of board
	 * @param size size of 3D board
	 */
	private void createDiags(int size) {
		createDiagOne(size);
		createDiagTwo(size);
		createDiagThree(size);
		createDiagFour(size);
	}	
	private void createDiagOne(int size) {
		diagOne = new int[size][3];
		for (int i = 0; i < size; i++) {
			diagOne[i][0] = i;
			diagOne[i][1] = i;
			diagOne[i][2] = i;
		}
	}
	private void createDiagTwo(int size) {
		diagTwo = new int[size][3];
		for (int i = 0; i < size; i++) {
			diagTwo[i][0] = i;
			diagTwo[i][1] = (size - 1) - i;
			diagTwo[i][2] = i;
		}
	}
	private void createDiagThree(int size) {
		diagThree = new int[size][3];
		for (int i = 0; i < size; i++) {
			diagThree[i][0] = (size - 1) - i;
			diagThree[i][1] = i;
			diagThree[i][2] = i;
		}
	}
	private void createDiagFour(int size) {
		diagFour = new int[size][3];
		for (int i = 0; i < size; i++) {
			diagFour[i][0] = (size - 1) - i;
			diagFour[i][1] = (size - 1) - i;
			diagFour[i][2] = i;
		}
	}
	
	
	/**
	 * Method used to check if the player has one on the given diagonal
	 * @param diag - array of coordinates of each point on the diagonal
	 */
	public void checkWinOnDiag(int[][] diag) {
		
		//if any of the points on the diagonal do not match the latest move, it's not a
		//winning move, and the check ends
		for (int[] cell : diag) {
			if (board3D[cell[2]].getBoard()[cell[0]][cell[1]] != board3D[pos3D[2]].getBoard()[pos3D[0]][pos3D[1]])
				return;
		}
		setWinner(board3D[pos3D[2]].getBoard()[pos3D[0]][pos3D[1]]);
	}


	/**
	 * getter and setter for board3D
	 * setter takes in a size and returns an cubic board of that size
	 */
	public TicTacToeGame[] getBoard3D() {
		return board3D;
	}
	public void setBoard3D(int size) {
		board3D = new TicTacToeGame[size];
		for (int i = 0; i < board3D.length; i++) {
			board3D[i] = new TicTacToeGame(size);
		};
	}
	
}
