import java.io.Serializable;

public class GameBoard implements Serializable{
    private static final long serialVersionUID = 3732806577326531959L;
	private String[][] board;
    private boolean isTurn;

    public GameBoard(String[][] board, boolean isTurn) {
        this.board = board;
        this.isTurn = isTurn;
    }

    public String[][] getBoard() {
        return board;
    }

    public boolean getIsTurn() {
        return isTurn;
    }

    public void setBoard(String[][] board) {
        this.board = board;
    }

    public void setIsTurn(boolean isTurn) {
        this.isTurn = isTurn;
    }
}
