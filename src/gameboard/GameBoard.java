package gameboard;

// Internal imports
import constants.GameConstants;
import tui.TerminalColors;

/**
 * This class represents a game board for a client. It makes moves on the board and keeps track of what has been already hit
 * and whether a certain ship has sunk. It is always created from an encoded board sent in by the client which is then decoded here.
 * @inv board != null, encodedBoard != null
 */
public class GameBoard {
    // The game board
    private String[][] board;

    // The encdoded game board sent in by client
    private String encodedBoard;

    /**
     * Initialises the encoded, and decoded game boards.
     * @param encodedBoard The encoded game board sent in by the client.
     * @pre encodedBoard != null
     * @post ensures that encodedBoard, board are initialised and that the encdodedBoard is decoded
     */
    public GameBoard(String encodedBoard) {
        this.encodedBoard = encodedBoard;
        board = new String[GameConstants.BOARD_SIZE_X][GameConstants.BOARD_SIZE_Y];
        decodeBoard(encodedBoard);
    }

    /**
     * Decodes and sets the encoded board that was sent in by the client.
     * @param encodedBoard The board to decode.
     * @pre encodedBoard != null, board != null
     * @post ensures that the encodedBoard is decoded and set as the board
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
     * @pre x >= 0 && x < 15, y >= 0 && y < 10, board != null
     * @post ensures that the move is made and results of whether ship is hit and sunk and whether all ships are destroyed
     */
    public boolean[] makeMove(int x, int y) {
        boolean[] update = new boolean[3];
        boolean isHit = false;
        boolean isSunk = false;

        // Checks for whether ship was hit
        if (!board[x][y].equals(GameConstants.FIELD_TYPE_WATER) && !board[x][y].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
            isHit = true;
        }

        // Makes the move
        if (!board[x][y].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {  
            board[x][y] = board[x][y] + GameConstants.FIELD_TYPE_HIT_EXTENSION;
        } 

        // Only checks if a ship sunk in the case that a ship was hit.
         // That's because there could be a case where player fires at a field
         // that already has a sunk ship and the player shouldn't receive a point for that.
        if (isHit) {
            isSunk = hasSunk(x, y);
        }

        update[0] = isHit;
        update[1] = isSunk;
        update[2] = allShipsDestroyed();
        return update;
    }

    /**
     * Checks whether all ships have been destroyed on this board.
     * @return Whether all ships have been destroyed or not.
     * @pre board != null
     * @post ensures that a check is made for this board about whether all the ships are destroyed and returns the result
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
     * This method checks whether whether certain coordinates are part of a sunk ship.
     * This is only for single player because in multiplayer the server would do this instead.
     * @param x The X coordinate of the move.
     * @param y The Y coordinate of the move.
     * @return Whether a ship was sunk as a result of the move.
     * @pre x >= 0 && x < 15, y >= 0 && y < 10, board != null
     * @post ensures that a check is made for whether a ship in given coordinates has sunk
     */
    public boolean hasSunk(int x, int y) {
        boolean hasSunk = true; // Indicator for whether ship was sunk

        String fieldName = board[x][y]; // The name of the field that the move was made upon

        // The first word in the field being moved upon. This indicates what kind of field and what kind of ship it is.
        String fieldNameBeginning = board[x][y].split("_")[0];

        // All ships are placed horizontally and the ships start is on the left side. So they are placed from left to right.
        // So to check whether a ships has sunk, the start coordinate, x1, has to be found and the end coordinate, x2,
        // has to be found. So these two variables represent that.
        int x1; 
        int x2;

        
        if (fieldNameBeginning.equals(GameConstants.FIELD_TYPE_PATROL)) { // If the field is a patrol ship

            x1 = x;
            x2 = x;

        } else if (fieldName.startsWith("SUPER_PATROL")) { // If the field is super patrol ship
            
            if (fieldName.endsWith("FRONT_HIT")) {
                x1 = x;
                x2 = x+1;
            } else {
                x1 = x-1;
                x2 = x;
            }

        } else if (fieldName.startsWith("DESTROYER")) { // If the field is destroyer ship

            if (fieldName.endsWith("FRONT_HIT")) {
                x1 = x;
                x2 = x+2;
            } else if (fieldName.endsWith("MID_HIT")) {
                x1 = x-1;
                x2 = x+1;
            } else {
                x1 = x-2;
                x2 = x;
            }

        } else if (fieldName.startsWith("BATTLESHIP")) { // If the field is battleship

            if (fieldName.endsWith("FRONT_HIT")) {
                x1 = x;
                x2 = x+3;
            } else if (fieldName.endsWith("FRONT_MID_HIT")) {
                x1 = x-1;
                x2 = x+2;
            } else if (fieldName.endsWith("BACK_MID_HIT")) {
                x1 = x-2;
                x2 = x+1;
            } else {
                x1 = x-3;
                x2 = x;
            }

        } else if (fieldName.startsWith("CARRIER")) { // If the field is carrier

            if (fieldName.endsWith("FRONT_HIT")) {
                x1 = x;
                x2 = x+4;
            } else if (fieldName.endsWith("FRONT_MID_HIT")) {
                x1 = x-1;
                x2 = x+3;
            } else if (fieldName.endsWith("BACK_MID_HIT")) {
                x1 = x-3;
                x2 = x+1;
            } else if (fieldName.endsWith("MID_HIT")) {
                x1 = x-2;
                x2 = x+2;
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
        
        return hasSunk;
    }

    /**
     * Getter for getting the encoded board.
     * @return The encoded board.
     * @pre encodedBoard != null
     * @post ensures that the encoded board is returned
     */
    public String getEncodedBoard() {
        return this.encodedBoard;
    }


    /**
     * Getter for getting the decoded board.
     * @return The decoded board.
     * @pre board != null
     * @post ensures that the decoded board is returned
     */
    public String[][] getBoard() {
        return this.board;
    }










        /**
     * Prints specific lines for the board a specific amount of times.
     * @param code The code given as a parameter to see what kind of line it needs to be.
     * @param amount the amount of times it needs to be printed.
     */
    public void printBoardLine(String code, int amount){
        for(int i = 0; i < amount; i++){
            switch (code) {
                case "black": 
                    System.out.print(TerminalColors.BLACK_BACKGROUND + " "+ TerminalColors.RESET);
                    break;
                case "blue":
                    System.out.print(TerminalColors.BLUE_BACKGROUND + " "+ TerminalColors.RESET);
                    break;
                case "white":
                    System.out.print(TerminalColors.WHITE_BACKGROUND + " "+ TerminalColors.RESET);
                    break;
                case "ship":
                    System.out.print(TerminalColors.BLACK_FONT_WHITE_BACKGROUND + "S" + TerminalColors.RESET);
                    break;
                case "ship-hit":
                    System.out.print(TerminalColors.BLACK_FONT_RED_BACKGROUND + "S" + TerminalColors.RESET);
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
                case "red":
                    System.out.print(TerminalColors.RED_BACKGROUND + " "+ TerminalColors.RESET);
                   break;
            }
       }
    }
}
