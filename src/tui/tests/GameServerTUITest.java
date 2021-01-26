package tui.tests;

// External imports
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

// Internal imports
import tui.GameServerTUI;

public class GameServerTUITest {
    static final String TEST_MESSAGE = "Some message";
    static final String TEST_QUESTION = "What is your favorite color?";
    static final String TEST_ANSWER = "red";
    static final String TEST_INT_ANSWER = "5";

    private static final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;

    // private static final ByteArrayInputStream inContent = new ByteArrayInputStream(GameServerTUITest.TEST_MESSAGE.getBytes());
    private static final InputStream originalIn = System.in; 
    
    static GameServerTUI view;

    @BeforeAll
    public static void setupTui() {
        view = new GameServerTUI();
        System.setOut(new PrintStream(outContent));
    }

    @Test
    void testShowMessage() {
        view.showMessage(GameServerTUITest.TEST_MESSAGE);
        assertEquals(outContent.toString(), GameServerTUITest.TEST_MESSAGE+"\n");
        outContent.reset();
    }

    @Test
    void testGetString() {
        
        System.setIn(new ByteArrayInputStream(GameServerTUITest.TEST_ANSWER.getBytes())); // Prepares the input

        String response = view.getString(GameServerTUITest.TEST_QUESTION);
        
        assertEquals(response, GameServerTUITest.TEST_ANSWER);
        outContent.reset();
    }

    @AfterAll
	static void restoreStream() {
        System.setOut(originalOut);
        System.setIn(originalIn);
	}
}
