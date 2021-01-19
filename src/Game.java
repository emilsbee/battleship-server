// External imports
import java.util.Timer;
import java.util.TimerTask;

// Internal imports
import protocol.ProtocolMessages;
import protocol.ServerProtocol;

public class Game implements Runnable, ServerProtocol {
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

    public Game(GameServerTUI view) {
        this.view = view;
        gameStarted = false;
    }

    @Override
	public void run() {
        view.showMessage("Game started");
        TimerTask task = new TimerTask(){
            public void run() {
                view.showMessage("Game ended!");
            }
        };

        Timer timer = new Timer("Timer");

        long delay = 5000L;
        timer.schedule(task,delay);
	}


    // Sets the player to which ever variable is still available.
    public synchronized void setPlayer(GameClientHandler player) {
        if (player1 == null) {
            player1 = player;
            view.showMessage("Player 1 added. Player name: " + player1.getName());
        } else if (player2 == null) {
            player2 = player;
            view.showMessage("Player 2 added. Player name: " + player2.getName());
            sendEnemyName();
        } 
    }

    public void sendEnemyName() {
        player1.sendMessage(enemyName(player2.getName()));
        player2.sendMessage(enemyName(player1.getName()));
    }


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

    public void startGame() {
        gameStarted = true;
        new Thread(this).start();
    }

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
	public synchronized void clientBoard(String[][] board, String playerName) {
		// TODO Auto-generated method stub
		
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
