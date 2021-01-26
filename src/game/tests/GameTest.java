package game.tests;


// External imports
import org.junit.jupiter.api.*;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

// Internal imports
import game.Game;
import tui.GameServerTUI;

/**
 * Tests the game initialisation. 
 */
class GameTest {
    static Game game;
    static GameServerTUI view;
    private static final int GAME_ID = 1;

    @BeforeAll
    public static void setUpGame() {
        view = new GameServerTUI();
        game = new Game(view, GameTest.GAME_ID);
    }   

    @Test
    void testGameInitialisation() {
        assertTrue(game.getPlayer1Points() == 0);
        assertTrue(game.getPlayer2Points() == 0);
        assertFalse(game.getGameStarted());
        assertTrue(game.getGameId() == GameTest.GAME_ID);
    }


}
