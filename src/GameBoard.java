import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Point;


public class GameBoard implements Serializable {
	private static final long serialVersionUID = 6798175181929406915L;

    private static String[] alphabet = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"};

    // Array of ships
    private List<Ship> ships;


    // Game board grid
    private String[][] board;

    // Ship definitions
    // First item in array is the size and second is amount of ships 
    private static final int[] CARRIER = {5, 2};
    private static final int[] BATTLESHIP = {4, 3};
    private static final int[] DESTROYER = {3, 5};
    private static final int[] SUPER_PATROL = {2, 8}; 
    private static final int[] PATROL_BOAT = {1, 10};

    Random random;
    

	public GameBoard(boolean manualPlacement) {
        random = new Random();
        ships = new ArrayList<>();
        if (manualPlacement) {
            manualBoard();  
        } else {
            generateBoard();
        }
    }

    public void generateBoard() {
        String[][] newBoard = new String[15][10];
        newBoard = initialiseEmptyBoard(newBoard);
        
        placeShip(GameBoard.CARRIER, newBoard);
        placeShip(GameBoard.BATTLESHIP, newBoard);
        placeShip(GameBoard.DESTROYER, newBoard);
        placeShip(GameBoard.SUPER_PATROL, newBoard);
        placeShip(GameBoard.PATROL_BOAT, newBoard);

        setBoard(newBoard);
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }
    

    public String[][] getBoard() {
        return this.board;
    }

    public void placeShip(int[] ship, String[][] board) {
        for (int shipCount = 0; shipCount < ship[1]; shipCount++) { // Iterates over the number of ships
            
            /* Find free fields to place the ship */
            boolean isPlaced = false;
            int x = 0;
            int y = 0;
            while (!isPlaced) {
                
                x = random.nextInt(15);
                y = random.nextInt(10);

                isPlaced = doesFit(x, y, ship[0], board);
            }
            
            Point[] position = new Point[ship[0]];

            /* Place the ship on board */
            int count = 0;
            for (int xPos = x; xPos < x + ship[0]; xPos++) {
                board[xPos][y] = "SHIP";
                position[count] = new Point(xPos, y); 
                count++;
            } 
            /* Create ship instance and it to the ships list */
            ships.add(new Ship(position));
        }

    }  

    /**
     * 
     * @param x Random X coordinate 
     * @param y Random Y coordinate
     * @param shipSize The size of the ship to be checked
     * @param board The game board
     * @return Whether the ship fits on the board
     */
    public boolean doesFit(int x, int y, int shipSize, String[][] board) {
        if (x + (shipSize-1) < 15) { // Checks whether ship fits on board

            for (int i = x; i < x+shipSize; i++) { // Iterates over the fields that the ship would take up
                if (board[i][y].equals("SHIP")) {
                    return false;
                } 
            }
            return true;
        } 

        return false;
    }

    public void manualBoard() {
        // Some comment
    } 

    public String[][] initialiseEmptyBoard(String[][] board) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = "WATER";
            }
        }
        return board;
    }

    public boolean isShipEnd(int x, int y) {
        for (Ship ship : ships) {
            if (ship.getPositon()[ship.getLength()-1].getX() == x && ship.getPositon()[ship.getLength()-1].getY() == y) {
                return true;
            } 
        }
        return false;
    }

    public void printBoard(String[][] board) {
        for (int i = 0; i < 10; i++) {
            
            /* ALPHABET AT THE TOP */
            if (i == 0) {
                System.out.print("      "); // Left margin
                for (int j = 0; j < 15; j++) {
                    System.out.print(" ");
                    System.out.print(" ");
                    System.out.print(alphabet[j].toUpperCase());
                    System.out.print(" ");
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

            System.out.print("      "); 
            for (int j = 0; j < 15; j++) { 
                System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                System.out.print(TerminalColors.BLUE_BACKGROUND +" "+ TerminalColors.RESET);
                System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                if (j != 14) {   
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                }
            }

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
                    if (j != 14) {   
                        System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    }

                } else {
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    if (isShipEnd(j, i)) {
                        if (j != 14) {   
                            System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                        }
                    } else {
                        System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    }
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
                        System.out.print(String.valueOf(i+1).toUpperCase());
                        System.out.print(" ");
                        System.out.print(" ");
                    } else {
                        String toPrint = String.valueOf(i+1) + "  "; // This is necessary because every number besides ten takes up one space so it needs to be equaled out
                        System.out.print(" ");
                        System.out.print(" ");
                        System.out.print(toPrint.toUpperCase());
                        System.out.print(" ");
                    }
                }
                /* Number on the left */
                
                /* Actual letter printing W for water and S for ship */
                if (board[j][i].equals("WATER")) {
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " " + TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    if (j != 14) {   
                        System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    }
                } else {
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.BLACK_FONT_WHITE_BACKGROUND + "S" + TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    if (isShipEnd(j, i)) {
                        if (j != 14) {   
                            System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                        }
                    } else {
                        System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    }
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
                    if (j != 14) {   
                        System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    }

                } else {
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    if (isShipEnd(j, i)) {
                        if (j != 14) {   
                            System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                        }
                    } else {
                        System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    }
                }
            }
            /* Line below the letters */
    
        }
        /* New line */
        System.out.println(" "); 
        /* New line */
    }

   
}

