package game.tests;


// External imports
import org.junit.jupiter.api.*;

import constants.GameConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Internal imports
import game.Game;
import game.GameBoard;
import server.ProtocolMessages;
import server.GameServer;
import tui.GameServerTUI;

/**
 * Tests the game initialisation. Also tests a whole game whereby a player wins by destroying all opponents ships. 
 */
class GameTest {
    private static final int PORT = 8888;
    private static final String FIRST_PLAYER_NAME = "Albert";
    private static final String SECOND_PLAYER_NAME = "Sam";
    private static final String ENCODED_BOARD = "b;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;PATROL;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;PATROL;WATER;WATER;PATROL;WATER;WATER;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;PATROL;CARRIER_FRONT;CARRIER_FRONT_MID;CARRIER_MID;CARRIER_BACK_MID;CARRIER_BACK;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;PATROL;WATER;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;CARRIER_FRONT;CARRIER_FRONT_MID;CARRIER_MID;CARRIER_BACK_MID;CARRIER_BACK;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER";
    private static final int GAME_ID = 1;
 

    @Test
    void testGameInitialisation() {
        Game game = new Game(new GameServerTUI(), GameTest.GAME_ID);
        assertTrue(game.getPlayer1Points() == 0);
        assertTrue(game.getPlayer2Points() == 0);
        assertFalse(game.getGameStarted());
        assertTrue(game.getGameId() == GameTest.GAME_ID);
    }


    @Test
    void testWinByDestroyAllShips() {
        GameServer server = new GameServer(new String[]{String.valueOf(GameTest.PORT)});

        Socket pingSocket1 = null;
        PrintWriter out1 = null;
        BufferedReader in1 = null;
        Socket pingSocket2 = null;
        PrintWriter out2 = null;
        BufferedReader in2 = null;

        try {
            GameBoard gameBoard = new GameBoard(GameTest.ENCODED_BOARD);
            String[][] board = gameBoard.getBoard();

            // Connect client 1 to server
            pingSocket1 = new Socket("localhost", GameTest.PORT);
            out1 = new PrintWriter(pingSocket1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(pingSocket1.getInputStream()));

            // Connect client 2 to server
            pingSocket2 = new Socket("localhost", GameTest.PORT);
            out2 = new PrintWriter(pingSocket2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(pingSocket2.getInputStream()));
            
            /* Client 1 handshake*/
            out1.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameTest.FIRST_PLAYER_NAME); // Send first handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in1.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            /* Client 2 handshake*/
            out2.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameTest.SECOND_PLAYER_NAME); // Send second handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in2.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            /* Both client enemy names */
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameTest.SECOND_PLAYER_NAME, in1.readLine()); // Assert that the client 1 receives second player name
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameTest.FIRST_PLAYER_NAME, in2.readLine()); // Assert that the client 2 receives first player name


            /* Both clients send their encoded game board*/
            out1.println(GameTest.ENCODED_BOARD); // Client 1 sends their encoded board
            out2.println(GameTest.ENCODED_BOARD); // Client 2 sends their encoded board
            
            /* Both clients receive SETUP message*/
            String gameSetupResponse = in1.readLine(); // The response is needed to determine which client should make the move first
            assertTrue(gameSetupResponse.contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random
            assertTrue(in2.readLine().contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random    


            /* Make the moves */
            if (gameSetupResponse.split(";")[1].equals(GameTest.FIRST_PLAYER_NAME)) { // If client 1 goes first
                
                // Iterates over the whole board
                for (int y = 0; y < GameConstants.BOARD_SIZE_Y; y++) {
                    for (int x = 0; x < GameConstants.BOARD_SIZE_X; x++) {
    
                        if (!board[x][y].equals(GameConstants.FIELD_TYPE_WATER)) { // If there is a ship on the current field in the iteration
    
                            out1.println(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+x+ProtocolMessages.DELIMITER+y); // Move on that ship
    
                            assertTrue(in1.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+x+ProtocolMessages.DELIMITER+y+ProtocolMessages.DELIMITER+"true")); // Assert that the ship was hit
                            assertTrue(in2.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+x+ProtocolMessages.DELIMITER+y+ProtocolMessages.DELIMITER+"true")); // Assert that the ship was hit
    
                        }
    
                    }
    
                }

                /* Assert that the game is over after all ships are hit and that the client 1 has won */
                assertTrue(in1.readLine().contains(ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+GameTest.FIRST_PLAYER_NAME+ProtocolMessages.DELIMITER+"true"));
                assertTrue(in2.readLine().contains(ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+GameTest.FIRST_PLAYER_NAME+ProtocolMessages.DELIMITER+"true"));
            
            } else { // If client 2 goes first

                // Iterates over the whole board
                for (int y = 0; y < GameConstants.BOARD_SIZE_Y; y++) {
                    for (int x = 0; x < GameConstants.BOARD_SIZE_X; x++) {
    
                        if (!board[x][y].equals(GameConstants.FIELD_TYPE_WATER)) { // If there is a ship on the current field in the iteration
    
                            out2.println(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+x+ProtocolMessages.DELIMITER+y); // Move on that ship
    
                            assertTrue(in1.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+x+ProtocolMessages.DELIMITER+y+ProtocolMessages.DELIMITER+"true")); // Assert that the ship was hit
                            assertTrue(in2.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+x+ProtocolMessages.DELIMITER+y+ProtocolMessages.DELIMITER+"true")); // Assert that the ship was hit
    
                        }
    
                    }
    
                }

                /* Assert that the game is over after all ships are hit and that the client 1 has won */
                assertTrue(in1.readLine().contains(ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+GameTest.SECOND_PLAYER_NAME+ProtocolMessages.DELIMITER+"true"));
                assertTrue(in2.readLine().contains(ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+GameTest.SECOND_PLAYER_NAME+ProtocolMessages.DELIMITER+"true"));
            }




            // Close the connection to server and communication with server
            pingSocket1.close();
            out1.close();
            in1.close();
            pingSocket2.close();
            out2.close();
            in2.close();
            server.shutdownServer();
        } catch (IOException e) {
            
        }
    }
}
