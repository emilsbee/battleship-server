import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class GameClientHandler implements Runnable {
    // The socket and In- and OutputStreams 
	private BufferedReader in;
	private BufferedWriter out;
    private Socket socket;
    
    // The connected game server
    GameServer server;

    // Player's name
    private String name;


    /**
	 * Constructs a new GameClientHandler. Opens the In- and OutputStreams.
	 * 
	 * @param socket The client socket
	 * @param server  The connected server
	 * @param name The name of this GameClientHandler player
	 */
    public GameClientHandler(Socket socket, GameServer server) {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.socket = socket;
            this.server = server;
        } catch (IOException e) {
            shutdown();
        }
    }

    /**
	 * Continuously listens to client input and forwards the input to the
	 * {@link #handleCommand(String)} method.
	 */
	@Override
	public void run() {
        String input;
        
		try {
            input = in.readLine();
            while (input != null) {
                System.out.println("> " + name + " Incoming: " + input);
                handleCommand(input);
                out.newLine();
                out.flush();
                input = in.readLine();
            }
        } catch (IOException e) {
            shutdown();
        }
	}

    private void handleCommand(String input) throws IOException {
        if (input.split(";")[0].equals(ProtocolMessages.HANDSHAKE) && input.split(";").length >= 2) { // Handshake
            String playerName = input.split(";")[1];
            this.name = playerName;
            out.write(server.getHello(playerName));
		} else {
            out.write("ERROR: Name of the player must be included!");

        }
    }

    public String getName() {
        return this.name;
    }

    /**
	 * Shut down the connection to this client by closing the socket and 
	 * the In- and OutputStreams.
	 */
	private void shutdown() {
		System.out.println("> " + name + " Shutting down.");
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.removeClient(this);
	}
}
