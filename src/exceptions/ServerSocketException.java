package exceptions;

/**
 * The exception is thrown when there's a problem with establishing a server socket.
 */
public class ServerSocketException extends Exception {

	private static final long serialVersionUID = 8377173653100027988L;

	public ServerSocketException(String msg) {
		super(msg);
	}

}