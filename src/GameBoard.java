import java.io.Serializable;
import java.util.Random;

public class GameBoard implements Serializable {
    /**
	 *
	 */
	private static final long serialVersionUID = 7688335136468996702L;
	// Ship definitions
    // First item in array is the size and second is amount of ships 
    private static final int[] CARRIER = {5, 2};
    private static final int[] BATTLESHIP = {4, 3};
    private static final int[] DESTROYER = {3, 5};
    private static final int[] SUPER_PATROL = {2, 8};
    private static final int[] PATROL_BOAT = {1, 10};

    private static String[] alphabet = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"};
    Random random;
    String[][] board;
    
    public GameBoard() {
        random = new Random();
        generateBoard();
    }

    public String[][] getBoard() {
        return this.board;
    }

    public void generateBoard() {
        String[][] newBoard = new String[15][10];
        newBoard = initialiseEmptyBoard(newBoard);
        System.out.println("RUNS1");
        placeShip(GameBoard.CARRIER, newBoard);
        System.out.println("RUNS2");
        placeShip(GameBoard.BATTLESHIP, newBoard);
        System.out.println("RUNS3");
        placeShip(GameBoard.DESTROYER, newBoard);
        // System.out.println("RUNS4");
        // placeShip(GameBoard.SUPER_PATROL, board);
        // System.out.println("RUNS5");
        // placeShip(GameBoard.PATROL_BOAT, board);
        this.board = newBoard;

    }

    public void placeShip(int[] ship, String[][] board) {
        for (int m = 0; m < ship[1]; m++) {

            boolean fits = false;

            if (ship[0] == 1) { // If the ship is only one field big
                int x = random.nextInt(alphabet.length);
                int y = random.nextInt(10);

                while (!fits) {
                    fits = !hasShipAround(board, x, y, y, x);
                }    

                board[x][y] = "SHIP";
            } else { // If the ship is more than one field big
                int xStart = 0;
                int yStart = 0;
                int direction = 0; // 0: Rightwards, 1: upwards, 2: leftwards, 3: downwards

                while (!fits) {
                    xStart = random.nextInt(alphabet.length);
                    yStart = random.nextInt(10);
                    
                    direction = random.nextInt(3); 
                    fits = doesFit(direction, board, ship[0], xStart, yStart);
                }
                
                if (direction == 0) { // If placement is rightwards
                    
                    for (int i = xStart; i <= xStart + (ship[0]-1); i++) {
                        board[i][yStart] = "SHIP";
                    }

                } else if (direction == 1) { // If placement is upwards
                    
                    for (int j = yStart; j <= yStart + (ship[0]-1); j++) {
                        board[xStart][j] = "SHIP";
                    }

                } else if (direction == 2) { // If placement is leftwards

                    for (int k = xStart; k >= xStart - (ship[0]-1); k--) {
                        board[k][yStart] = "SHIP";
                    }

                } else { // If placement is downwards

                    for (int p = yStart; p >= yStart - (ship[0]-1); p--) {
                        board[xStart][p] = "SHIP";
                    }

                }
            }
        }
    }  

    
    private boolean doesFit(int checkDirection, String[][] board, int shipSize, int x, int y) {
        if (checkDirection == 0 && x + (shipSize-1) < 15) { // Checks that ship fits on the board rightwards for x and y given
            return !hasShipAround(board, x, y, y, x+(shipSize-1));
        } else if (checkDirection == 1 && y + (shipSize-1) < 10) { // Checks that ship fits on the board upwards for x and y given
            int upperMostY = y - (shipSize-1);
            return !hasShipAround(board, x, upperMostY, y, x);
        } else if (checkDirection == 2 && x - (shipSize-1) > -1) { // Checks that ship fits on the board leftwards for x and y given
            int leftMostX = x - (shipSize-1);
            return !hasShipAround(board, leftMostX, y, y, x);
        } else if (checkDirection == 3 && y - (shipSize -1) > -1) { // Checks that ship fits on the board downwards for x and y given
            return !hasShipAround(board, x, y, y+(shipSize-1), x);
        } else { // If ship doesn't fit on the board
            return false;
        }
    }

    private boolean hasShipAround(String[][] board, int x, int y, int shipBottomY, int shipEndX) {
        boolean hasShipAround = false;
        for (int i = (y-1); i <= (shipBottomY+1); i++) { // Iterates over the rows going downwards. Basically from the row above the ship to the row below it

            // If the current row is actually on the board since it could be that the boat is on board but it's on the top edge, so
            // one row above the top edge does not exist, hence does not need to be checked for ships
            if (i >= 0 && i <= 9) { // Row on board

                for (int j = (x-1); j <= (shipEndX+1); j++) {
                    if (j >= 0 && j <= 14 && board[j][i].equals("SHIP")) { // Column on board and the specific field has ship on it already return false
                        hasShipAround = true;
                    }
                }
            } 
        }
        return hasShipAround;
    }

    public String[][] initialiseEmptyBoard(String[][] board) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = "WATER";
            }
        }
        return board;
    }

    public void printBoard(String[][] board) {
        for (int i = 0; i < 10; i++) {
            
            /* ALPHABET AT THE TOP */
            if (i == 0) {
                System.out.print("      "); // Left margin
                for (int j = 0; j < 15; j++) {
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(alphabet[j]);
                    System.out.print(" ");
                    System.out.print(" ");
                }
                System.out.println(" "); // New line
                for (int j = 0; j < 15; j++) {
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                }
            }
            /* ALPHABET AT THE TOP */
            
            /* New line */
            System.out.println(" "); 
            /* New line */

            /* Line above the letters */
            for (int j = 0; j < 15; j++) {
                if (j == 0) {
                    System.out.print("  ");
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                }
                if (board[j][i].equals("WATER")) {
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND +" "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);

                } else {
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                }
            }
            /* Line above the letters */

            /* New line */
            System.out.println(" "); 
            /* New line */

            /* Line of letters */
            for (int j = 0; j < 15; j++) {

                /* Number on the left */
                if (j == 0) {
                    if (i+1 == 10) {
                        System.out.print(" ");
                        System.out.print(" ");
                        System.out.print(String.valueOf(i+1));
                        System.out.print(" ");
                        System.out.print(" ");
                    } else {
                        String toPrint = String.valueOf(i+1) + "  "; // This is necessary because every number besides ten takes up one space so it needs to be equaled out
                        System.out.print(" ");
                        System.out.print(" ");
                        System.out.print(toPrint);
                        System.out.print(" ");
                    }
                }
                /* Number on the left */
                
                /* Actual letter printing W for water and S for ship */
                if (board[j][i].equals("WATER")) {
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + "W" + TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                } else {
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + "S" + TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                }
                /* Actual letter printing W for water and S for ship */
            }
            /* Line of letters */

            /* New line */
            System.out.println(" "); 
            /* New line */
            
            /* Line below the letters */
            for (int j = 0; j < 15; j++) {
                if (j == 0) {
                    System.out.print("  ");
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(" ");
                }
                if (board[j][i].equals("WATER")) {
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND +" "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);

                } else {
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.GREEN_BACKGROUND + " "+ TerminalColors.RESET);
                }
            }
            /* Line below the letters */
    
        }
        /* New line */
        System.out.println(" "); 
        /* New line */
    }
}
