package server.tests;

// External imports
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

// Internal imports
import protocol.ProtocolMessages;
import server.GameServer;
import tui.TerminalColors;

public class GameServerTest {
    private static final int PORT = 8888;
    private static final String FIRST_PLAYER_NAME = "Albert";

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    static GameServer server;

    @BeforeAll
    public static void setUpServer() {
        System.setOut(new PrintStream(outContent));
        server = new GameServer(new String[]{String.valueOf(GameServerTest.PORT)}); 
    }

    @Test
    void testServerInitialisation() {
        // Asserts that the actual output in terminal indicates that server was started without any exceptions
        assertEquals(
            GameServer.SERVER_START_MESSAGE+"\n" + 
            TerminalColors.GREEN_BOLD + "Server started on port " + GameServerTest.PORT + TerminalColors.RESET+"\n" +
            GameServer.SERVER_LISTENING_FOR_CONNECTIONS_MESSAGE+"\n" +
            GameServer.SERVER_NEW_CLIENT_MESSAGE +"\n" +
            GameServer.SERVER_LISTENING_FOR_CONNECTIONS_MESSAGE+"\n" +
            "Game " + 1 + ": Player 1 added. Player name: " + GameServerTest.FIRST_PLAYER_NAME+"\n" ,
            outContent.toString() 
        );
        outContent.reset();

        // Asserts that the server socket was established for listening
        assertNotNull(server.getServerSocket());
    }

    @Test 
    void testClientConnection() {
        Socket pingSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            // Establish connection to server, and socket reading/writing
            pingSocket = new Socket("localhost", GameServerTest.PORT);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(pingSocket.getInputStream()));

            // Test that handshake with the server works which indicates that both sending and recieving messages works and that server succesfully accepts new clients.
            // This test also partly tests the GameClientHandler
            out.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameServerTest.FIRST_PLAYER_NAME); // Send a handshake
            
            assertEquals(ProtocolMessages.HANDSHAKE, in.readLine()); // Read recieved handshake from server

            assertNotNull(server.getServerSocket()); // Assert that the server socket is still listening

            // Close the connection to server and communication with server
            pingSocket.close();
            out.close();
            in.close();
        } catch (IOException e) {
            
        }
    }

    @AfterAll
	static void restoreStream() {
        System.setOut(originalOut);
        server.shutdownServer();
	}
}
