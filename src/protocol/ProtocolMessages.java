package protocol;

public class ProtocolMessages {
    /**
	 * Delimiter used to separate arguments sent over the network.
	 */
    public static final String DELIMITER = ";";

    /**
	 * Sent as last line in a multi-line response to indicate the end of the text.
	 */
    public static final String EOT = "--EOT--";

    /** Used for the server-client handshake */
    public static final String HANDSHAKE = "h";
    
    /**
	 * The following chars are both used by the TUI to receive user input, and the
	 * server and client to distinguish messages.
	 */
    public static final String NAME_EXISTS = "ne";
    public static final String ENEMYNAME = "n";
    public static final String CLIENTBOARD = "b";
    public static final String SETUP = "s";
    public static final String MOVE = "m";
    public static final String UPDATE = "u";
    public static final String GAMEOVER = "g";
    public static final String EXIT = "e";

    enum fieldState {
        WATER,
        SHIP
    }
}
