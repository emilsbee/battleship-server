package client;

// External imports
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ProtocolException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

// Internal imports
import game.Game;
import protocol.ProtocolMessages;
import protocol.ServerProtocol;
import server.GameServer;
import tui.GameServerTUI;

public class GameClientHandler implements Runnable, ServerProtocol {
    // The socket input and output streams    
    private BufferedReader in;
    private BufferedWriter out;

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
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

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
            input = in.readLine();
            while (input != null) {
                handleCommand(input);
                input = in.readLine();
            }
        } catch (IOException e) {
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
    public void handleCommand(String input) throws ProtocolException {
        
        if (input.split(";")[0].equals(ProtocolMessages.HANDSHAKE)) { // Handshake
            
            try {
                String playerName = input.split(";")[1];
                handleHello(playerName);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ProtocolException("Client didn't provide name in the handshake.");
            }

		} else if (input.split(";")[0].equals(ProtocolMessages.CLIENTBOARD)) { // Clientboard 
            
            clientBoard(input);

        } else if (input.split(";")[0].equals(ProtocolMessages.MOVE)) { // Move
            try {
                int x = Integer.parseInt(input.split(";")[1]);
                int y = Integer.parseInt(input.split(";")[2]);
                view.showMessage("X: "+ x + "Y: "+ y);
                move(x, y);
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                throw new ProtocolException("Client didn't provide correct x and y values.");
            }

        } else if (input.equals(ProtocolMessages.EXIT)) {
            exit();
        }
        // if (!input.isEmpty()) {
        //     System.out.println(input);  
        // }
    }
    

    
    /**
     * This method is always called by the game. It creates a 30 second timer and if
     * the respective client doesn't send their move then this timer wakes up the game
     * thread and informs it that their client didn't make a move.
     */
    public void makeMove() {
        Timer timer = new Timer("Timer");
    

        task = new TimerTask() {
            public void run() {
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
     * Sends a String message to the client through writeUTF.
     * @param message The message to send to the client.
     */
    public void sendMessage(String message)  {
        if (out != null) {
            try {
                out.write(message);
                out.newLine();
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

	@Override
	public void handleHello(String playerName) {
		if (game.isValidPlayerName(playerName)) {
            this.name = playerName;
            sendMessage(ProtocolMessages.HANDSHAKE);
            game.setPlayer(this);
        } else {
            nameExists();
        }
	}

	@Override
	public void nameExists() {
        sendMessage(ProtocolMessages.NAME_EXISTS);
    }

    @Override
	public void enemyName(String playerName) {
		sendMessage(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+playerName);
	}

    @Override
	public void clientBoard(String encodedBoard) {
        game.setBoard(encodedBoard, name);
    }
    
    @Override
	public void gameSetup(String playerName) {
		sendMessage(ProtocolMessages.SETUP+ProtocolMessages.DELIMITER+playerName);
	}

    @Override
    public void move(int x, int y) {
        game.makeMove(x, y, false);
        synchronized (game) { 
            task.cancel();
            game.notifyAll();
        }
    }

	@Override
	public void update(int x, int y, boolean isHit, boolean isSunk, boolean isLate, String lastPlayerName, String nextPlayerName) {
        sendMessage(
            ProtocolMessages.UPDATE + 
            ProtocolMessages.DELIMITER + 
            String.valueOf(x) +
            ProtocolMessages.DELIMITER + 
            String.valueOf(y) + 
            ProtocolMessages.DELIMITER +
            String.valueOf(isHit) + 
            ProtocolMessages.DELIMITER +
            String.valueOf(isSunk) + 
            ProtocolMessages.DELIMITER +
            String.valueOf(isLate) +
            ProtocolMessages.DELIMITER +
            lastPlayerName +
            ProtocolMessages.DELIMITER +
            nextPlayerName
        );
		
	}

	@Override
	public void gameOver(String playerName, boolean winType) {
		sendMessage(ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+playerName+ProtocolMessages.DELIMITER+winType);
	}

	@Override
	public void exit() {
		shutdown();
	}

	
}
