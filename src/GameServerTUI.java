import java.util.Scanner;

public class GameServerTUI {
    Scanner in;

    public GameServerTUI() {
        this.in = new Scanner(System.in);
    }

    public void showMessage(String message) {
        System.out.println(message);
    }

    public String getString(String question) {
		System.out.print(question);
		return in.nextLine();
	}

    public int getInt(String question) {
		System.out.print(question);
		while(true){
			try {
				return Integer.parseInt(in.next());
			} catch(NumberFormatException ne) {
				System.out.print("That's not a valid number.\n"+question);
			}
		}
    }
    
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
