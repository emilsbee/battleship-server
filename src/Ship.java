// External imports
import java.io.Serializable;
import java.awt.Point;

public class Ship implements Serializable {
	private static final long serialVersionUID = -2453862416427037800L;

	// First array dimension: X
    // Second dimension: Y    
    private Point[] position;

    // The lenght of ship
    private int length;

    public Ship(Point[] position) {
        this.position = position;
        this.length = position.length;
    }

    public Point[] getPositon() {
        return this.position;
    }

    public int getLength() {
        return this.length;
    }
}