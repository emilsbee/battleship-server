package game;

// External imports
import java.util.Random;

// Internal imports
import client.GameClientHandler;
import protocol.ProtocolMessages;
import protocol.ServerProtocol;
import tui.GameServerTUI;

public class Game implements ServerProtocol, Runnable {
    // The id of the game
    private int gameId;

    // Player names
    private GameClientHandler player1;
    private GameClientHandler player2;

    // Player boards
    private String[][] player1Board;
    private String[][] player2Board;

    // The server TUI
    private GameServerTUI view;

    // Indicator for actual start of gane
    private boolean gameStarted;

    // Re-usable random
    Random random;

    private String currentMove;


    /**
     * Constructor that initialises this game's id and the terminal view, and sets the game started to false
     * since the game only starts after both players have sent in their boards.
     * @param view
     * @param gameId
     */
    public Game(GameServerTUI view, int gameId) {
        this.gameId = gameId; 
        this.view = view;
        random = new Random();
        gameStarted = false;
    }

    public int getGameId() {
        return gameId;
    }

    /**
     * The main game loop. This is called when this game is put in a thread and the start() 
     * method is called. Although this isn't exactly a loop, a timer is set for 5 minutes after which the
     * game automatically ends and whoever has most points wins, if equal then tie.
     */
    @Override
	public void run() {
        currentMove = decideWhoStart();
        sendMessageToBothPlayers(gameSetup(currentMove)); // Sends message to both players with information about who starts
        

        view.showMessage("Game " + gameId + ": started");
        
        long t = System.currentTimeMillis(); 
        long end = t + 300000; // Ends in 5 minutes

        while(System.currentTimeMillis() < end) {
                                                   
            view.showMessage("Game " + gameId + " Waiting for turn");
            determineMove();

            try {
                synchronized (this) {
                    wait();
                }
			} catch (InterruptedException e) {}
        }

        view.showMessage("Game " + gameId + ": ended!");
    }

    public void determineMove() {
        if (player1.getName().equals(currentMove)) {
            player1.makeMove();
        } else {
            player2.makeMove();
        }
    }
  

    public void makeMove(int x, int y, boolean isLate) {
        String previousMove = currentMove;

        if (isLate) {
            view.showMessage("Game " + gameId + "late move."); 
        } else {

            view.showMessage("Game " + gameId + "move!"); 
        }

        if (player1.getName().equals(currentMove)) {
            currentMove = player2.getName();
        } else if (player2.getName().equals(currentMove)) {
            currentMove = player1.getName();
        }

        update(x, y, true, true, isLate, previousMove, currentMove);
    }

    
    
    public void sendMessageToBothPlayers(String message) {
        player1.sendMessage(message);
        player2.sendMessage(message);
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
    public synchronized void setBoard(String[][] board, String playerName) {
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

    /**
     * Randomly determines which players goes first.
     * @return The name of the player that goes first.
     */
    public String decideWhoStart() {
        int whoStarts = random.nextInt(2);
        if (whoStarts == 0) {
            return player1.getName();
        } else {
            return player2.getName();
        }
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
	public synchronized String gameSetup(String playerName) {
		return ProtocolMessages.SETUP + ProtocolMessages.DELIMITER + playerName;
	}

	@Override
	public synchronized boolean move(int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public synchronized void update(int x, int y, boolean isHit, boolean isSunk, boolean isLate, String lastPlayerName, String nextPlayerName) {
		sendMessageToBothPlayers(
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
	public synchronized String gameOver(int result) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public synchronized void exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lateMove() {
        if (player1.getName().equals(currentMove)) {
            player1.sendMessage(ProtocolMessages.UPDATE+ProtocolMessages.DELIMITER+player2.getName());
            player2.sendMessage(ProtocolMessages.LATE_MOVE+ProtocolMessages.DELIMITER+player2.getName());
            this.currentMove = player2.getName();
        } else {
            player1.sendMessage(ProtocolMessages.LATE_MOVE+ProtocolMessages.DELIMITER+player1.getName());
            player2.sendMessage(ProtocolMessages.LATE_MOVE+ProtocolMessages.DELIMITER+player1.getName());
            this.currentMove = player1.getName();
        }

	}



	


	
}
