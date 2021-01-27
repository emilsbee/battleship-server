package gameboard.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gameboard.GameBoard;
import constants.GameConstants;

public class GameBoardTest {
    private static final String ENCODED_BOARD = "b;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;PATROL;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;PATROL;WATER;WATER;PATROL;WATER;WATER;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;PATROL;CARRIER_FRONT;CARRIER_FRONT_MID;CARRIER_MID;CARRIER_BACK_MID;CARRIER_BACK;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;PATROL;WATER;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;CARRIER_FRONT;CARRIER_FRONT_MID;CARRIER_MID;CARRIER_BACK_MID;CARRIER_BACK;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER";

    // The board
    private String[][] board;

    // The gameboard to access all the methods, with randomised placing of the ships
    private GameBoard gameboard;
  
    /**
     * Before each test construct new GameBoard and initialise it, and get the board from the gameBoard
     */
    @BeforeEach
    public void setUp() {
        gameboard = new GameBoard(GameBoardTest.ENCODED_BOARD);
        board = gameboard.getBoard();
    }

    /**
     * Test the method generateBoard
     */
    @Test
    public void decodedBoardTest() {
        
        //counters for both type of field
        int waterCounter = 0;
        int shipCounter = 0;

        //iterate over board
        for(int i = 0; i < board.length; i++) { 
            for (int j = 0; j < board[i].length; j++){
                if(board[i][j].equals("WATER")) {
                    waterCounter++;
                }
                else {
                    shipCounter++;
                }
            }
        }
        assertTrue(waterCounter == 87);
        assertTrue(shipCounter == 63);

    }

    /**
     * Test if the amount of ships are correct for each type of ship
     */
    @Test
    public void shipsOnBoardTest() {
        // TODO
    }

    /**
     * Test the method makeMove(), which makes a move on the board
     */
    @Test 
    public void makeMoveTest() {

        //get the fieldname of point (2,3)
        String fieldname = board[2][3];

        //assert this field has not been hit yet, because it is a new board with no boards yet
        assertFalse(fieldname.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION));

        //make a move on hit on (2,3) it will update the field
        gameboard.makeMove(2, 3);

        //get the fieldname of the point that has been fired at
        String fieldnameNew = board[2][3];

        //assert that the new fieldname should be updated into a hit field
        assertTrue(fieldnameNew.endsWith(GameConstants.FIELD_TYPE_HIT_EXTENSION));
    }

    /**
     * Test the method allShipsDestroyed() which check whether or not all the ships on the board have been hit & sunk
     */
    @Test 
    public void allShipsDestroyedTest() {

        //iterate over the whole board
        for(int i = 0; i < GameConstants.BOARD_SIZE_X; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE_Y; j++) {
                if(board[i][j].equals(GameConstants.FIELD_TYPE_PATROL)) { //if it is the patrol ship
                    gameboard.makeMove(i,j); //make a move on that point (i,j)
                    assertTrue(board[i][j].equals(GameConstants.FIELD_TYPE_PATROL_HIT)); //assert it has been hit

                    assertTrue(gameboard.hasSunk(i,j)); //assert it has been sunk
                }
                else if(board[i][j].equals(GameConstants.FIELD_TYPE_SUPER_PATROL_FRONT)) { //if it is the first 'part' of the ship
                    gameboard.makeMove(i,j);
                    assertTrue(board[i][j].equals(GameConstants.FIELD_TYPE_SUPER_PATROL_FRONT_HIT));
                    
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_SUPER_PATROL_BACK)); //assert it is the good part of the ship, and it has not been hit yet
                    gameboard.makeMove(i+1,j); //make a move on the point after the first 'part' of the ship (i+1,j)
                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_SUPER_PATROL_BACK_HIT));

                    assertTrue(gameboard.hasSunk(i,j));
                    assertTrue(gameboard.hasSunk(i+1,j)); //assert is also returns true from a different part of the ship
                }
                else if(board[i][j].equals(GameConstants.FIELD_TYPE_DESTROYER_FRONT)) {
                    gameboard.makeMove(i,j);
                    assertTrue(board[i][j].equals(GameConstants.FIELD_TYPE_DESTROYER_FRONT_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_DESTROYER_MID));
                    gameboard.makeMove(i+1,j);
                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_DESTROYER_MID_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+2][j].equals(GameConstants.FIELD_TYPE_DESTROYER_BACK));
                    gameboard.makeMove(i+2,j);
                    assertTrue(board[i+2][j].equals(GameConstants.FIELD_TYPE_DESTROYER_BACK_HIT));

                    assertTrue(gameboard.hasSunk(i,j));
                    assertTrue(gameboard.hasSunk(i+1,j));
                    assertTrue(gameboard.hasSunk(i+2,j));
                }
                else if(board[i][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_FRONT)) {
                    gameboard.makeMove(i,j);
                    assertTrue(board[i][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_FRONT_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_FRONT_MID));
                    gameboard.makeMove(i+1,j);
                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_FRONT_MID_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+2][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_BACK_MID));
                    gameboard.makeMove(i+2,j);
                    assertTrue(board[i+2][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_BACK_MID_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed
                    
                    assertTrue(board[i+3][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_BACK));
                    gameboard.makeMove(i+3,j);
                    assertTrue(board[i+3][j].equals(GameConstants.FIELD_TYPE_BATTLESHIP_BACK_HIT));

                    assertTrue(gameboard.hasSunk(i,j));
                    assertTrue(gameboard.hasSunk(i+1,j));
                    assertTrue(gameboard.hasSunk(i+2,j));
                    assertTrue(gameboard.hasSunk(i+3,j));
                }
                else if(board[i][j].equals(GameConstants.FIELD_TYPE_CARRIER_FRONT)) {
                    gameboard.makeMove(i,j);
                    assertTrue(board[i][j].equals(GameConstants.FIELD_TYPE_CARRIER_FRONT_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_CARRIER_FRONT_MID));
                    gameboard.makeMove(i+1,j);
                    assertTrue(board[i+1][j].equals(GameConstants.FIELD_TYPE_CARRIER_FRONT_MID_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+2][j].equals(GameConstants.FIELD_TYPE_CARRIER_MID));
                    gameboard.makeMove(i+2,j);
                    assertTrue(board[i+2][j].equals(GameConstants.FIELD_TYPE_CARRIER_MID_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+3][j].equals(GameConstants.FIELD_TYPE_CARRIER_BACK_MID));
                    gameboard.makeMove(i+3,j);
                    assertTrue(board[i+3][j].equals(GameConstants.FIELD_TYPE_CARRIER_BACK_MID_HIT));
                                        
                    assertFalse(gameboard.allShipsDestroyed()); //assert that all ships have not been destroyed

                    assertTrue(board[i+4][j].equals(GameConstants.FIELD_TYPE_CARRIER_BACK));
                    gameboard.makeMove(i+4,j);
                    assertTrue(board[i+4][j].equals(GameConstants.FIELD_TYPE_CARRIER_BACK_HIT));

                    assertTrue(gameboard.hasSunk(i,j));
                    assertTrue(gameboard.hasSunk(i+1,j));
                    assertTrue(gameboard.hasSunk(i+2,j));
                    assertTrue(gameboard.hasSunk(i+3,j));
                    assertTrue(gameboard.hasSunk(i+4,j));
                }
            }
        }
        assertTrue(gameboard.allShipsDestroyed()); //assert that all ships on the board have been sunk
    } 
}
