// External imports
import java.util.Timer;
import java.util.TimerTask;

// Internal imports
import protocol.ProtocolMessages;
import protocol.ServerProtocol;

public class Game implements Runnable, ServerProtocol {
    // The id of the game
    private int gameId;

    // Player names
    private GameClientHandler player1;
    private GameClientHandler player2;

    // Player boards
    private GameBoard player1Board;
    private GameBoard player2Board;

    // The server TUI
    private GameServerTUI view;

    // Indicator for actual start of gane
    private boolean gameStarted;

    /**
     * Constructor that initialises this game's id and the terminal view, and sets the game started to false
     * since the game only starts after both players have sent in their boards.
     * @param view
     * @param gameId
     */
    public Game(GameServerTUI view, int gameId) {
        this.gameId = gameId; 
        this.view = view;
        gameStarted = false;
    }

    /**
     * The main game loop. This is called when this game is put in a thread and the start() 
     * method is called. Although this isn't exactly a loop, a timer is set for 5 minutes after which the
     * game automatically ends and whoever has most points wins, if equal then tie.
     */
    @Override
	public void run() {
        view.showMessage("Game " + gameId + ": started");
        TimerTask task = new TimerTask(){
            public void run() {
                view.showMessage("Game " + gameId + ": ended!");
            }
        };

        Timer timer = new Timer("Timer");

        long delay = 5000L;
        timer.schedule(task,delay);
	}


    /**
     * After a succesful handshake with the client whereby a uniqe name is gotten
     * this method is called by the GameClientHandler thread to add the client to the game. 
     * Starting from player1. So player1 will always be the first connected, then player 2.
     * @param player The player to be added to the game.
     */
    public synchronized void setPlayer(GameClientHandler player) {
        if (player1 == null) {
            player1 = player;
            view.showMessage("Game " + gameId + ": Player 1 added. Player name: " + player1.getName());
        } else if (player2 == null) {
            player2 = player;
            view.showMessage("Game " + gameId + ": Player 2 added. Player name: " + player2.getName());
            sendEnemyName();
        } 
    }

    /**
     * After both players have connected and their unique names are stored in this class
     * this method is called and it send the players their opponent's name.
     * The message is sent through the respective player's GameClientHandler {@link #sendMessage()} method.
     */
    public void sendEnemyName() {
        player1.sendMessage(enemyName(player2.getName()));
        player2.sendMessage(enemyName(player1.getName()));
    }


    /**
     * Sets the respective player's game board. Since in this class the players are just called player1
     * and player2 to know which player is setting their board, the names are compared with the one provided when calling method
     * to the names of the players in this game. This method is synchronized since it can be called by both player1 and player2 GameClientHandler
     * threads at the same time. But that should not be possible as the last one to call this method starts up the game so the calls for setting board
     * must be synchronized.
     * @param board The board to be set.
     * @param playerName The name of the player for which the board is to be set.
     */
    public synchronized void setBoard(GameBoard board, String playerName) {
        if (player1.getName().equals(playerName)) {
            player1Board = board;
            if (player2Board != null && !gameStarted) {
                startGame();
            }
        } else if (player2.getName().equals(playerName)) {
            player2Board = board;
            if (player1Board != null && !gameStarted) {
                startGame();
            }
        }
    }

    /**
     * Method to start the game by creating a separate thread for it. This is only called
     * after both players have submitted their boards.
     */
    public void startGame() {
        gameStarted = true;
        new Thread(this).start();
    }

    /**
     * Used by GameClientHandler thread to check whether the client has submitted a uniqe name. 
     * Since player1 is always connected first there is a check for player1 == null and the name, whatever it is, 
     * has to be unique. Then when player2 connects the name provided by it is compared to player1 name by the second part.
     * @param playerName The name to be checked
     * @return Whether the name can be used in this game.
     */
    public synchronized boolean isValidPlayerName(String playerName) {
        return (
            player1 == null || 
            !player1.getName().equals(playerName) 
        );
    }

    public void endGame() {
        player1 = null;
        player2 = null;
    }

	@Override
	public synchronized String getHello(String playerName) {
		return ProtocolMessages.HANDSHAKE;
    }
    
    @Override
	public synchronized String nameExists() {
		return ProtocolMessages.NAME_EXISTS;
	}

    @Override
    public synchronized String enemyName(String playerName) {
        return ProtocolMessages.ENEMYNAME + ProtocolMessages.DELIMITER + playerName;
    }

	@Override
	public synchronized String gameSetup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized boolean move(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized String update(int x, int y, boolean isHit, boolean isSunk, boolean isTurn) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized String gameOver(int result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void exit() {
		// TODO Auto-generated method stub
		
	}



	


	
}
