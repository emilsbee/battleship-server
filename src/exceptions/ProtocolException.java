package exceptions;

/**
 * The exception is thrown when there's a problem with one of the protocol messages
 * received from a client.
 */
public class ProtocolException extends Exception {

	private static final long serialVersionUID = 4814836969744019085L;

	public ProtocolException(String msg) {
		super(msg);
	}

}