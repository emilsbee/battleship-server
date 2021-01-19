// External imports
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.Point;


public class GameBoard implements Serializable {
	private static final long serialVersionUID = 6798175181929406915L;

    // List of ships
    private List<Ship> ships;

    // The game board
    private String[][] board;

    // Ship definitions
    // First item in array is the size and second is amount of ships 
    private static final int[] CARRIER = {5, 2};
    private static final int[] BATTLESHIP = {4, 3};
    private static final int[] DESTROYER = {3, 5};
    private static final int[] SUPER_PATROL = {2, 8}; 
    private static final int[] PATROL_BOAT = {1, 10};

    // Re-usable instance of Random
    Random random;
    

    /**
     * Constructor that calls for a board creation based on the argument. Either 
     * calls to randomly generate the board or manually.
     * @param manualPlacement Indicates whether the board will be created manually or randomly.
     */
	public GameBoard(boolean manualPlacement) {
        random = new Random();
        ships = new ArrayList<>();
        if (manualPlacement) {
            manualBoard();  
        } else {
            generateBoard();
        }
    }

    public void manualBoard() {
        // Some comment
    }

    /**
     * Initialises, generates and sets a randomly created board. 
     */
    public void generateBoard() {
        String[][] newBoard = new String[15][10];
        newBoard = initialiseEmptyBoard(newBoard);
        
        findPlaceOnBoard(GameBoard.CARRIER, newBoard);
        findPlaceOnBoard(GameBoard.BATTLESHIP, newBoard);
        findPlaceOnBoard(GameBoard.DESTROYER, newBoard);
        findPlaceOnBoard(GameBoard.SUPER_PATROL, newBoard);
        findPlaceOnBoard(GameBoard.PATROL_BOAT, newBoard);

        setBoard(newBoard);
    }

    /**
     * Setter for a newly created board.
     * @param board the board to be set.
     */
    public void setBoard(String[][] board) {
        this.board = board;
    }
    

    /**
     * Getter for the game board created.
     * @return the board.
     */
    public String[][] getBoard() {
        return this.board;
    }

    /**
     * Given a certain ship type, the method finds valid fields on the board to place the ship and then calls
     * {@link #placeShip(int[], String[][], int, int)} method to place the ship and add it to the ships list. 
     * This method does that for the specific amount of the ship needed. 
     * @param ship
     * @param board
     */
    public void findPlaceOnBoard(int[] ship, String[][] board) {
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
            
            placeShip(ship, board, x, y);
        }
    }

    /**
     * Places a given ship type with given coordinates on the board. The coordinates are just x and y value
     * so for ships that are longer than one field, the ship is always placed horizontally and rightwards from
     * the given coordinates. After ship is placed on the board, a Ship instance is created and added to the ships
     * list.
     * @param ship The ship to be placed.
     * @param board The board to place the ship on.
     * @param x The x coordinate of the ship starting field
     * @param y The y coordinate of the ship starting field
     */
    public void placeShip(int[] ship, String[][] board, int x, int y) {
        Point[] position = new Point[ship[0]];

        /* Place the ship on board */
        int count = 0;
        for (int xPos = x; xPos < x + ship[0]; xPos++) {
            board[xPos][y] = "SHIP";
            position[count] = new Point(xPos, y); 
            count++;
        } 
        /* Create ship instance and add it to the ships list */
        ships.add(new Ship(position));
    }  


    /**
     * Checks whether a given ship size fits on board and whether there are no other ships that the ship would overlap.
     * @param x Random X coordinate 
     * @param y Random Y coordinate
     * @param shipSize The size of the ship to be checked
     * @param board The game board
     * @return Whether the ship fits on the board and doesn't overlap any other ships
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


    /**
     * Initialises a given board by setting all fields of it to the string WATER
     * @param board the board to be initalised with water fields
     * @return the initialised board.
     */
    public String[][] initialiseEmptyBoard(String[][] board) {
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 10; j++) {
                board[i][j] = "WATER";
            }
        }
        return board;
    }


    /**
     * Determines whether certain coordinates on the board are the end point of a ship.
     * @param x x coordinate
     * @param y y coordinate
     * @return whether the given coordinates are endpoint of a ship
     */
    public boolean isShipEnd(int x, int y) {
        for (Ship ship : ships) {
            if (ship.getPositon()[ship.getLength()-1].getX() == x && ship.getPositon()[ship.getLength()-1].getY() == y) {
                return true;
            } 
        }
        return false;
    }

}


