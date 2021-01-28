package server;

// External imports
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import exceptions.ClientUnavailableException;
// Internal imports
import exceptions.ProtocolException;
import game.Game;
import tui.GameServerTUI;
import tui.TerminalColors;

/**
 * Represents the communication with a client. Each client has its own game client handler thread that handles the communication.
 * This class listens and writes messages. So it also implements the server protocol that makes sure all communication is by the protocol.
 * Furthermore, it handles the messages which means that it communicates with the game instance this client is a part of. Both the game calls methods
 * from this class and this class calls method of game. Hence it is a middle man between the client and the game. 
 * @inv socket != null, game != null, view != null
 */
public class GameClientHandler implements Runnable, ServerProtocol {
    public static final String HANDSHAKE_EXCEPTION_MSG = "Client didn't provide name in the handshake."; 
    public static final String MOVE_EXCEPTION_MSG = "Client didn't provide correct x and y values.";

    // The socket input and output streams    
    private BufferedReader in;
    private BufferedWriter out;

    // The client socket
    private Socket socket;
    
    // Player's name
    private String name;

    // The game that this player is participating in
    private Game game;
    
    // The terminal view of this server
    private GameServerTUI view;

    // Re-usable task variable that is used for player move timer
    private TimerTask task;

    // Re-usable timer variable for scheduling the player move timer task
    private Timer timer;

    public GameClientHandler() {
        
    }

    /**
     * Constructs a new GameClientHandler. Opens the BufferedWriter and BufferedReader.
     * @param socket The client socket.
     * @param game The game instance.
     * @param view the terminal view of the server for displaying messages and prompting questions.
     * @pre socket != null, game != null, view != null
     * @post ensures that IO is established through the given socket. Also ensures that socket, game and view are initialised. 
     * Also ensures that the user is informed if IO through socket fails. 
	 */
    public GameClientHandler(Socket socket, Game game, GameServerTUI view) {
        try {

            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.socket = socket;
            this.game = game;
            this.view = view;
        } catch (IOException e) {
            view.showMessage("Game "+ game.getGameId() + ", player: " + name + " is having an IO problem creating input and output streams.");
            exit();
        
        }
    }

    /**
	 * Thread that continuously listens to client input and forwards the input to the
	 * {@link #handleCommand(String)} method.
     * @pre in != null, view != null, game != null
     * @post ensures that messages from client are read and informs user if reading goes wrong
	 */
	@Override
	public void run() {
        String input;
        
		try {
            input = in.readLine();
            
            while (input != null) {
            
                handleCommand(input);
            
                if (in == null) {
                    break;
                }
            
                input = in.readLine();
            }
        } catch (IOException e) {
            view.showMessage("Game "+ game.getGameId() + ", player: " + name + " is having an IO problem reading input.");
            exit();
        } catch (ProtocolException pe) {
            view.showMessage("Game "+ game.getGameId() + ", player: " + name + pe.getMessage());
            exit();
        }
    }

