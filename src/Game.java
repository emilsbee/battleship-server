public class Game {
    String player1Name;
    String player2Name;
    GameServerTUI view;

    public Game(String player1Name, String player2Name, GameServerTUI view) {
        this.player1Name = player1Name;
        this.player2Name = player2Name;
        this.view = view;
        start();
    }

    private void start() {
        view.showMessage("Game started with players: " + player1Name +" "+ player2Name);
    }
}
