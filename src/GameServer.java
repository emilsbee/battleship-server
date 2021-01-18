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

    // List of GameClientHandlers, one for each client
    private List<GameClientHandler> clients;

    // The game
    private Game game;

    // The view of this GameServer
    private GameServerTUI view;

    public GameServer() throws IOException {
        clients = new ArrayList<>();
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
                while (true) {
                    if (clients.size() < 2) {
                        view.showMessage(TerminalColors.BLUE + "Listening for player connections..." + TerminalColors.RESET);
                        Socket socket = serverSocket.accept();
                        view.showMessage(TerminalColors.GREEN + "New client connected!" + TerminalColors.RESET);
                        GameClientHandler handler = new GameClientHandler(socket, this, game);
                        new Thread(handler).start();
                        clients.add(handler);
                    } 
                }
            } catch (ExitProgram ee) {
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
                throw new ExitProgram(TerminalColors.RED_BOLD + "Game server stopped due to failed socket creation. Try again by starting server on a different port perhaps."+ TerminalColors.RESET);
            }
        }
    }

    /**
	 * Removes a clientHandler from the client list.
	 * @requires client != null
	 */
	public void removeClient(GameClientHandler client) {
		this.clients.remove(client);
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
