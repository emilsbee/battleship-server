package client;

// External imports
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

// Internal imports
import game.Game;
import protocol.ProtocolMessages;
import server.GameServer;
import tui.GameServerTUI;

public class GameClientHandler implements Runnable {
    // The socket input and output streams
    private ObjectInputStream in;
    private ObjectOutputStream out;
    
    // The client socket
    private Socket socket;
    
    // The main server
    GameServer server;

    // Player's name
    private String name;

    // The game that this player is participating in
    private Game game;
    
    // The terminal view of this server
    private GameServerTUI view;

    private TimerTask task;


    /**
     * Constructs a new GameClientHandler. Opens the ObjectInputStream and ObjectOutputStream.
     * Important to remember that ObjectOutputStream has to be created before the ObjectInputStream.
     * @param socket The client socket
     * @param server  The connected server
     * @param game The game instance
     * @param view the terminal view of the server
	 */
    public GameClientHandler(Socket socket, GameServer server, Game game, GameServerTUI view) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.socket = socket;
            this.server = server;
            this.game = game;
            this.view = view;
        } catch (IOException e) {
            view.showMessage(name + "'s thread is having an IO problem creating input and output streams.");
            shutdown();
        }
    }

    /**
	 * Continuously listens to client input and forwards the input to the
	 * {@link #handleCommand(String)} method.
	 */
	@Override
	public void run() {
        String input;
        
		try {
            input = in.readUTF();
            while (input != null) {
                handleCommand(input);
                input = in.readUTF();
            }
        } catch (IOException | ClassNotFoundException e) {
            view.showMessage(name + "'s thread is having an IO problem reading UTF input.");
            shutdown();
        } 
    }

    /**
     * Handles client sent input.
     * @param input the String input to handle.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void handleCommand(String input) throws IOException, ClassNotFoundException {
        
        if (input.split(";")[0].equals(ProtocolMessages.HANDSHAKE) && input.split(";").length >= 2) { // Handshake
            String playerName = input.split(";")[1];

            if (game.isValidPlayerName(playerName)) {
                this.name = playerName;
                sendMessage(game.getHello(playerName));
                game.setPlayer(this);
            } else {
                sendMessage(game.nameExists());
            }
		} else if (input.equals(ProtocolMessages.CLIENTBOARD)) { // Clientboard 
            listenForGameBoard();
        } else if (input.split(";")[0].equals(ProtocolMessages.MOVE)) { // Move
            int x = Integer.parseInt(input.split(";")[1]);
            int y = Integer.parseInt(input.split(";")[2]);
            game.makeMove(x, y, false);
           synchronized (game) {
               task.cancel();
               game.notifyAll();
           }
        }
            // if (!input.isEmpty()) {
            //     System.out.println(input);  
            // }
    }
    
    

    public void makeMove() {
        view.showMessage("Game " + game.getGameId() + " Move timer started");
        Timer timer = new Timer("Timer");
    

        task = new TimerTask() {
            public void run() {
                view.showMessage("Game " + game.getGameId() + " Move timer ended");
                game.makeMove(0, 0, true);
                synchronized (game) {
                    game.notifyAll();
                }
            }
        };

        long delay = 30000L; // 30 seconds
        timer.schedule(task, delay);
    }
  
    /**
     * Waits for the input of a gameboard from the client through a new ObjectInputStream.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void listenForGameBoard() throws IOException, ClassNotFoundException {
        ObjectInputStream gameBoardIn = new ObjectInputStream(socket.getInputStream());
        String[][] board;
        try {
            board = (String[][]) gameBoardIn.readObject();
            game.setBoard(board, name);
        } catch (IOException e) {
            view.showMessage(name + "'s thread is having an IO problem reading game board input.");
            shutdown();
        } 
    }

    /**
     * Sends a String message to the client through writeUTF.
     * @param message The message to send to the client.
     */
    public void sendMessage(String message)  {
        if (out != null) {
            try {
                out.writeUTF(message);
                out.flush();
            } catch (IOException e) {
                e.getStackTrace();
                shutdown();
            }
        } 
    }

    /**
     * Getter for this player's name
     * @return This threads client's name.
     */
    public String getName() {
        return this.name;
    }

    /**
	 * Shuts down the connection to this client by closing the socket and 
	 * the input, output streams.
	 */
	private void shutdown() {
        try {
            in.close();
			out.close();
			socket.close();
            System.out.println(name + " has disconnected.");
		} catch (IOException e) {
			view.showMessage(name + "'s thread is having an IO problem disconnecting.");
		}
	}
}
