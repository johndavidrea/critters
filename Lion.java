import java.awt.*;
import java.util.concurrent.ThreadLocalRandom;

public class Lion extends Critter {
	// variables used for tracking color changes
	private int moveCounter = 0;
	private int randomColor = ThreadLocalRandom.current().nextInt(0, 3);
	
	public Lion() {}
	
	public Action getMove(CritterInfo info) {
		// randomly roll a new color every three moves
		moveCounter++;		
		if (moveCounter >= 4) {
			randomColor = ThreadLocalRandom.current().nextInt(0, 3);
			moveCounter = 1;
		}
		
		// always infect if an enemy is in front
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		} 
		// otherwise if a wall is in front or to the right, then turn left
		else if (info.getFront() == Neighbor.WALL || info.getRight() == Neighbor.WALL) {
			return Action.LEFT;
		} 
		// otherwise if a fellow Lion is in front, then turn right
		else if (info.getFront() == Neighbor.SAME) {
			return Action.RIGHT;
		} 
		// otherwise hop
		else {
			return Action.HOP;
		}
	}

	public Color getColor() {
		// pick one of three colors based on the 1-3 range of our random integer
		if (randomColor == 0) {
			return Color.RED;
		} else if (randomColor == 1) {
			return Color.GREEN;
		} else {
			return Color.BLUE;
		}
	}

	public String toString() {
		return "L";
	}
}
