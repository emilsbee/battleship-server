public class Game {
    // Player names
    GameClientHandler player1;
    GameClientHandler player2;

    // The server TUI
    GameServerTUI view;

    // The game server
    GameServer server;

    public Game(GameServerTUI view, GameServer server) {
        this.view = view;
        this.server = server;
    }

    private void start() {
        view.showMessage("Game started, waiting for players");

    }

    // Sets the player to which ever variable is still available.
    public void setPlayer(GameClientHandler player) {
        if (player1 == null) {
            player1 = player;
            view.showMessage("Player 1 added. Player name: " + player1.getName());
        } else if (player2 == null) {
            player2 = player;
            view.showMessage("Player 2 added. Player name: " + player2.getName());
        } 
    }
}
