package gameboard.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import gameboard.GameBoard;
import constants.GameConstants;

public class GameBoardTest {
  
    // The board
    private String[][] board;

    // The gameboard to access all the methods, with randomised placing of the ships
    private GameBoard gameboard;
  
    /**
     * Before each test construct new GameBoard and initialise it, and get the board from the gameBoard
     */
    @BeforeEach
    public void setUp() {
        gameboard = new GameBoard(false);
        board = gameboard.getBoard();
    }

    /**
     * Test the method initialiseEmptyBoard, which makes an double String array and fills it with 'WATER' Strings
     */
    @Test
    public void initialiseEmptyBoardTest() {
        String[][] newBoard = new String[15][10];

        newBoard = gameboard.initialiseEmptyBoard(newBoard);

        int counter = 0;

        for(int i = 0; i < GameConstants.BOARD_SIZE_X; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE_Y; j++){
                assertTrue(newBoard[i][j].equals("WATER"));
                counter++;
            }
        }
        assertTrue(counter == 150); 
    }

    /**
     * Test the method generateBoard
     */
    @Test
    public void generateBoardTest() {
        
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
     * Test the method findPlaceOnBoard
     */
    @Test
    public void findPlaceOnBoardTest() {

        //make a new board to test the method
        String[][] newBoard = new String[15][10];
        newBoard = gameboard.initialiseEmptyBoard(newBoard);

        //Place all the ships on the board
        gameboard.findPlaceOnBoard(new Carrier(), newBoard);
        gameboard.findPlaceOnBoard(new Battleship(), newBoard);
        gameboard.findPlaceOnBoard(new Destroyer(), newBoard);
        gameboard.findPlaceOnBoard(new SuperPatrol(), newBoard);
        gameboard.findPlaceOnBoard(new Patrol(), newBoard);

        //assert that all ships have not been destroyed
        assertFalse(gameboard.allShipsDestroyed());

        //counters for the different fields that can be on a new board
        int waterCounter = 0;
        int shipCounter = 0;

        //iterate over the whole board
        for(int i = 0; i < GameConstants.BOARD_SIZE_X; i++) {
            for (int j = 0; j < GameConstants.BOARD_SIZE_Y; j++){
                if(newBoard[i][j].equals("WATER")) {
                    waterCounter++;
                }
                else {
                    shipCounter++;
                }
            }
        }

        //assert that the amount of water and ship tiles are correct
        assertTrue(waterCounter + shipCounter == 150);
        assertTrue(waterCounter == 87);
        assertTrue(shipCounter == 63);
    }

    /**
     * Test the method doesFit
     */
    @Test
    public void doesFitTest() {

        //make a new board to test the method
        String[][] newBoard = new String[15][10];
        newBoard = gameboard.initialiseEmptyBoard(newBoard);

        //asserting 
        assertTrue(gameboard.doesFit(1, 1, 1, newBoard)); //check if a patrol fits on a valid coordinate
        assertTrue(gameboard.doesFit(5, 5, 5, newBoard)); //check if a carrier fits on a valid coordinate
        assertFalse(gameboard.doesFit(17, 5, 2, newBoard)); //check if a superPatrol fits on a invalid coordinate
        assertFalse(gameboard.doesFit(12, 8, 4, newBoard)); //check if a battleship fits on a invalid coordinate, out of the board x>15
        
        Patrol ship = new Patrol(); //construct Patrol ship
        ship.placeOnBoard(newBoard, 1, 1); //place Patrol ship on the board on (1,1)
        assertFalse(gameboard.doesFit(1, 1, 3, newBoard)); //check if a destroyer fits on a invalid coordinate, where there is a boat already
    }
    /**
     * Test if the amount of ships are correct for each type of ship
     */
    @Test
    public void shipsOnBoardTest() {
        List<Ship> ships = gameboard.getShips();

        int[] shipsCounter = new int[5]; // counter array for: i-0 = Patrol, 1 = SuperPatrol, 2 = Destroyer, 3 = Battleship, 4 = Carrier

        int size; 

        for(Ship s : ships) { //iterate over the s ship in ships, and decide which ship it is to higher the counter
            size = s.getSize();
            switch(size) {
                case 1:
                shipsCounter[0]++;
                break;
                case 2:
                shipsCounter[1]++;
                break;
                case 3:
                shipsCounter[2]++;
                break;
                case 4:
                shipsCounter[3]++;
                break;
                case 5:
                shipsCounter[4]++;
                break;
            }
        }

        // assert that all counters are the correct value for the amount of ships
        assertTrue(shipsCounter[0] == 10);
        assertTrue(shipsCounter[1] == 8);
        assertTrue(shipsCounter[2] == 5);
        assertTrue(shipsCounter[3] == 3);
        assertTrue(shipsCounter[4] == 2);
        assertTrue(shipsCounter[0]+shipsCounter[1]+shipsCounter[2]+shipsCounter[3]+shipsCounter[4] == 28); //assert that there are 28 ships
    }

    /**
     * Test the method addScore(), which adds a points if a ship has been hit and adds an extra point if the ship has been sunk
     */
    @Test
    public void addScoreTest() {
        assertTrue(gameboard.getScore() == 0); //Check if initial value of points = 0

        gameboard.addScore(false, false); //Did not hit a ship
        assertTrue(gameboard.getScore() == 0);

        gameboard.addScore(true, false); //Hit a ship, but the ship did not sink
        assertTrue(gameboard.getScore() == 1); 

        gameboard.addScore(false, false); //Did not hit a ship, so points should stay 1
        assertTrue(gameboard.getScore() == 1); 

        gameboard.addScore(true, true); //Hit a ship, and ship has been sunk
        assertTrue(gameboard.getScore() == 3);

        gameboard.addScore(false, false); //Did not hit a ship, so points should stay 3 after a ship has been sunk
        assertTrue(gameboard.getScore() == 3);

        gameboard.addScore(false, true); //Did not hit a ship, but did sunk the ship. This should not add any points to the score as it should not be possible
        assertTrue(gameboard.getScore() == 3);
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
