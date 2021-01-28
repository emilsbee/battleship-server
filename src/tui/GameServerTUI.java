package tui;

// External imports
import java.util.Scanner;

/**
 * This class is a TUI for the game server. Is prompts questions to the user and displays messages.
 * @inv scanner != null
 */
public class GameServerTUI {
    private Scanner in;

    /**
     * Initialises the scanner
     * @post ensures that in != null
     */
    public GameServerTUI() {
        this.in = new Scanner(System.in);
    }

	/**
     * Simple method to more easily display messages in terminal
     * @param message The message to be displayed.
     * @pre message != null
     * @post ensures that a message on a new line is printed
     */
    public void showMessage(String message) {
        System.out.println(message);
    }

	/**
     * Simple method to get a String input from the user given a certain question.
     * @param question The question to be answered by the user.
     * @pre question != null, in != null
     * @post enusres that a string response is returned 
     * @return The answer to the question.
     */
    public String getString(String question) {
        System.out.print(question);
        in = new Scanner(System.in);
		return in.nextLine();
	}


    /**
     * Simple method to get an integer input from the user given a certain question.
     * It keeps asking for the integer until a valid one is entered.
     * @param question The question be answered by the user.
     * @return The integer answer to the question.
     * @pre question != null, in != null
     * @post ensures that an actual integer is returned
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
     * @pre question != null, in != null
     * @post ensures that a boolean response is returned
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