    /**
     * Handles client sent input and calls the respective methods to handle the task related to the message.
     * @param input the String input to handle.
     * @throws ProtocolException when the input provided by client doesnt abide the protocol.
     * @pre input != null, game != null
     * @pre ensures that given correct input protocol message the respective handler methods are called. Also
     * informs the user if the protocol message is just partly correct.
     */
    public void handleCommand(String input) throws ProtocolException {
        
        if (input.split(";")[0].equals(ProtocolMessages.HANDSHAKE)) { // Client sends handshake
            
            try {
                String playerName = input.split(";")[1];
                handleHello(playerName);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new ProtocolException(GameClientHandler.HANDSHAKE_EXCEPTION_MSG);
            }

		} else if (input.split(";")[0].equals(ProtocolMessages.CLIENTBOARD)) { // Client sends their game board 
            
            clientBoard(input);

        } else if (input.split(";")[0].equals(ProtocolMessages.MOVE)) { // Client makes a move

            if (game.getCurrentMove().equals(name)) { // Makes sure that a client can't make a move when it's not their move

                try {
                    int x = Integer.parseInt(input.split(";")[1]);
                    int y = Integer.parseInt(input.split(";")[2]);
                    move(x, y);
                } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
                    throw new ProtocolException(GameClientHandler.MOVE_EXCEPTION_MSG);
                }
            
            }

        } else if (input.equals(ProtocolMessages.EXIT)) { // Client sends message that they are exiting the game

            exit();
        
        }
    }
    

    
    /**
     * This method is called by the game when it's this clients move. It creates a 30 second timer and if
     * the respective client doesn't send their move in time, then this timer makes the 
     * move for them. The move is marked as late so even though it includes valid
     * coordinates, they are not actually taken into account by the game.
     * @pre game != null
     * @post ensures that a 30 seconds timer is set in which time the client must make a move and cancel this timer.
     * If the timer runs out it makes a late move on behalf of the client.
     */
    public void makeMove() {    
        timer = new Timer("Timer");
        task = new TimerTask() {
            public void run() {
                game.makeMove(0, 0, true);
            }
        };

        long delay = 30000L; // 30 seconds
        timer.schedule(task, delay);
    }


    /**
     * Sends a String message to the client.
     * @param message The message to send to the client.
     * @throws ClientUnavailableException
     * @pre message != null, out != null
     * @post ensures that an attempt is made at sending the given message to the client, and informs
     * the user if the sending fails.
     */
    public void sendMessage(String message) throws ClientUnavailableException  {
        if (out != null) {
            try {
                out.write(message);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                throw new ClientUnavailableException("Error while writing to a client.");
            }
        } 
    }


    /**
     * Getter for this player's name
     * @return This threads client's name.
     * @pre name != null
     * @post ensures that the client's name is returned
     */
    public String getName() {
        return this.name;
    }


    /**
	 * Firstly informs the game (if it hasn't already ended) that the client is quitting.
     * Then shuts down the connection to this client by closing the socket and 
	 * the input, output streams.
     * @pre game != null, in != null, out != null, socket != null, view != null
     * @post ensures that an attempt is made to shutdown communication and infor the game about quitting.
     * Also informs the user if the shutdown has failed.
	 */
	private void shutdown() {
        if (in != null && out != null && socket != null) {
            try {
                if (!game.getGameStarted()) {
                    game.endGame(false, name, null);
                }
                in.close();
                out.close();
                socket.close();
                in = null;
                out = null;
                socket = null;
                System.out.println(name + " has disconnected.");
            } catch (IOException e) {
                view.showMessage(name + "'s thread is having an IO problem disconnecting.");
            }
        }
    }
    
    /**
     * Getter for the socket connection. Used for determining whether client has disconnected. 
     * @return The socket.
     * @pre socket != null
     * @post ensures that the socket is returned
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * {@inheritDoc}
     * @pre playerName != null, game != null, view != null
     * @post ensures that client is notified if they chose the same name as the opponents, if not then sends back handshake to them.
     */
	@Override
	public void handleHello(String playerName) {
        if (game.isValidPlayerName(playerName)) { // If the name provided by the client is not taken by the opponent
            
            try {
                this.name = playerName;
				sendMessage(ProtocolMessages.HANDSHAKE);
                game.setPlayer(this);
			} catch (ClientUnavailableException e) {
                view.showMessage(TerminalColors.RED_BOLD + e.getMessage() + TerminalColors.RESET);
                shutdown();
			}
        
        } else {  // If the name is already taken by the opponent
        
            nameExists();
        
        }
	}

    /**
     * {@inheritDoc}
     * @pre view != null
     * @post ensures that an attempt is made at sending message to client informing of duplicate name
     */
	@Override
	public void nameExists() {
        try {
			sendMessage(ProtocolMessages.NAME_EXISTS);
		} catch (ClientUnavailableException e) {
            view.showMessage(TerminalColors.RED_BOLD + e.getMessage() + TerminalColors.RESET);
            shutdown();
		}
    }

    /**
     * {@inheritDoc}
     * @pre playerName != null, view != null
     * @post ensures that an attempt is made at sending the opponent's name to the client
     */
    @Override
	public void enemyName(String playerName) {
		try {
			sendMessage(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+playerName);
		} catch (ClientUnavailableException e) {
            view.showMessage(TerminalColors.RED_BOLD + e.getMessage() + TerminalColors.RESET);
            shutdown();
		}
	}

    /**
     * {@inheritDoc}
     * @pre encodededBoard != null, game != null
     * @post ensures that the client sent encdode board is set in the game 
     */
    @Override
	public void clientBoard(String encodedBoard) {
        game.setBoard(encodedBoard, name);
    }
    
    /**
     * {@inheritDoc}
     * @pre playerName != null, view != null
     * @post ensures that an attempt is made at sending a message a client informing the who goes first. If this
     * client goes first the move timer is started too.
     */
    @Override
	public void gameSetup(String playerName) {
        if (playerName.equals(name)){ // If the first move in the game is for this client
            makeMove();
        }
		try {
			sendMessage(ProtocolMessages.SETUP+ProtocolMessages.DELIMITER+playerName);
		} catch (ClientUnavailableException e) {
            view.showMessage(TerminalColors.RED_BOLD + e.getMessage() + TerminalColors.RESET);
            shutdown();
		}
	}

    /**
     * {@inheritDoc}
     * @pre x >= 0 && x < 15, y >= 0 && y < 10, timer != null, game != null
     * @post ensures that the move timer is canceled and a move is made in the game.
     */
    @Override
    public void move(int x, int y) {
        timer.cancel(); // Cancels the timer that's set before each move.
        game.makeMove(x, y, false);
    }

    /**
     * {@inheritDoc}
     * @pre x >= 0 && x < 15, y >= 0 && y < 10, lastPlayerName != null, nextPlayerName != null, view != null
     * @post ensures that an attempt is made at sending the update message to the client.
     */
	@Override
	public void update(int x, int y, boolean isHit, boolean isSunk, boolean isLate, String lastPlayerName, String nextPlayerName) {
        try {
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
		} catch (ClientUnavailableException e) {
            view.showMessage(TerminalColors.RED_BOLD + e.getMessage() + TerminalColors.RESET);
            shutdown();
		}
		
	}

    /**
     * {@inheritDoc}
     * @pre playerName != null, view != null
     * @post makes an attempt at sending the game over message to the client
     */
	@Override
	public void gameOver(String playerName, boolean winType) {
        try {
			sendMessage(ProtocolMessages.GAMEOVER+ProtocolMessages.DELIMITER+playerName+ProtocolMessages.DELIMITER+winType);
		} catch (ClientUnavailableException e) {
            view.showMessage(TerminalColors.RED_BOLD + e.getMessage() + TerminalColors.RESET);
            shutdown();
		}
	}

    /**
     * {@inheritDoc}
     * @pre game != null
     * @post ensures that game is informed of exitting and the communication is shut down with client
     */
	@Override
	public void exit() {
        game.endGame(false, name, null);
		shutdown();
	}

	
}
