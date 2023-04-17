import java.awt.*;

public class GoldRush extends Critter {
	
	public Action getMove(CritterInfo info) {
		
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		} 		
		
		if (info.getDirection() == Direction.WEST) {
			return Action.HOP;
		} else if (info.getDirection() == Direction.NORTH) {
			return Action.LEFT;
		} else if (info.getDirection() == Direction.EAST) {
			return Action.LEFT;
		} else if (info.getDirection() == Direction.SOUTH) {
			return Action.RIGHT;
		}
		
		return Action.INFECT;
		
	}
		
	public Color getColor() {
		return Color.YELLOW;
	}

	public String toString() {
		return "G";
	}
}
