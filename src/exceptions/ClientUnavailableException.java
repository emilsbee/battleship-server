package exceptions;

/**
 * Exception thrown when game client handler is unable to communicate with the client meaning some IO erorr has occured.
 */
public class ClientUnavailableException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * @pre msg != null
	 * @post ensures that message is passed to super class
	 */
	public ClientUnavailableException(String msg) {
		super(msg);
	}
}
