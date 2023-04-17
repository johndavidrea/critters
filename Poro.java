import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Poro extends Critter {
	
	private int critterLifeTimer = 0;
    private static enum Threat {
    	LEFT, RIGHT, BACK, FLANKEDLEFT, FLANKEDRIGHT, FLANKEDBACK
    };
    
    private ArrayList<Threat> threats = new ArrayList<Threat>();
	
    private HashMap<String, Direction> dangerDirectionsNorth;
    private HashMap<String, Direction> dangerDirectionsEast;
    private HashMap<String, Direction> dangerDirectionsSouth;
    private HashMap<String, Direction> dangerDirectionsWest;
	
	public Poro() {
		threatDictionary();
	}
	
	public Action getMove(CritterInfo info) {
		// always increment the life timer and check for adjacent threats
		critterLifeTimer++;
		detectThreats(info);
		// always infect if an enemy is in front of us
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		}  
		// if an enemy is flanking us and about to infect, hop to evade
		if (threats.contains(Threat.FLANKEDBACK) || threats.contains(Threat.FLANKEDLEFT) || threats.contains(Threat.FLANKEDRIGHT)) {
			return evadeThreat(info);
		}
		// if an enemy is near us, but not facing toward us, we rotate to them
		if (threats.contains(Threat.BACK) || threats.contains(Threat.LEFT) || threats.contains(Threat.RIGHT)) {
			return attackThreat(info);
		}
		
		
		if (critterLifeTimer < 30) {
			return blobWest(info);
		}
		
		
		if (countAdjacentAllies(info) > 1) {
			return fortress(info);
		}
		
		if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} else {
			return Action.LEFT;
		}

	}
		
	public Color getColor() {
		return Color.MAGENTA;
	}

	public String toString() {
		return "P";
	}
	
	@SuppressWarnings("static-access")
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
	
	private void threatDictionary () {	
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
	
	private void checkForThreats (CritterInfo info, HashMap<String, Direction> dictionary) {
		
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
	
	private Action evadeThreat (CritterInfo info) {
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
	private Action attackThreat (CritterInfo info) {
		if (threats.contains(Threat.LEFT)) {
			return Action.LEFT;
		} 
		if (threats.contains(Threat.RIGHT)) {
			return Action.RIGHT;
		} 
		return Action.LEFT;
	}
	
	//public int adjacentAllies() {
	//	
	//}
	
	private Action blobWest(CritterInfo info) {
		switch (info.getDirection()) {
			case WEST : return Action.HOP;
			case NORTH : return Action.LEFT;
			case SOUTH : return Action.RIGHT;
			case EAST : return Action.RIGHT;
			default : return null;
		}	
	}
	
	private Action blobRight(CritterInfo info) {
		
		if (info.getDirection() == Direction.EAST) {
			return Action.HOP;
		}
		
		if (info.getDirection() == Direction.NORTH) {
			return Action.RIGHT;
		}
		
		if (info.getDirection() == Direction.EAST) {
			return Action.RIGHT;
		}
		
		return Action.RIGHT;
		
	}
	
	private Action fortress(CritterInfo info) {
		/*
		if (countAdjacentAllies(info) == 4) {
			return Action.RIGHT;
		}
		 
		if (info.getLeft() == Neighbor.EMPTY && info.getRight() == Neighbor.EMPTY) {
			if (Math.random() < 0.5) {
				return Action.LEFT;
			} else {
				return Action.RIGHT;
			}
		} 
		
		if (info.getLeft() == Neighbor.EMPTY) {
			return Action.LEFT;
		} 
		
		if (info.getRight() == Neighbor.EMPTY) {
			return Action.LEFT;
		}
		*/
		
		return Action.RIGHT;						
	}
	
	private int countAdjacentAllies(CritterInfo info) {
		int allyCount = 0;
		
		if(info.getFront() == Neighbor.SAME) {
			allyCount++;
		}
		if(info.getLeft() == Neighbor.SAME) {
			allyCount++;
		}
		if (info.getRight() == Neighbor.SAME) {
			allyCount++;
		}
		if (info.getBack() == Neighbor.SAME) {
			allyCount++;
		}
		return allyCount;
	}
	
}
