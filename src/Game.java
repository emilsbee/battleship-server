import java.util.Timer;
import java.util.TimerTask;

public class Game implements Runnable {
    // Player names
    private GameClientHandler player1;
    private GameClientHandler player2;

    // Player boards
    private GameBoard player1Board;
    private GameBoard player2Board;

    // The server TUI
    private GameServerTUI view;

    // The game server
    private GameServer server;


    // Indicator for actual start of gane
    private boolean gameStarted;

    public Game(GameServerTUI view, GameServer server) {
        this.view = view;
        this.server = server;
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
    public void setPlayer(GameClientHandler player) {
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
        player1.sendMessage(server.enemyName(player2.getName()));
        player2.sendMessage(server.enemyName(player1.getName()));
    }


    public void setBoard(GameBoard board, String playerName) {
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

    public void endGame() {
        player1 = null;
        player2 = null;
    }


	
}
