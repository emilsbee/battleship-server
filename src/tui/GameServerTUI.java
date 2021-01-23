package tui;

// External imports
import java.util.Scanner;

public class GameServerTUI {
    Scanner in;

    public GameServerTUI() {
        this.in = new Scanner(System.in);
    }

	/**
     * Simple method to more easily display messages in terminal
     * @param message The message to be displayed.
     */
    public synchronized void showMessage(String message) {
        System.out.println(message);
    }

	/**
     * Simple method to get a String input from the user given a certain question.
     * @param question The question to be answered by the user.
     * @return The answer to the question.
     */
    public String getString(String question) {
		System.out.print(question);
		return in.nextLine();
	}


    /**
     * Simple method to get an integer input from the user given a certain question.
     * It keeps asking for the integer until a valid one is entered.
     * @param question The question be answered by the user.
     * @return The integer answer to the question.
     */
    public int getInt(String question) {
        int answer;
		System.out.print(question);
		while(true){
			try {
                answer = Integer.parseInt(in.next());
                in.nextLine();
                return answer;
			} catch(NumberFormatException ne) {
				System.out.print("That's not a valid number.\n"+question);
			}
		}
    }
	
	    /**
     * Simple method to get a boolean input from the user given a certain question.
     * It keeps asking for the boolean until a valid one is entered.
     * @param question To be asked to the user
     * @return The boolean answer user provided
     */
    public boolean getBoolean(String question) {
		while(true){
			String input = getString(question);
			if (input.equalsIgnoreCase("yes")) {
				return true;
			} else if (input.equalsIgnoreCase("no")) {
				return false;
			} else {
				showMessage("Please enter yes or no.");
			}
		}
    }
    private static final String[] alphabet = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"};

    public void printBoard(String[][] board) {

        /* New lines */
        printBoardLine("newLine", 1);
        /* New lines */


        /* New lines */
        printBoardLine("newLine", 2);
        /* New lines */

        for (int i = 0; i < 10; i++) {
            
            /* ALPHABET AT THE TOP */
            if (i == 0) {
                System.out.print("      "); // Left margin
                for (int j = 0; j < 15; j++) {
                    printBoardLine("space", 2);
                    System.out.print(alphabet[j].toUpperCase());
                    printBoardLine("space", 3);
                }
                printBoardLine("newLine", 1); // New line
                for (int j = 0; j < 15; j++) {
                    printBoardLine("space", 5);
                }
            }
            /* ALPHABET AT THE TOP */
            

            /* New line */
            printBoardLine("newLine", 1);
            /* New line */

            System.out.print("      "); 
            for (int j = 0; j < 15; j++) { 
                printBoardLine("blue", 5);
                if (j != 14) {   
                    printBoardLine("blue", 1);
                }
            }

            /* New line */
            printBoardLine("newLine", 1);
            /* New line */

            /* Line above the letters */
            for (int j = 0; j < 15; j++) {
                if (j == 0) {
                    System.out.print("  ");
                    printBoardLine("space", 4);
                }
                if (board[j][i].equals("WATER")) {
                    printBoardLine("blue", 5);
                    if (j != 14) {   
                        printBoardLine("blue", 1);
                    }

                } else {
                    printBoardLine("white", 5);
                    if (board[j][i].endsWith("BACK") || board[j][i].equals("PATROL")) {
                        if (j != 14) {   
                            printBoardLine("blue", 1);
                        }
                    } else {
                        printBoardLine("white", 1);
                    }
                }
            }
            /* Line above the letters */

            

            /* New line */
            printBoardLine("newLine", 1);
            /* New line */

            /* Line of letters */
            for (int j = 0; j < 15; j++) {

                /* Number on the left */
                if (j == 0) {
                    if (i+1 == 10) {
                        printBoardLine("space", 2);
                        System.out.print(String.valueOf(i+1).toUpperCase());
                        printBoardLine("space", 2);
                    } else {
                        String toPrint = String.valueOf(i+1) + "  "; // This is necessary because every number besides ten takes up one space so it needs to be equaled out
                        printBoardLine("space", 2);
                        System.out.print(toPrint.toUpperCase());
                        printBoardLine("space", 1);
                    }
                }
                /* Number on the left */
                
                /* Actual letter printing W for water and S for ship */
                if (board[j][i].equals("WATER")) {
                    printBoardLine("blue", 5);
                    if (j != 14) {   
                        printBoardLine("blue", 1);
                    }
                } else {
                    printBoardLine("white", 2);
                    printBoardLine("ship", 1);
                    printBoardLine("white", 2);
                    if (board[j][i].endsWith("BACK") || board[j][i].equals("PATROL")) {
                        if (j != 14) {   
                            printBoardLine("blue", 1);
                        }
                    } else {
                        printBoardLine("white", 1);
                    }
                }
                /* Actual letter printing W for water and S for ship */
            }
            /* Line of letters */

            /* New line */
            printBoardLine("newLine", 1);
            /* New line */
            
            /* Line below the letters */
            for (int j = 0; j < 15; j++) {
                if (j == 0) {
                    System.out.print("  ");
                    printBoardLine("space", 4);
                }
                if (board[j][i].equals("WATER")) {
                    printBoardLine("blue", 5);
                    if (j != 14) {   
                        printBoardLine("blue", 1);
                    }

                } else {
                    printBoardLine("white", 5);
                    if (board[j][i].endsWith("BACK") || board[j][i].equals("PATROL")) {
                        if (j != 14) {   
                            printBoardLine("blue", 1);
                        }
                    } else {
                        printBoardLine("white", 1);
                    }
                }
            }
            /* Line below the letters */
    
        }
        /* New line */
        printBoardLine("newLine", 1);
        /* New line */
    }

    /**
     * Prints specific lines for the board a specific amount of times.
     * @param code The code given as a parameter to see what kind of line it needs to be.
     * @param amount the amount of times it needs to be printed.
     */
    public void printBoardLine(String code, int amount){
        for(int i = 0; i < amount; i++){
            switch (code) {
                case "blue":
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    break;
                case "white":
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    break;
                case "ship":
                    System.out.print(TerminalColors.BLACK_FONT_WHITE_BACKGROUND + "S" + TerminalColors.RESET);
                    break;
                case "space":
                    System.out.print(" ");
                    break;
                case "newLine":
                    System.out.println(" ");
                    break;
                case "cyan":
                    System.out.print(TerminalColors.CYAN_BACKGROUND + " "+ TerminalColors.RESET);
                    break;
                //case "red":
                    //System.out.print(TerminalColors.RED_BACKGROUND + " "+ TerminalColors.RESET);
                   // break;
            }
       }
    }
}
