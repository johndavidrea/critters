// John Rea - CS 145
// Lab 2: Critters
// Augments the normal Bear critter with dynamic threat detection

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SuperBear extends Critter {
	// initialize our variables
    public static enum Threat {
        LEFT, RIGHT, BACK, FLANKEDLEFT, FLANKEDRIGHT, FLANKEDBACK
    };
    
	ArrayList<Threat> threats = new ArrayList<Threat>();
	
	HashMap<String, Direction> dangerDirectionsNorth;
	HashMap<String, Direction> dangerDirectionsEast;
	HashMap<String, Direction> dangerDirectionsSouth;
	HashMap<String, Direction> dangerDirectionsWest;
	
	// move our dictionary down into a method and put it in the constructor for readability
	public SuperBear() {
		threatDictionary();
	}
	
	public Action getMove(CritterInfo info) {
		// check for adjacent enemies
		detectThreats(info);
		// if we can infect, we should always do so
		if (info.getFront() == Neighbor.OTHER) {
			return Action.INFECT;
		} 
		// if we've been flanked by an enemy critter, then we should attempt to evade
		if (threats.contains(Threat.FLANKEDBACK) || threats.contains(Threat.FLANKEDLEFT) || threats.contains(Threat.FLANKEDRIGHT)) {
			return evadeThreat(info);
		}
		// if there's an enemy critter next to us that isn't facing us, we should attack them
		if (threats.contains(Threat.BACK) || threats.contains(Threat.LEFT) || threats.contains(Threat.RIGHT)) {
			return attackThreat(info);
		}		
		
		// otherwise, we're just a normal bear
		if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} else {
			return Action.LEFT;
		}
	}

	public Color getColor() {
		return Color.BLACK;
	}

	public String toString() {
		return "S";
	}
	
	// check adjacent tiles for enemy critters
	public void detectThreats(CritterInfo info) {
		// clear the list of threats from last move cycle
		threats.clear();
		// refer to one of our four HashMap dictionaries depending on which direction we're facing
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
	
	// contains the dictionary for converting relative threat directions to cardinal directions
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
	
	// check left, right, and back for enemy critters
	// add the threat type to the "threats" ArrayList
	public void checkForThreats (CritterInfo info, HashMap<String, Direction> dictionary) {
		// handle the case where an enemy is left of us
		if (info.getLeft() == Neighbor.OTHER) {
			// if the enemy left of us is pointed at us, we are flanked and should evade
			if (info.getLeftDirection() == dictionary.get("Left")) {
				threats.add(Threat.FLANKEDLEFT);
			} 
			// otherwise we are not flanked, and we can rotate to attack them
			else {
				threats.add(Threat.LEFT);
			}
		}
		// handle the case where an enemy is right of us
		if (info.getRight() == Neighbor.OTHER) {
			if (info.getRightDirection() == dictionary.get("Right")) {
				threats.add(Threat.FLANKEDRIGHT);
			} else {
				threats.add(Threat.RIGHT);
			}
		}
		// handle the case where an enemy is behind us
		if (info.getBack() == Neighbor.OTHER) {
			if (info.getBackDirection() == dictionary.get("Back")) {
				threats.add(Threat.FLANKEDBACK);
			} else {
				threats.add(Threat.BACK);
			}
		}
	}
	
	// handle the case where we are flanked, and therefore about to be infected
	public Action evadeThreat (CritterInfo info) {
		// if we can hop, do so for a 50/50 chance to avoid being infected
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
