package protocol;

public interface ServerProtocol {

    /**
    * Returns a String to be sent as a response to a Client HELLO request,
    * which is just the ProtocolMessages.HELLO
    * @return String to be sent to client as a handshake response.
    */
   public String getHello(String playerName);
   
   /**
    * When client tries to connect but in handshake includes the same name as the opponent.
    * @return The formatted message to send back to client. 
    */
    public String nameExists();

   /**
    * Method that sends to player their opponents name. The message construct:
    * ProtocolMessages.ENEMYNAME + ProtocolMessages.DELIMITER + playerName
    * @param playerName the name of the player requesting the enemies name 
    * @return the enemy name to send to the other player. 
    */
   public String enemyName(String playerName);

   
   /**
    * Sends out to clients who begins the game.
    * The message construct: ProtocolMessages.SETUP + ProtocolMessages.DELIMITER + playerName
    * @return the name of player that goes first.
    */
   public String gameSetup(String playerName);

   /**
    * A move that the client makes.
    * The construct of message received: ProtocolMessages.MOVE + ProtocolMessages.DELIMITER + xCoordinate + ProtocolMessages.DELIMITER + yCoordinate
    * @param x The x value of the move.
    * @param y The y value of the move
    * @return whether the move was a hit or a miss.
    */
   public boolean move(int x, int y); 

   /**
    * Method to update both clients after one of them has made a move. The update
    * includes the x,y coordinates of the move, whether the move was a hit, and whether
    * the move sunk a ship and finally which  client has the next move.
    * Message construct: ProtocolMessages.UPDATE + ProtocolMessages.DELIMITER + xCoordinate +
    * ProtocolMessages.DELIMITER + yCoordinate + ProtocolMessages.DELIMITER + isHit + ProtocolMessages.DELIMITER +
    * isSunk + ProtocolMessages.DELIMITER + playerName
    * @param x the x value of the previous move
    * @param y the y value of the previous move
    * @param isHit indicates whether previous move was a hit on a ship
    * @param isSunk indicates whether previous move sunk a whole ship
    * @param isTurn indicates whether the client has the move now
    * @return the update to both clients about the previous move.
    */
   public void update(int x, int y, boolean isHit, boolean isSunk, boolean isLate, String lastPlayerName, String nextPlayerName);

   public void lateMove();

   /**
    * Method that informs clients when game ends. This could happen if 5 minutes pass, someone sinks all of opponent's ships
    * or one of the clients disconnects.
    * The message construct: ProtocolMessages.GAMEOVER + ProtocolMessages.DELIMITER + result 
    * @param result integer from 0 to 2. 0: win, 1: lose, 2:tie.
    * @return the result of the game which is an int from 0 to 2 that represents whether the client won: 0, lost:1 or it is a tie:2.
    */
   public String gameOver(int result);

   /**
    * When one of the players indicates exit, the whole game is stopped and both players disconnected.
    * Received message construct: ProtocolMessages.EXIT
    */
   public void exit();
}
