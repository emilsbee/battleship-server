package protocol;

public class ProtocolMessages {
    // Delimiter used to separate arguments sent over the network.
    public static final String DELIMITER = ";";

    // These strings are used by both the server and client to communicate certain events.
    public static final String HANDSHAKE = "h";
    public static final String LATE_MOVE = "lm";
    public static final String NAME_EXISTS = "ne";
    public static final String ENEMYNAME = "n";
    public static final String CLIENTBOARD = "b";
    public static final String SETUP = "s";
    public static final String MOVE = "m";
    public static final String UPDATE = "u";
    public static final String GAMEOVER = "g";
    public static final String EXIT = "e";
}
