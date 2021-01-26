package gameboard;

// Internal imports
import constants.GameConstants;

/**
 * This class represents a game board for a client. It makes moves on the board and keeps track of what has been already hit
 * and whether a certain ship has sunk. It is always created from an encoded board sent in by the client which is then decoded here.
 */
public class GameBoard {
    // The game board
    private String[][] board;

    // The encdoded game board sent in by client
    private String encodedBoard;
    
    /**
     * Initialises the encoded, and decoded game boards.
     * @param encodedBoard The encoded game board sent in by the client.
     */
    public GameBoard(String encodedBoard) {
        this.encodedBoard = encodedBoard;
        board = new String[GameConstants.BOARD_SIZE_X][GameConstants.BOARD_SIZE_Y];
        decodeBoard(encodedBoard);
    }

    /**
     * Decodes and sets the encoded board that was sent in by the client.
     * @param encodedBoard The board to decode.
     */
    public void decodeBoard(String encodedBoard) {
        String[] splitEncodedBoard = encodedBoard.split(";");

        for (int i = 0; i < GameConstants.BOARD_SIZE_Y; i++) {

            for (int j = 0; j < GameConstants.BOARD_SIZE_X; j++) {
                
                board[j][i] = splitEncodedBoard[((i*GameConstants.BOARD_SIZE_X)+j)+1];

            }

        }
    }

    /**
     * Called by the game instance to make a move on behalf of the opponent given x and y coordinates of the move.
     * @param x The X coordinate of the move.
     * @param y The Y coordinate of the move.
     * @return Information about whether a ship was hit, whether that hit resulted in sinking the ship, and whether all ships have been destroyed.
     */
    public boolean[] makeMove(int x, int y) {
        boolean[] update = new boolean[3];
        boolean isHit = false;
        boolean isSunk = false;

        // Checks for whether ship was hit
        if (!board[x][y].equals(GameConstants.FIELD_TYPE_WATER) && !board[x][y].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
            isHit = true;
        }

        // Checks for whether ship was sunk
        if (hasSunk(x, y)) {
            isSunk = true;
        }

        update[0] = isHit;
        update[1] = isSunk;
        update[2] = allShipsDestroyed();
        return update;
    }

    /**
     * Checks whether all ships have been destroyed on this board.
     * @return Whether all ships have been destroyed or not.
     */
    public boolean allShipsDestroyed() {
        boolean allShipsDestroyed = true;

        for (int i = 0; i < GameConstants.BOARD_SIZE_Y; i++) {

            for (int j = 0; j < GameConstants.BOARD_SIZE_X; j++) {

                if (!board[j][i].startsWith(GameConstants.FIELD_TYPE_WATER) && !board[j][i].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
                    allShipsDestroyed = false;
                }
                
            }

        }

        return allShipsDestroyed;
    }

    /**
     * This method actually makes the move on this board and then checks whether it resulted in a ship that sunk.
     * So it also accepts moves on fields that were previously fired upon in which case it just returns false for whether a ship sunk.
     * @param x The X coordinate of the move.
     * @param y The Y coordinate of the move.
     * @return Whether a ship was sunk as a result of the move.
     */
    private boolean hasSunk(int x, int y) {
        boolean hasSunk = true; // Indicator for whether ship was sunk

        String fieldName = board[x][y]; // The name of the field that the move was made upon

        // Since all the field names that are more than one word long are seprated by _ (underscore)
        // this is the array of all the words from the field being moved upon.
        String[] splitFieldName = board[x][y].split("_"); 

        // The first word in the field being moved upon. This indicates what kind of field and what kind of ship it is.
        String fieldNameBeginning = splitFieldName[0];

        // All ships are placed horizontally and the ships start is on the left side. So they are placed from left to right.
        // So to check whether a ships has sunk, the start coordinate, x1, has to be found and the end coordinate, x2,
        // has to be found. So these two variables represent that.
        int x1; 
        int x2;

        if (!fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) { // If the field wasn't previously fired on

            board[x][y] = fieldName+GameConstants.FIELD_TYPE_HIT_EXTENSION; // Adding the hit extension to whatever field this previously was
        
            if (fieldNameBeginning.equals(GameConstants.FIELD_TYPE_PATROL)) { // If the field is a patrol ship

                x1 = x;
                x2 = x;

            } else if (fieldName.startsWith("SUPER_PATROL") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) { // If the field is super patrol ship
                
                if (fieldName.endsWith("FRONT")) {
                    x1 = x;
                    x2 = x+1;
                } else {
                    x1 = x-1;
                    x2 = x;
                }
    
            } else if (fieldName.startsWith("DESTROYER") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) { // If the field is destroyer ship
    
                if (fieldName.endsWith("FRONT")) {
                    x1 = x;
                    x2 = x+2;
                } else if (fieldName.endsWith("MID")) {
                    x1 = x-1;
                    x2 = x+1;
                } else {
                    x1 = x-2;
                    x2 = x;
                }
    
            } else if (fieldName.startsWith("BATTLESHIP") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) { // If the field is battleship
    
                if (fieldName.endsWith("FRONT")) {
                    x1 = x;
                    x2 = x+3;
                } else if (fieldName.endsWith("FRONT_MID")) {
                    x1 = x-1;
                    x2 = x+2;
                } else if (fieldName.endsWith("BACK_MID")) {
                    x1 = x-2;
                    x2 = x+1;
                } else {
                    x1 = x-3;
                    x2 = x;
                }
    
            } else if (fieldName.startsWith("CARRIER") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) { // If the field is carrier
    
                if (fieldName.endsWith("FRONT")) {
                    x1 = x;
                    x2 = x+4;
                } else if (fieldName.endsWith("FRONT_MID")) {
                    x1 = x-1;
                    x2 = x+3;
                } else if (fieldName.endsWith("MID")) {
                    x1 = x-2;
                    x2 = x+2;
                } else if (fieldName.endsWith("BACK_MID")) {
                    x1 = x-3;
                    x2 = x+1;
                } else {
                    x1 = x-4;
                    x2 = x;
                }
    
            } else { // If the field is water
                return false;
            }
    
            // The loop that iterates from start of the ship to the end to check whether all parts of it are hit.
            for (int i = x1; i <= x2; i++) {
                if (!board[i][y].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
                    hasSunk = false;
                }
    
            }

        } else { // If this field was previously fired upon already

            hasSunk = false;
        
        }
        
        return hasSunk;
    }

    /**
     * Getter for getting the encoded board.
     * @return The encoded board.
     */
    public String getEncodedBoard() {
        return this.encodedBoard;
    }


    /**
     * Getter for getting the decoded board.
     * @return The decoded board.
     */
    public String[][] getBoard() {
        return this.board;
    }
}


 