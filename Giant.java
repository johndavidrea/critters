import java.awt.*;

public class Giant extends Critter {
	
	private int moveCounter = 1;
	
	public Giant() {}
	
	public Action getMove(CritterInfo info) {
		moveCounter++;
		
		if (moveCounter >= 25) {
			moveCounter = 1;
		}
		
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		} else if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} else {
			return Action.RIGHT;
		} 
	}

	public Color getColor() {
		return Color.GRAY;
	}

	public String toString() {
		if (moveCounter <= 6) {
			return "fee";
		} else if (moveCounter <= 12) {
			return "fie";
		} if (moveCounter <= 18) {
			return "foe";
		} if (moveCounter <= 24) {
			return "fum";
		} else {
			return "ERROR";
		}
	}
}
