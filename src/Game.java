import java.io.IOException;

public class Game implements Runnable{
    // Game loop indicator
    private boolean running;

    // Player names
    private GameClientHandler player1 = null;
    private GameClientHandler player2 = null;

    private boolean player1Connected;
    private boolean player2Connected;

    // The server TUI
    private GameServerTUI view;

    // The game server
    private GameServer server;

    public Game(GameServerTUI view, GameServer server) {
        this.view = view;
        this.server = server;
        player1Connected = false;
        player2Connected = false;
        running = true;
    }

    public void run() {
        boolean init = true;
        view.showMessage("Waiting for both players to connect");
        
        while (running) {   
            
            if (player1Connected && player2Connected) {
                view.showMessage("message");
                while (true) {
                    if (init) {
                        player1.sendMessage(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+player2.getName());
                        player2.sendMessage(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+player1.getName());
                        init = false;
                    }
                }
           

            }
        }

    }

    // Sets the player to which ever variable is still available.
    public void setPlayer(GameClientHandler player) {
        if (player1 == null) {
            player1 = player;
            player1Connected = true;
            view.showMessage("Player 1 added. Player name: " + player1.getName());
        } else if (player2 == null) {
            player2 = player;
            player2Connected = true;
            view.showMessage("Player 2 added. Player name: " + player2.getName());
            startGame();
        } 
    }

    public void startGame() {
        view.showMessage("game started");
        player1.sendMessage(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+player2.getName());
        player2.sendMessage(ProtocolMessages.ENEMYNAME+ProtocolMessages.DELIMITER+player1.getName());
    }

    public void endGame() {
        running = false;
        player1 = null;
        player2 = null;
    }
}
