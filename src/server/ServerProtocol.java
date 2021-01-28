package server;

/**
 * Interface that must be implemented by a class that communicates with a client.
 */
public interface ServerProtocol {

    /**
    * Handles the handshake sent in by the client. Checks whether the name included
    * is not already taken in the game, if it isn't then sets that name in the game,
    * if it is then sends back {@link #nameExists()}. 
    * @param playerName The name submitted by the client in the handshake.
    */
   public void handleHello(String playerName);
   
   /**
    * When client tries to connect but in handshake includes the same name as the opponent.
    * This methods sends back message informing the client of that.
    */
    public void nameExists();

    /**
     * Receives the encoded version of client's game board and sets it in the game. The setter method
     * in the game takes care of decoding the board.
     * @param encodedBoard The encoded game board sent in by the client.
     */
    public void clientBoard(String encodedBoard);

   /**
    * Method that sends to the client their opponents name. 
    * @param playerName The opponents name. 
    */
   public void enemyName(String playerName);

   
   /**
    * Sends out to clients the name of the player that has the first move in the game. 
    * Also starts the move timer for the player that has the first move.
    @param playerName The name of the player that goes first.
    */
   public void gameSetup(String playerName);

   /**
    * A move that the client makes. This method then make the respective move in the game
    * and also cancels the timer that was set for the move.
    * @param x The X coordinate of the move.
    * @param y The Y coordinate of the move
    */
   public void move(int x, int y); 

   /**
    * Method to update both clients after one of them has made a move. 
    * @param x the X coordinate of the move made
    * @param y the Y coordinate of the move made
    * @param isHit indicates whether move was a hit on a ship
    * @param isSunk indicates whether move sunk a whole ship
    * @param isLate indicates whether the previos move was a late move 
    * @param lastPlayerName The name of the player that made the move
    * @param nextPlayerName The name of the player that should make the next move
    */
   public void update(int x, int y, boolean isHit, boolean isSunk, boolean isLate, String lastPlayerName, String nextPlayerName);


   /**
    * Method that informs clients when game ends. 
    * This could happen if 5 minutes pass, someone sinks all of opponent's ships
    * or one of the clients disconnects.
    * @param playerName Of the winner, or empty string if it's a tie.
    * @param winType indicates what kind of end it was in the game. True: if game ended normally, false if one of the players quit.  
    */
   public void gameOver(String playerName, boolean winType);

   /**
    * When of the players wish to exit this method informs the game about it and
    * shuts down communication with the client.
    */
   public void exit();
}
