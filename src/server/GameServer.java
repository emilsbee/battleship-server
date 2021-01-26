package server;

// External imports
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Internal imports
import client_handler.GameClientHandler;
import exceptions.ServerSocketException;
import game.Game;
import tui.GameServerTUI;
import tui.TerminalColors;

/**
 * This class represents the game server that accepts clients and matches them up for a game. 
 */
public class GameServer implements Runnable {
    
    // Server socket for the game server
    private ServerSocket serverSocket;

    // Re-usable variable used to check whether someone is connected and waiting for an opponent
    private GameClientHandler clientWaitingForGame;

    // The terminal view of this server
    private GameServerTUI view;

    // The id of the last game. 
    private int gameCount; 

    // The port number on which server is hosted.
    private int port;

    /**
     * Starts the server on a new thread.
     * @param args May include the server port,
     */
    public static void main(String[] args) {
        GameServer server = new GameServer(args);
        new Thread(server).start();
    }

    /**
     * Checks whether a server port was provided, if it was and it's a valid number, sets that as the port.
     * If however the port provided is an invalid number it sets it to the default 8888 port.
     * If no port is provided the sets the port to 0 which will indicate the {@link #setup()} that it needs
     * to prompt the user for a port. Also initialises the gameCount (which is basically id of games) to 0
     * and starts a new TUI.
     * @param args May include the server port.
     */
    public GameServer(String[] args) {
        if (args.length >= 1) { 
            
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                port = 8888;
            }

        } else {
        
            port = 0;
        
        }
        
        view = new GameServerTUI();
        view.showMessage(TerminalColors.BLUE_BOLD + "Welcome to the Battleship game server! " + TerminalColors.RESET);
        
        gameCount = 0;
    }

    /**
     * Server's loop for listening for new client connections. To pair up clients for games it uses a simple
     * approach whereby first client connected waits until someone else connects and then they are paired for a game.
     * Then next client that connects again waits for an opponent and so forth.
     */
	@Override
	public void run() {
        boolean openNewSocket = true; // Indicates whether to keep listening for new client connections.

        while(openNewSocket) {
            try {
                setup(); // Establishes a server socket

                Game game = new Game(view, gameCount); // Initialises the first game

                while (true) {

                    view.showMessage(TerminalColors.BLUE_BOLD + "Listening for player connections..." + TerminalColors.RESET);
                    Socket socket = serverSocket.accept(); // Listens for new clients
                    view.showMessage(TerminalColors.GREEN_BOLD + "New client connected!" + TerminalColors.RESET);

                    if (clientWaitingForGame == null) { // If nobody is waiting for an opponent
                             
                        gameCount++; // Increments the gameCount so next game has unique id
                        game = new Game(view, gameCount);

                        // Creates and starts a new client handler
                        GameClientHandler handler = new GameClientHandler(socket, game, view);
                        new Thread(handler).start();

                        // Indicates that someone is waiting for a game
                        clientWaitingForGame = handler;
                    
                    } else { // If somebody is waiting for an opponent

                        // Creates amd starts a new client handler
                        GameClientHandler handler = new GameClientHandler(socket, game, view);
                        new Thread(handler).start();

                        // Indicates that nobody is waiting for a game
                        clientWaitingForGame = null;
                    }

                }

            } catch (IOException ie) {
            
                view.showMessage(TerminalColors.RED_BOLD + "An IO occured listening to new clients. " + TerminalColors.RESET);
                openNewSocket = false;
            
            } catch (ServerSocketException e) {
				view.showMessage(TerminalColors.RED_BOLD + e.getMessage()+ TerminalColors.RESET);
                openNewSocket = false;
			}
        }
        
	}

    /**
     * Sets up a server socket on a specific port that is either given by the user or is prompted. 
     * @throws ServerSocketException
     * @throws ExitProgram if socket can't be created on the specific port.
     */
    public void setup() throws ServerSocketException  {
        serverSocket = null;

        while (serverSocket == null) {
            
            if ( port == 0) {
                port = view.getInt("Please enter the server port: ");
            }

            try {
				serverSocket = new ServerSocket(port);
                view.showMessage(TerminalColors.GREEN_BOLD + "Server started on port " + port + TerminalColors.RESET);
			} catch (IOException e) {
				throw new ServerSocketException("There was a problem establishing the server socket.");
			}
            
        }
    }
}
