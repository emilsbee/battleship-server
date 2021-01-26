package server.tests;

// External imports
import org.junit.jupiter.api.*;

import protocol.ProtocolMessages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

// Internal imports
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
        new Thread(server).start();
    }

    @Test
    void testServerInitialisation() {
        assertEquals(
            TerminalColors.BLUE_BOLD + "Welcome to the Battleship game server!" + TerminalColors.RESET+"\n" + 
            TerminalColors.GREEN_BOLD + "Server started on port " + GameServerTest.PORT + TerminalColors.RESET+"\n" +
            TerminalColors.BLUE_BOLD + "Listening for player connections..." + TerminalColors.RESET+"\n",
            outContent.toString() 
        );
        outContent.reset();

        assertNotNull(server.getServerSocket());
        assertEquals(0, server.getGameCount());
    }

    @Test 
    void testFirstClientConnection() {
        int gameId = 1;
        Socket pingSocket = null;
        PrintWriter out = null;
        
        try {
            pingSocket = new Socket("localhost", GameServerTest.PORT);
            out = new PrintWriter(pingSocket.getOutputStream(), true);
        } catch (IOException e) {
            return;
        }

        out.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameServerTest.FIRST_PLAYER_NAME); // Send a handshake
       
        assertNotNull(server.getServerSocket()); // Assert that the server socket is still listening
        assertEquals(gameId, server.getGameCount()); // Assert that the first game has been created
        assertEquals(server.getClientCount(), 1);

        try {
			pingSocket.close();
            out.close();
		} catch (IOException e) {
		    
		}
    }

    // @Test
    // void testSecondClientConnection() {
    //     Socket pingSocket = null;
    //     PrintWriter out = null;
        
    //     try {
    //         pingSocket = new Socket("localhost", GameServerTest.PORT);
    //         out = new PrintWriter(pingSocket.getOutputStream(), true);
    //     } catch (IOException e) {
    //         return;
    //     }

    //     out.println(ProtocolMessages.HANDSHAKE+ProtocolMessages.DELIMITER+GameServerTest.FIRST_PLAYER_NAME); // Send a handshake

    //     assertEquals(outContent.toString(), TerminalColors.GREEN_BOLD + "New client connected!" + TerminalColors.RESET+"\n"); // Asert that new client indeed has connected
    //     assertEquals(outContent.toString(), TerminalColors.GREEN_BOLD + "somethinge" + TerminalColors.RESET+"\n"); // Asert that new client indeed has connected
    //     assertNotNull(server.getServerSocket()); // Assert that the server socket is still listening
    //     assertEquals(server.getGameCount(), 1); // Assert that the first game has been created


    //     outContent.reset();
        
    //     try {
	// 		pingSocket.close();
    //         out.close();
	// 	} catch (IOException e) {
		    
	// 	}
    // }

    @AfterAll
	static void restoreStream() {
        System.setOut(originalOut);
	}
}
