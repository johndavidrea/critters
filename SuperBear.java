import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SuperBear extends Critter {
	
    public static enum Threat {
        LEFT, RIGHT, BACK, FLANKEDLEFT, FLANKEDRIGHT, FLANKEDBACK
    };
    
	ArrayList<Threat> threats = new ArrayList<Threat>();
	
	HashMap<String, Direction> dangerDirectionsNorth;
	HashMap<String, Direction> dangerDirectionsEast;
	HashMap<String, Direction> dangerDirectionsSouth;
	HashMap<String, Direction> dangerDirectionsWest;
	
	public SuperBear() {
		threatDictionary();
	}
	
	public Action getMove(CritterInfo info) {
		detectThreats(info);
		
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		} 
		
		if (threats.contains(Threat.FLANKEDBACK) || threats.contains(Threat.FLANKEDLEFT) || threats.contains(Threat.FLANKEDRIGHT)) {
			return evadeThreat(info);
		}
		
		if (threats.contains(Threat.BACK) || threats.contains(Threat.LEFT) || threats.contains(Threat.RIGHT)) {
			return attackThreat(info);
		}		
		
		if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} else {
			return Action.LEFT;
		}
	}

	public Color getColor() {
		return Color.DARK_GRAY;
	}

	public String toString() {
		return "S";
	}
	
	public void detectThreats(CritterInfo info) {
		threats.clear();
				
		switch (info.getDirection()) {
			case NORTH :
				checkForThreats(info, dangerDirectionsNorth);
				break;
			case EAST :
				checkForThreats(info, dangerDirectionsEast);
				break;
			case SOUTH :
				checkForThreats(info, dangerDirectionsSouth);
				break;
			case WEST :
				checkForThreats(info, dangerDirectionsWest);
				break;
		}
		
	}
	
	public void threatDictionary () {	
		dangerDirectionsNorth = new HashMap<>();
		dangerDirectionsNorth.put("Left", Direction.EAST);
		dangerDirectionsNorth.put("Right", Direction.WEST);
		dangerDirectionsNorth.put("Back", Direction.NORTH);
		
		dangerDirectionsEast = new HashMap<>();
		dangerDirectionsEast.put("Left", Direction.SOUTH);
		dangerDirectionsEast.put("Right", Direction.NORTH);
		dangerDirectionsEast.put("Back", Direction.EAST);
		
		dangerDirectionsSouth = new HashMap<>();
		dangerDirectionsSouth.put("Left", Direction.WEST);
		dangerDirectionsSouth.put("Right", Direction.EAST);
		dangerDirectionsSouth.put("Back", Direction.SOUTH);
		
		dangerDirectionsWest = new HashMap<>();
		dangerDirectionsWest.put("Left", Direction.NORTH);
		dangerDirectionsWest.put("Right", Direction.SOUTH);
		dangerDirectionsWest.put("Back", Direction.WEST);
	}
	
	public void checkForThreats (CritterInfo info, HashMap<String, Direction> dictionary) {
		
		if (info.getLeft() == Neighbor.OTHER) {
			if (info.getLeftDirection() == dictionary.get("Left")) {
				threats.add(Threat.FLANKEDLEFT);
			} else {
				threats.add(Threat.LEFT);
			}
		}
		
		if (info.getRight() == Neighbor.OTHER) {
			if (info.getRightDirection() == dictionary.get("Right")) {
				threats.add(Threat.FLANKEDRIGHT);
			} else {
				threats.add(Threat.RIGHT);
			}
		}
		
		if (info.getBack() == Neighbor.OTHER) {
			if (info.getBackDirection() == dictionary.get("Back")) {
				threats.add(Threat.FLANKEDBACK);
			} else {
				threats.add(Threat.BACK);
			}
		}
	}
	
	public Action evadeThreat (CritterInfo info) {
		// if we can hop, do so to avoid being infected
		if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} 
		// if we can't evade being infected, rotate away from allies to delay infecting them
		if (info.getFront() == Neighbor.SAME) {
			if (info.getLeft() != Neighbor.SAME) {
				return Action.LEFT;
			} 
			if (info.getRight() != Neighbor.SAME) {
				return Action.RIGHT;
			}
		}
		// if we can't do anything else, wait in place
		return Action.INFECT;
	}
	
	// rotate to attack adjacent threats
	public Action attackThreat (CritterInfo info) {
		if (threats.contains(Threat.LEFT)) {
			return Action.LEFT;
		} 
		if (threats.contains(Threat.RIGHT)) {
			return Action.RIGHT;
		} 
		return Action.LEFT;
	}
}
