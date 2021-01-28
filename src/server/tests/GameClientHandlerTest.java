package server.tests;

// External imports
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

// Internal imports
import server.GameClientHandler;
import exceptions.ProtocolException;
import protocol.ProtocolMessages;
import server.GameServer;


/**
 * These tests mostly test out the protocol messages. It send the messages on behalf of clients and expects to receives the 
 * correct protocol messages back. Although there is some interacation with the game by the GameClientHandler that is tested,
 * the tests don't take into account the correctness of the game mechanics. 
 */
public class GameClientHandlerTest {
    private static final int PORT = 8888;
    private static final String FIRST_PLAYER_NAME = "Albert";
    private static final String SECOND_PLAYER_NAME = "Sam";
    private static final String ENCODED_BOARD = "b;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;PATROL;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;PATROL;WATER;WATER;PATROL;WATER;WATER;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER;PATROL;CARRIER_FRONT;CARRIER_FRONT_MID;CARRIER_MID;CARRIER_BACK_MID;CARRIER_BACK;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;PATROL;WATER;WATER;WATER;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;DESTROYER_FRONT;DESTROYER_MID;DESTROYER_BACK;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;WATER;WATER;WATER;CARRIER_FRONT;CARRIER_FRONT_MID;CARRIER_MID;CARRIER_BACK_MID;CARRIER_BACK;WATER;BATTLESHIP_FRONT;BATTLESHIP_FRONT_MID;BATTLESHIP_BACK_MID;BATTLESHIP_BACK;WATER;WATER;WATER;WATER;WATER;WATER;WATER;PATROL;WATER;WATER;WATER;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;SUPER_PATROL_FRONT;SUPER_PATROL_BACK;WATER;WATER";

    GameServer server;



    @BeforeEach
    void setupServer() {
        server = new GameServer(new String[]{String.valueOf(GameClientHandlerTest.PORT)});
    }

    @AfterEach
    void closeServer() {
        server.shutdownServer();
    }

