package gameboard;



// Internal imports
import constants.GameConstants;

public class GameBoard {
    // The game board
    private String[][] board;

    private String encodedBoard;
    
    public GameBoard(String encodedBoard) {
        this.encodedBoard = encodedBoard;
        board = new String[GameConstants.BOARD_SIZE_X][GameConstants.BOARD_SIZE_Y];
        decodeBoard(encodedBoard);
    }

    public String getEncodedBoard() {
        return this.encodedBoard;
    }


    public String[][] getBoard() {
        return this.board;
    }

    /**
     * Decodes the encoded board that was sent in by the client.
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

    public boolean[] makeMove(int x, int y) {
        boolean[] update = new boolean[3];
        boolean isHit = false;
        boolean isSunk = false;

        if (!board[x][y].equals(GameConstants.FIELD_TYPE_WATER) && !board[x][y].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
            isHit = true;
        }

        if (hasSunk(x, y)) {
            isSunk = true;
        }

        update[0] = isHit;
        update[1] = isSunk;
        update[2] = allShipsDestroyed();
        return update;
    }

    private boolean allShipsDestroyed() {
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

    private boolean hasSunk(int x, int y) {
        boolean hasSunk = true;
        String fieldName = board[x][y];
        String[] splitFieldName = board[x][y].split("_");
        String fieldNameBeginning = splitFieldName[0];
        int x1;
        int x2;

        String currentFieldValue = board[x][y];
        board[x][y] = currentFieldValue+GameConstants.FIELD_TYPE_HIT_EXTENSION;

        if (fieldNameBeginning.equals(GameConstants.FIELD_TYPE_PATROL)) {
            x1 = x;
            x2 = x;
        } else if (fieldName.startsWith("SUPER_PATROL") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
            
            if (fieldName.endsWith("FRONT")) {
                x1 = x;
                x2 = x+1;
            } else {
                x1 = x-1;
                x2 = x;
            }

        } else if (fieldName.startsWith("DESTROYER") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {

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

        } else if (fieldName.startsWith("BATTLESHIP") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {

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

        } else if (fieldName.startsWith("CARRIER") && !fieldName.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {

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

        } else {
            return false;
        }

        for (int i = x1; i <= x2; i++) {
            if (!board[i][y].endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION)) {
                hasSunk = false;
            }

        }
        
        
        return hasSunk;
    }
}


 