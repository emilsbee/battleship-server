import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import protocol.ProtocolMessages;

public class GameClientHandler implements Runnable {
    // The socket and In- and OutputStreams 
    private ObjectInputStream in;
	private ObjectOutputStream out;
    private Socket socket;
    
    // The connected game server
    GameServer server;

    // Player's name
    private String name;

    // The game
    private Game game;
    
    /**
     * Constructs a new GameClientHandler. Opens the ObjectInputStream and ObjectOutputStream.
     * Important to remember that ObjectOutputStream has to be created before the ObjectInputStream.
     * @param socket The client socket
     * @param server  The connected server
     * @param game The game instance
	 */
    public GameClientHandler(Socket socket, GameServer server, Game game) {
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.socket = socket;
            this.server = server;
            this.game = game;
        } catch (IOException e) {
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
            shutdown();
        } 
    }
    
    /**
     * Waits for the input of a gameboard from the client through a new ObjectInputStream which
     * is closed after receiving the gameboard.
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void listenForGameBoard() throws IOException, ClassNotFoundException {
        ObjectInputStream gameBoardIn = new ObjectInputStream(socket.getInputStream());
        GameBoard board;
        try {
            board = (GameBoard) gameBoardIn.readObject();
        } catch (IOException e) {
            shutdown();
        } 
        // gameBoardIn.close();
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
            this.name = playerName;
            sendMessage(server.getHello(playerName));
            game.setPlayer(this);
		} else if (input.equals(ProtocolMessages.CLIENTBOARD)) { // Clientboard 
            listenForGameBoard();
        } 
            if (!input.isEmpty()) {
                System.out.println(input);  
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
     * 
     * @return This threads client's name.
     */
    public String getName() {
        return this.name;
    }

    /**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
	 */
	private void shutdown() {
		System.out.println("> " + name + " Shutting down.");
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.removeClient(this);
	}
}