    @Test
    void testHandshake() {
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Establish connection to server, and socket reading/writing
            pingSocket = new Socket("localhost", GameClientHandlerTest.PORT);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));

            out.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send a handshake
            
            assertEquals(ProtocolMessages.HANDSHAKE, in.readLine()); // Read received handshake from server


            // Close the connection to server and communication with server
            pingSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            
        }
    }


    @Test
    void testNameExists() {

        Socket pingSocket1 = null;
        PrintWriter out1 = null;
        BufferedReader in1 = null;
        Socket pingSocket2 = null;
        PrintWriter out2 = null;
        BufferedReader in2 = null;

        try {
            // Connect client 1 to server
            pingSocket1 = new Socket("localhost", GameClientHandlerTest.PORT);
            out1 = new PrintWriter(pingSocket1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(pingSocket1.getInputStream()));

            // Connect client 2 to server
            pingSocket2 = new Socket("localhost", GameClientHandlerTest.PORT);
            out2 = new PrintWriter(pingSocket2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(pingSocket2.getInputStream()));
            

            out1.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send first handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in1.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            out2.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send the same name as in first handshake
            assertEquals(ProtocolMessages.NAME_EXISTS, in2.readLine()); // Assert that the message received is the protocol message NAME_EXISTS


            // Close the connection to server and communication with server
            pingSocket1.close();
            out1.close();
            in1.close();
            pingSocket2.close();
            out2.close();
            in2.close();
        } catch (IOException e) {
            
        }
    }

    @Test
    void testEnemyName() {
        Socket pingSocket1 = null;
        PrintWriter out1 = null;
        BufferedReader in1 = null;
        Socket pingSocket2 = null;
        PrintWriter out2 = null;
        BufferedReader in2 = null;

        try {
            // Connect client 1 to server
            pingSocket1 = new Socket("localhost", GameClientHandlerTest.PORT);
            out1 = new PrintWriter(pingSocket1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(pingSocket1.getInputStream()));

            // Connect client 2 to server
            pingSocket2 = new Socket("localhost", GameClientHandlerTest.PORT);
            out2 = new PrintWriter(pingSocket2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(pingSocket2.getInputStream()));
            

            out1.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send first handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in1.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            out2.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME); // Send second handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in2.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME, in1.readLine()); // Assert that the client 1 receives second player name
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME, in2.readLine()); // Assert that the client 2 receives first player name

            // Close the connection to server and communication with server
            pingSocket1.close();
            out1.close();
            in1.close();
            pingSocket2.close();
            out2.close();
            in2.close();
        } catch (IOException e) {
            
        }
    }


    @Test
    void testClientBoard() {
        Socket pingSocket1 = null;
        PrintWriter out1 = null;
        BufferedReader in1 = null;
        Socket pingSocket2 = null;
        PrintWriter out2 = null;
        BufferedReader in2 = null;

        try {
            // Connect client 1 to server
            pingSocket1 = new Socket("localhost", GameClientHandlerTest.PORT);
            out1 = new PrintWriter(pingSocket1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(pingSocket1.getInputStream()));

            // Connect client 2 to server
            pingSocket2 = new Socket("localhost", GameClientHandlerTest.PORT);
            out2 = new PrintWriter(pingSocket2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(pingSocket2.getInputStream()));
            

            out1.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send first handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in1.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            out2.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME); // Send second handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in2.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME, in1.readLine()); // Assert that the client 1 receives second player name
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME, in2.readLine()); // Assert that the client 2 receives first player name


            out1.println(GameClientHandlerTest.ENCODED_BOARD); // Client 1 sends their encoded board
            out2.println(GameClientHandlerTest.ENCODED_BOARD); // Client 2 sends their encoded board
            assertTrue(in1.readLine().contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random
            assertTrue(in2.readLine().contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random    

            // Close the connection to server and communication with server
            pingSocket1.close();
            out1.close();
            in1.close();
            pingSocket2.close();
            out2.close();
            in2.close();
        } catch (IOException e) {
            
        }
    }

    /**
     * Connects two clients to the server, sends respective messages in order to get to the point to make a move.
     * Then both client send one move and assertions are made that both receive UPDATE messages.
     */
    @Test
    void testMove() {
        // move[0]: x coordinate, move[1]: y coordinate
        int[] move1 = new int[]{2,3};
        int[] move2 = new int[]{5,9};

        Socket pingSocket1 = null;
        PrintWriter out1 = null;
        BufferedReader in1 = null;
        Socket pingSocket2 = null;
        PrintWriter out2 = null;
        BufferedReader in2 = null;

        try {
            // Connect client 1 to server
            pingSocket1 = new Socket("localhost", GameClientHandlerTest.PORT);
            out1 = new PrintWriter(pingSocket1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(pingSocket1.getInputStream()));

            // Connect client 2 to server
            pingSocket2 = new Socket("localhost", GameClientHandlerTest.PORT);
            out2 = new PrintWriter(pingSocket2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(pingSocket2.getInputStream()));
            
            /* Client 1 handshake*/
            out1.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send first handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in1.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            /* Client 2 handshake*/
            out2.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME); // Send second handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in2.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            /* Both client enemy names */
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME, in1.readLine()); // Assert that the client 1 receives second player name
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME, in2.readLine()); // Assert that the client 2 receives first player name


            /* Both clients send their encoded game board*/
            out1.println(GameClientHandlerTest.ENCODED_BOARD); // Client 1 sends their encoded board
            out2.println(GameClientHandlerTest.ENCODED_BOARD); // Client 2 sends their encoded board
            
            /* Both clients receive SETUP message*/
            String gameSetupResponse = in1.readLine(); // The response is needed to determine which client should make the move first
            assertTrue(gameSetupResponse.contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random
            assertTrue(in2.readLine().contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random    

            /* Make the first move*/
            if (gameSetupResponse.split(";")[1].equals(GameClientHandlerTest.FIRST_PLAYER_NAME)) {
                out1.println(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move1[0]+ProtocolMessages.DELIMITER+move1[1]); // Make the move1 on behalf of client 1
            } else {
                out2.println(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move1[0]+ProtocolMessages.DELIMITER+move1[1]); // Make the move1 on behalf of client 2
            }

            /* Assert the first move*/
            String updateMessage = in1.readLine();
            assertTrue(updateMessage.contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+move1[0]+ProtocolMessages.DELIMITER+move1[1])); // Assert that the UPDATE message is received and includes the coordinates of the move1
            assertTrue(in2.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+move1[0]+ProtocolMessages.DELIMITER+move1[1])); // Assert that the UPDATE message is received and includes the coordinates of the move1

            
            /* Make the second move*/
            if (gameSetupResponse.contains(GameClientHandlerTest.FIRST_PLAYER_NAME)) { // From the update message determines which client should go next

                out1.println(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move2[0]+ProtocolMessages.DELIMITER+move2[1]); // Make the move2 on behalf of client 1
            
            } else {
            
                out2.println(ProtocolMessages.MOVE+ProtocolMessages.DELIMITER+move2[0]+ProtocolMessages.DELIMITER+move2[1]); // Make the move2 on behalf of client 2
            
            }

            /* Assert the second move*/
            assertTrue(in1.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+move2[0]+ProtocolMessages.DELIMITER+move2[1])); // Assert that the UPDATE message is received and includes the coordinates of the move2
            assertTrue(in2.readLine().contains(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+move2[0]+ProtocolMessages.DELIMITER+move2[1])); // Assert that the UPDATE message is received and includes the coordinates of the move2


            // Close the connection to server and communication with server
            pingSocket1.close();
            out1.close();
            in1.close();
            pingSocket2.close();
            out2.close();
            in2.close();
        } catch (IOException e) {
            
        }
    }

    
    @Test
    void testGameOverByQuitting() {
        Socket pingSocket1 = null;
        PrintWriter out1 = null;
        BufferedReader in1 = null;
        Socket pingSocket2 = null;
        PrintWriter out2 = null;
        BufferedReader in2 = null;

        try {
            // Connect client 1 to server
            pingSocket1 = new Socket("localhost", GameClientHandlerTest.PORT);
            out1 = new PrintWriter(pingSocket1.getOutputStream(), true);
            in1 = new BufferedReader(new InputStreamReader(pingSocket1.getInputStream()));

            // Connect client 2 to server
            pingSocket2 = new Socket("localhost", GameClientHandlerTest.PORT);
            out2 = new PrintWriter(pingSocket2.getOutputStream(), true);
            in2 = new BufferedReader(new InputStreamReader(pingSocket2.getInputStream()));
            
            /* Client 1 handshake*/
            out1.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME); // Send first handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in1.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            /* Client 2 handshake*/
            out2.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME); // Send second handshake
            assertEquals(ProtocolMessages.HANDSHAKE, in2.readLine()); // Assert that the message received is the protocol message HANDSHAKE

            /* Both client enemy names */
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.SECOND_PLAYER_NAME, in1.readLine()); // Assert that the client 1 receives second player name
            assertEquals(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+GameClientHandlerTest.FIRST_PLAYER_NAME, in2.readLine()); // Assert that the client 2 receives first player name


            /* Both clients send their encoded game board*/
            out1.println(GameClientHandlerTest.ENCODED_BOARD); // Client 1 sends their encoded board
            out2.println(GameClientHandlerTest.ENCODED_BOARD); // Client 2 sends their encoded board
            
            /* Both clients receive SETUP message*/
            String gameSetupResponse = in1.readLine(); // The response is needed to determine which client should make the move first
            assertTrue(gameSetupResponse.contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random
            assertTrue(in2.readLine().contains(ProtocolMessages.SETUP)); // Assert that response contain the SETUP message since the player who goes is chosen at random    

            
            /* Client 1 sends EXIT message */
            out1.println(ProtocolMessages.EXIT);
            assertTrue(in2.readLine().contains(ProtocolMessages.GAMEOVER));

            // Close the connection to server and communication with server
            pingSocket1.close();
            out1.close();
            in1.close();
            pingSocket2.close();
            out2.close();
            in2.close();
        } catch (IOException e) {
            
        }
    }

    @Test
    void testProtocolException() {

        GameClientHandler handler = new GameClientHandler();

        try {
            handler.handleCommand(ProtocolMessages.HANDSHAKE);
        } catch (ProtocolException e) {
            assertEquals(GameClientHandler.HANDSHAKE_EXCEPTION_MSG, e.getMessage());
        }

    }
}
 