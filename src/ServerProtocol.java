public interface ServerProtocol {

    /**
    * Returns a String to be sent as a response to a Client HELLO request,
    * which is just the ProtocolMessages.HELLO
    * @return String to be sent to client as a handshake response.
    */
   public String getHello(String playerName);
   
   /**
    * Generates a gameboard given a randomly generated fields with ships/water and a boolean indicating whether the client has to go first.
    * @param board A double String array that represents the board. Contains values from the enum fieldState that make water and ships.
    * @param isTurn A booleans representing whether it is the clients move.
    * @return GameBoard to be sent to client as their board. It already has the ships placed and indicates with 
    * a boolean whether the client receiving the board has the first move.
    */
   public GameBoard gameSetup(String[][] board, boolean isTurn);
   
   /**
    * A move that the client makes.
    * @param x The x value of the move.
    * @param y The y value of the move
    * @return whether the move was a hit or a miss.
    */
   public boolean move(int x, int y); 

   /**
    * Method to update both clients after one of them has made a move. The update
    * includes the x,y coordinates of the move, whether the move was a hit, and whether
    * the move sunk a ship and finally whether the client has the next move.
    * @param x the x value of the previous move
    * @param y the y value of the previous move
    * @param isHit indicates whether previous move was a hit on a ship
    * @param isSunk indicates whether previous move sunk a whole ship
    * @param isTurn indicates whether the client has the move now
    * @return the update to both clients about the previous move.
    */
   public String update(int x, int y, boolean isHit, boolean isSunk, boolean isTurn);

   /**
    * Method that is called when game ends. This could happen if 5 minutes pass, someone sinks all of opponent's ships
    * or one of the clients disconnects.
    * @param result integer from 0 to 2. 0: win, 1: lose, 2:tie.
    * @return the result of the game which is an int from 0 to 2 that represents whether the client won: 0, lost:1 or it is a tie:2.
    */
   public String gameOver(int result);
}
