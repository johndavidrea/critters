import java.awt.*;

public class Bear extends Critter {
	private boolean alternateSlash = false;
	private Color bearColor;
	
	public Bear(boolean polar) {
		if(polar) {
			bearColor = Color.WHITE;
		} else {
			bearColor = Color.BLACK;
		}
	}
	
	public Action getMove(CritterInfo info) {
		alternateSlash = !alternateSlash;		
		
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		} else if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} else {
			return Action.LEFT;
		}
	}

	public Color getColor() {
		return bearColor;
	}

	public String toString() {
		if(alternateSlash) {
			return "/";
		} else {
			return "\\";
		}
	}
}
