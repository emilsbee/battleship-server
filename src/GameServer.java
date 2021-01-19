import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import exceptions.ExitProgram;
import protocol.ProtocolMessages;
import protocol.ServerProtocol;

public class GameServer implements Runnable, ServerProtocol {
    
    // Server socket for the game server
    private ServerSocket serverSocket;


    private List<Game> games;

    private GameClientHandler clientWaitingForGame;

    // The game
    private Game game;

    // The view of this GameServer
    private GameServerTUI view;

    public GameServer() throws IOException {
        games = new ArrayList<>();
        view = new GameServerTUI();
        game = new Game(view, this);
    }
    public static void main(String[] args) throws IOException {
        System.out.println(TerminalColors.BLUE_BOLD + "Welcome to the Battleship game server!" + TerminalColors.RESET);
        GameServer server = new GameServer();
        new Thread(server).start();
    }

    /**sendMessage
     * Calls {@link #setup()} to open up a server socket. Then listens for new client connections and accepts them only until 
     * two clients have connected. 
     * 
     * When {@link #setup()} throws exit program, the server socket is closed 
     */
	@Override
	public void run() {
        boolean openNewSocket = true; // Indicates whether to keep listening for new client connections.
        while(openNewSocket) {
            try {
                setup();

                game = new Game(view, this);
                while (true) {
                    view.showMessage(TerminalColors.BLUE + "Listening for player connections..." + TerminalColors.RESET);
                    Socket socket = serverSocket.accept();
                    view.showMessage(TerminalColors.GREEN + "New client connected!" + TerminalColors.RESET);

                    if (clientWaitingForGame == null) { // If nobody is waiting for an opponent
                        
                        if (game == null) {
                            game = new Game(view, this);
                        } 

                        GameClientHandler handler = new GameClientHandler(socket, this, game);
                        new Thread(handler).start();
                        clientWaitingForGame = handler;
                    
                    } else { // If somebody is waiting for an opponent

                        GameClientHandler handler = new GameClientHandler(socket, this, game);
                        new Thread(handler).start();
                        games.add(game);
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
                view.showMessage(TerminalColors.BLUE + "Attempting to open a socket at 127.0.0.1 on port " + port + "..." + TerminalColors.RESET);
                serverSocket = new ServerSocket(port);
                view.showMessage(TerminalColors.GREEN + "Server started at port " + port + TerminalColors.RESET);
            } catch (IOException e) {
                view.showMessage(TerminalColors.RED_BOLD + "ERROR: could not create a socket on 127.0.0.1" + " and port " + port + "." + TerminalColors.RESET);
                throw new ExitProgram(TerminalColors.RED_BOLD + "Game server stopped due to failed socket creation. Try again by starting server on a different port perhaps."+ TerminalColors.RESET);
            }
        }
    }

 
	@Override
	public String getHello(String playerName) {
		return ProtocolMessages.HANDSHAKE;
	}

    @Override
    public String enemyName(String playerName) {
        return ProtocolMessages.ENEMYNAME + ProtocolMessages.DELIMITER + playerName;
    }

	@Override
	public String gameSetup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean move(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String update(int x, int y, boolean isHit, boolean isSunk, boolean isTurn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String gameOver(int result) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void clientBoard(String[][] board, String playerName) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
}
