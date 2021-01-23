package game;

// External imports
import java.util.Random;

// Internal imports
import client.GameClientHandler;
import gameboard.GameBoard;
import tui.GameServerTUI;

public class Game implements Runnable {
    // The id of the game
    private int gameId;

    // Player names
    private GameClientHandler player1;
    private GameClientHandler player2;

    // Player boards
    private GameBoard player1Board;
    private GameBoard player2Board;

    // Player points
    private int player1Points;
    private int player2Points;

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
        player1Points = 0;
        player2Points = 0;
    }

    /**
     * The main game loop. This is called when this game is put in a thread and the start() 
     * method is called. Although this isn't exactly a loop, a timer is set for 5 minutes after which the
     * game automatically ends and whoever has most points wins, if equal then tie. When {@link #determineMove()}
     * method is called, the thread goes to sleep and waits until one of the GameClientHandlers wakes it up which indicates
     * that either a move was made by the client or it was a late move. 
     */
    @Override
	public void run() {
        currentMove = decideWhoStart();
        player1.gameSetup(currentMove);
        player2.gameSetup(currentMove);
        

        view.showMessage("Game " + gameId + ": started");
        
        long t = System.currentTimeMillis(); 
        long end = t + 300000; // Ends in 5 minutes

        while(System.currentTimeMillis() < end) {
                                                   
            determineMove();

            try {
                synchronized (this) {
                    wait();
                }
			} catch (InterruptedException e) {}
        }
        endGame(true, null, null);
        view.showMessage("Game " + gameId + ": ended!");
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
     * Determines and informs who won and with what type of win: forfeitextd 
     * @param timeFinished Indicates whether game is over because 5 minutes are up.
     * @param quitPlayerName Indicates whether the game is over because one of the players quit.
     * @param winnerName Indicates who won in the case that all ships are destroyed in the game for one of the players.
     */
    public void endGame(boolean timeFinished, String quitPlayerName, String winnerName) {

        if (quitPlayerName != null) {

            if (player1.getName().equals(quitPlayerName)) {
                player2.gameOver(player2.getName(), false);
            } else {
                player1.gameOver(player1.getName(), false);
            }

        } else if (timeFinished) { // Game ended because the 5 minute timer ran out

            if (player1Points > player2Points) { // Player 1 wins
                player1.gameOver(player1.getName(), true);
                player2.gameOver(player1.getName(), true);                
            } else if (player1Points < player2Points) { // Player 2 wins
                player1.gameOver(player2.getName(), true);
                player2.gameOver(player2.getName(), true); 
            } else { // Tie
                player1.gameOver("", true);
                player2.gameOver("", true); 
            }

        } else { // Game finished because all ships were destroyed
            if (player1.getName().equals(winnerName)) { // If player 1 wins
                player1.gameOver(player1.getName(), true);
                player2.gameOver(player1.getName(), true);
            } else { // If player 2 wins
                player1.gameOver(player2.getName(), true);
                player2.gameOver(player2.getName(), true);
            }
        }

    }

    /**
     * Determines which player is supposed to make a move and then asks the respective 
     * player's GameClientHandler thread for the move.
     */
    public void determineMove() {
        if (player1.getName().equals(currentMove)) {
            player1.makeMove();
        } else {
            player2.makeMove();
        }
    }
  
    /**
     * Method that is called by GameClientHandler threads when their respective client has made a move
     * or didn't make a move (late move).
     * @param x X coordinate of the move. 
     * @param y Y coordinate of the move.
     * @param isLate Indicates whether the move was actually made by the client or they missed their move.
     */
    public void makeMove(int x, int y, boolean isLate) {
        String previousMove = currentMove;
        boolean[] result;
        if (player1.getName().equals(currentMove)) { // If player 1 made the move
            result = player2Board.makeMove(x, y);
            System.out.println(result[0] + " " + result[1] + " " + result[2]);
            
            if (result[2]) { // If player 1's move destroyed all ships
                player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                endGame(false, null, player1.getName());
                Thread.currentThread().interrupt();
            } else { // If player 1's move didn't destroy all ships

                // Adds the points
                if (result[0]) {
                    player1Points++;
                } else {
                    currentMove = player2.getName();
                } 

                if (result[1]) {
                    player1Points++;
                }

                player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                
            }

        } else { // If player 2 made the move
            result = player1Board.makeMove(x, y);
            System.out.println(result[0] + " " + result[1] + " " + result[2]);

            if (result[2]) { // If player 2's move destroyed all ships
                player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);  
                player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                endGame(false, null, player2.getName());
                Thread.currentThread().interrupt();
            } else { // If player 2's move didn't destroy all ships

                // Adds the points
                if (result[0]) {
                    player2Points++;
                } else {
                    currentMove = player1.getName();
                } 


                if (result[1]) {
                    player2Points++;
                }

                player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
            }

        }

        
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
     * this method is called and it sends the players their opponent's name.
     * The message is sent through the respective player's GameClientHandler {@link #sendMessage()} method.
     */
    public void sendEnemyName() {
        player1.enemyName(player2.getName());
        player2.enemyName(player1.getName());
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
    public synchronized void setBoard(String encodedBoard, String playerName) {
        if (player1.getName().equals(playerName)) {
            player1Board = new GameBoard(encodedBoard);
            if (player2Board != null && !gameStarted) {
                startGame();
            }
        } else if (player2.getName().equals(playerName)) {
            player2Board = new GameBoard(encodedBoard);
            if (player1Board != null && !gameStarted) {
                startGame();
            }
        }
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

    /**
     * To get the current game's id that is assigned to it by the server.
     * @return The id of the game.
     */
    public int getGameId() {
        return gameId;
    }
}
