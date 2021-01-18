import java.io.IOException;

public class Game {
    // Player names
    private GameClientHandler player1 = null;
    private GameClientHandler player2 = null;


    // The server TUI
    private GameServerTUI view;

    // The game server
    private GameServer server;

    public Game(GameServerTUI view, GameServer server) {
        this.view = view;
        this.server = server;
    }


    // Sets the player to which ever variable is still available.
    public void setPlayer(GameClientHandler player) {
        if (player1 == null) {
            player1 = player;
            view.showMessage("Player 1 added. Player name: " + player1.getName());
        } else if (player2 == null) {
            player2 = player;
            view.showMessage("Player 2 added. Player name: " + player2.getName());
            startGame();
        } 
    }

    public void startGame() {
        view.showMessage("game started");
        player1.sendMessage(server.enemyName(player2.getName()));
        player2.sendMessage(server.enemyName(player1.getName()));
    }

    public void endGame() {
        player1 = null;
        player2 = null;
    }
}
