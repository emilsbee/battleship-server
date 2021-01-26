package game;

// External imports
import java.util.Random;
import java.util.concurrent.TimeUnit;

// Internal imports
import client.GameClientHandler;
import gameboard.GameBoard;
import tui.GameServerTUI;

/**
 * This class represents an instance of a game. Each game runs on a separate thread, or more precisely the 5 minute game
 * loop runs on the thread. This class keeps track of player moves and updates them on the respective game boards. It also keeps track
 * of player points. This class mainly communicates with game client handle threads to inform clients about what's going on in the game and
 * receive moves from them.
 */
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

    // Indicator whether the game has started. True if game is going on and false when it ends and before it starts.
    private boolean gameStarted;

    // Re-usable random
    Random random;

    // Indicates which players move it is. The player's names are used for this indicator.
    private String currentMove;

    // Indicates which player made the previous move before current move. Also uses players names.
    private String previousMove;

    // The thread on which 5 minute game loop runs
    private Thread gameThread;

    /**
     * Constructor that initialises this game's id, the terminal view, and sets the game started to false
     * since the game only starts after both players have sent in their boards. Also initialises both player's points to 0.
     * @param view The server's TUI.
     * @param gameId The id of this game given by the server.
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
     * The game loop thread. A while loop is set to end in 5 minutes or when the thread is interrupted because
     * the game ended due to some other reason. The loop sleeps for one second every loop to  
     * to be more efficient for the processor since it doesn't really
     * have to be accurate to milliseconds. It is also decided in this thread which player goes first 
     * and that's randomly found by the {@link #decideWhoStart()} methods. Then both players are informed 
     * about this with the game setup message. 
     */
    @Override
	public void run() {
        // Randomly chooses which players goes first and informs them about that
        currentMove = decideWhoStart();
        player1.gameSetup(currentMove);
        player2.gameSetup(currentMove);
        
        view.showMessage("Game " + gameId + ": started");
        
        long t = System.currentTimeMillis(); 
        long end = t + 300000; // Ends in 5 minutes

        // The game loop
        while(System.currentTimeMillis() < end && !gameThread.isInterrupted()) {
            try {
                  TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
                gameThread.interrupt();
                break;
            }
        }

        if (!gameThread.isInterrupted()) { // If the game ended because timer ran out 
            endGame(true, null, null);
        }

        view.showMessage("Game " + gameId + ": ended!");
    }


    /**
     * Method to start the game by creating a separate thread for it. This is only called
     * after both players have submitted their boards.
     */
    public void startGame() {
        gameStarted = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Determines and informs who won and with what type of win: forfeit, time finished or winning by destroying all ships.
     * @param timeFinished Indicates whether game is over because 5 minutes are up.
     * @param quitPlayerName Indicates whether the game is over because one of the players quit.
     * @param winnerName Indicates who won in the case that all ships are destroyed in the game for one of the players.
     */
    public void endGame(boolean timeFinished, String quitPlayerName, String winnerName) {

        if (gameStarted) { // If game has not yet ended. Prevents from being called after game has already once ended.

            if (quitPlayerName != null) { // If one of the players has quit the game
                gameThread.interrupt(); // Stops the 5 minute game loop from continuing. 

                if (player1.getName().equals(quitPlayerName)) { // If player 1 quit
                    
                    player2.gameOver(player2.getName(), false);

                } else { // If player 2 quit

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
                gameThread.interrupt(); // Stops the 5 minute game loop from continuing.
                
                if (player1.getName().equals(winnerName)) { // If player 1 wins
    
                    player1.gameOver(player1.getName(), true);
                    player2.gameOver(player1.getName(), true);

                } else { // If player 2 wins
    
                    player1.gameOver(player2.getName(), true);
                    player2.gameOver(player2.getName(), true);
                }
            }
        }
        gameStarted = false;
    }

  
    /**
     * Method that is called by GameClientHandler threads when their respective client has made a move
     * or didn't make a move (late move). This method then updates both player's board and then sends those updates to both players.
     * It also changes the currentMove and previousMove variables as well as calls the makeMove() method for the respective client thread
     * to start the move timer. And also it adds points for hit and sunk ships. This method is synchronized because it could potentially be called 
     * by both client threads at once.
     * @param x X coordinate of the move. 
     * @param y Y coordinate of the move.
     * @param isLate Indicates whether the move was actually made by the client or the timer sent it due to late move.
     */
    public synchronized void makeMove(int x, int y, boolean isLate) {
        if (gameStarted) { // If game is actually going on. Prevents from making moves before game and after it has ended.
            
            // The array for information about the move that will be received from on of the players boards.
            // It includes result[0]: isHit (whether a ship was hit), result[1]: isSunk (whether a ships was sunk)
            // and result[2]: areAllShipsDestryoed which indicates that one of the players has won.
            boolean[] result; 
            
            if (currentMove.equals(player1.getName())) { // If player 1 made the move
                
                if (isLate) { // If player 1 made a late move

                    currentMove = player2.getName();
                    previousMove = player1.getName();
                    player1.update(x, y, false, false, isLate, previousMove, currentMove);
                    player2.update(x, y, false, false, isLate, previousMove, currentMove);
                    player2.makeMove();
                
                } else { // If player 1 made a move on time

                    result = player2Board.makeMove(x, y); // Update the player 2 board and receive the results from that move.
                    
                    if (result[2]) { // If player 1's move destroyed all ships

                        player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                        player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                        endGame(false, null, player1.getName());

                    } else { // If player 1's move didn't destroy all ships
        
                        if (result[0]) { // If player 1 move hit a shit
                            
                            player1Points++;

                            if (result[1]) { // If ship was sunk
                                player1Points++;
                            }

                            currentMove = player1.getName(); 
                            previousMove = player1.getName();
                            player1.makeMove();

                        } else { // If no ships were hit
                            
                            currentMove = player2.getName();
                            previousMove = player1.getName();
                            player2.makeMove();

                        } 
        
                        player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                        player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                        
                    }
                
                }
 
    
            } else { // If player 2 made the move

                if (isLate) { // If player 2 made a late move

                    currentMove = player1.getName();
                    previousMove = player2.getName();
                    player1.update(x, y, false, false, isLate, previousMove, currentMove);
                    player2.update(x, y, false, false, isLate, previousMove, currentMove);
                    player1.makeMove();

                } else { // If player 2 made a move on time

                    result = player1Board.makeMove(x, y); // Update the player 1 board and receive the results from that move.
        
                    if (result[2]) { // If player 2's move destroyed all ships
                        
                        player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);  
                        player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                        endGame(false, null, player2.getName());
                        
                    } else { // If player 2's move didn't destroy all ships
        
                        if (result[0]) { // If ship was hit
                            
                            player2Points++;

                            if (result[1]) { // If ship was sunk
                                player2Points++;
                            }

                            currentMove = player2.getName(); 
                            previousMove = player2.getName();
                            player2.makeMove();

                        } else { // If no ship was hit

                            currentMove = player1.getName();
                            previousMove = player2.getName();
                            player1.makeMove();

                        } 
        
                        player1.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                        player2.update(x, y, result[0], result[1], isLate, previousMove, currentMove);
                    }
                
                }
 
    
            }
        }

        
    }

    /**
     * After a succesful handshake with the client whereby a uniqe name is gotten
     * this method is called by the GameClientHandler thread to add the client to the game. 
     * Starting from player1. So player1 will always be the first connected, then player 2.
     * @param player The player instance to be added to the game.
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
     * This method is synchronized because it could potentially be called by boht threads at once.
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
     * Getter to get the current game's id that is assigned to it by the server.
     * @return The id of the game.
     */
    public int getGameId() {
        return gameId;
    }

    /**
     * Getter to check whether the game has ended.
     * @return Whether the game has ended.
     */
    public boolean getGameEnded() {
        return this.gameStarted;
    }

    /**
     * Getter for who has the current move in the game.
     * @return The player name who has the current move.
     */
    public String getCurrentMove() {
        return currentMove;
    }
}
