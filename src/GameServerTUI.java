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
		System.out.print(TerminalColors.PURPLE_BOLD + question + TerminalColors.RESET);
		while(true){
			try {
				return Integer.parseInt(in.next());
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
}
