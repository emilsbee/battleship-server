// External imports
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

// Internal imports
import exceptions.ExitProgram;

public class GameServer implements Runnable {
    
    // Server socket for the game server
    private ServerSocket serverSocket;

    // Re-usable variable used to check whether someone is connected and waiting for an opponent
    private GameClientHandler clientWaitingForGame;

    // The terminal view of this server
    private GameServerTUI view;

    // Basically the id of the game. To distinguish messages from various games.
    private int gameCount; 

    /**
     * Starts the server on a new thread.
     * @param args Command line arguments passed when running this program
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        System.out.println(TerminalColors.BLUE_BOLD + "Welcome to the Battleship game server!" + TerminalColors.RESET);
        GameServer server = new GameServer();
        new Thread(server).start();
    }

    /**
     * Constructor that only initialises the terminal view of this server
     * @throws IOException
     */
    public GameServer() throws IOException {
        view = new GameServerTUI();
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
                setup();

                gameCount++;
                Game game = new Game(view, gameCount);
                while (true) {
                    view.showMessage(TerminalColors.BLUE_BOLD + "Listening for player connections..." + TerminalColors.RESET);
                    Socket socket = serverSocket.accept();
                    view.showMessage(TerminalColors.GREEN_BOLD + "New client connected!" + TerminalColors.RESET);

                    if (clientWaitingForGame == null) { // If nobody is waiting for an opponent
                        
                        if (game == null) {
                            gameCount++;
                            game = new Game(view, gameCount);
                        } 

                        GameClientHandler handler = new GameClientHandler(socket, this, game, view);
                        new Thread(handler).start();
                        clientWaitingForGame = handler;
                    
                    } else { // If somebody is waiting for an opponent

                        GameClientHandler handler = new GameClientHandler(socket, this, game, view);
                        new Thread(handler).start();
                        clientWaitingForGame = null;
                        game = null;
                    }
                }
            } catch (ExitProgram ee) {
                view.showMessage(TerminalColors.RED_BOLD + ee.getMessage()+ TerminalColors.RESET);
                openNewSocket = false;
            } catch (IOException ie) {
                view.showMessage(TerminalColors.RED_BOLD + "A server IO error occurred: " + ie.getMessage()+ TerminalColors.RESET);
                openNewSocket = false;
            }
        }
        
 		view.showMessage(TerminalColors.RED_BOLD +"Server turning off."+ TerminalColors.RESET);
	}

    /**
     * Sets up a server socket on a specific port that is given by the user. 
     * @throws ExitProgram if socket can't be created on the specific port.
     */
    public void setup() throws ExitProgram {
        serverSocket = null;

        while (serverSocket == null) {
            int port = view.getInt("Please enter the server port: ");

            try {
                serverSocket = new ServerSocket(port);
                view.showMessage(TerminalColors.GREEN_BOLD + "Server started on port " + port + TerminalColors.RESET);
            } catch (IOException e) {
                throw new ExitProgram(TerminalColors.RED_BOLD + "Game server stopped due to failed socket creation. Try again by starting server on a different port perhaps."+ TerminalColors.RESET);
            }
        }
    }
}
