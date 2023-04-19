// CS 145 Lab #2
// John Rea
// The one critter to rule them all

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

public class Poro extends Critter {
	// behavior configuration variables
	private int fortressMax = 200;
	private int fortressCooldown = 10;
	
	// general variables
	private Action defaultRotation;
	private int fortressTimer;
	
    // enums for handling relative directions and threat states
	private static enum Threat 
    {LEFT, RIGHT, BACK, FLANKEDLEFT, FLANKEDRIGHT, FLANKEDBACK};
    private static enum RelativeDirection 
    {LEFT, RIGHT, BACK, FRONT};
    
    // used for tracking adjacent allies and enemies
    private ArrayList<Threat> threats = new ArrayList<Threat>();
    private ArrayList<RelativeDirection> adjacentAllies = new ArrayList<RelativeDirection>();
    
	// hashmaps used for converting relative directions to cardinal directions
    private HashMap<String, Direction> dangerDirectionsNorth;
    private HashMap<String, Direction> dangerDirectionsEast;
    private HashMap<String, Direction> dangerDirectionsSouth;
    private HashMap<String, Direction> dangerDirectionsWest;
	
    // constructor initializes our threat dictionary method and gives each critter a default rotation direction
	public Poro() {
		threatDictionary();
		if (ThreadLocalRandom.current().nextInt(0, 2) == 0) {
			defaultRotation = Action.LEFT;
		} else {
			defaultRotation = Action.RIGHT;
		}
	}
	
	// decide which move our critter should make
	public Action getMove(CritterInfo info) {
		// check for adjacent threats and allies
		detectThreats(info);
		countAdjacentAllies(info);
		
		// reset the fortress timer once the cooldown period has elapsed
		if (fortressTimer > fortressMax + fortressCooldown) {
			fortressTimer = 0;
		}
		
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
			// if enemies are near us, decrement fortressTimer to stay in fortress protocol for longer
			if (fortressTimer > 10) {
				fortressTimer = fortressTimer - 10;
			}
			// attack the adjacent enemy
			return attackThreat(info);
		}
		
		// if we have an adjacent ally, enact fortress protocol	
		if (adjacentAllies.size() > 0 ) {
			// check to make sure we're allowed to group
			// if fortressTimer is greater than fortressMax, fortress is on cooldown and we shouldn't be grouping
			if (fortressTimer <= fortressMax) {
				return fortress(info);
			} 
		}
		
		// if we're not in fortress mode, increment the fortress timer
		fortressTimer++;
		
		// otherwise, we behave like a bear
		if (info.getFront() == Neighbor.EMPTY) {
			return Action.HOP;
		} else {
			return Action.LEFT;
		}
	}
	
	// decide which color our critter should be
	public Color getColor() {
		return Color.MAGENTA; 
	}
	
	// decide which string our critter should use
	public String toString() {
		return "P";
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
	
	// check left, right, and back for enemy critters
	// add the threat type to the "threats" ArrayList	
	private void checkForThreats (CritterInfo info, HashMap<String, Direction> dictionary) {
		// handle the case where an enemy is left of us
		if (info.getLeft() == Neighbor.OTHER) {
			// if the enemy left of us is pointed at us, we are flanked and should evade
			if (info.getLeftDirection() == dictionary.get("Left")) {
				threats.add(Threat.FLANKEDLEFT);
			} 
			// otherwise we are not flanked, and we can rotate to attack them
			threats.add(Threat.LEFT);
		}
		// handle the case where an enemy is right of us
		if (info.getRight() == Neighbor.OTHER) {
			// if the enemy right of us is pointed at us, we are flanked and should evade
			if (info.getRightDirection() == dictionary.get("Right")) {
				threats.add(Threat.FLANKEDRIGHT);
			} 
			// otherwise we are not flanked, and we can rotate to attack them
			threats.add(Threat.RIGHT);
		}
		// handle the case where an enemy is behind us
		if (info.getBack() == Neighbor.OTHER) {
			// if the enemy behind us is pointed at us, we are flanked and should evade
			if (info.getBackDirection() == dictionary.get("Back")) {
				threats.add(Threat.FLANKEDBACK);
			}
			// otherwise we are not flanked, and we can rotate to attack them
			threats.add(Threat.BACK);	
		}
	}
	
	// handle the case where we are flanked, and therefore about to be infected
	private Action evadeThreat (CritterInfo info) {
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
	private Action attackThreat (CritterInfo info) {
		// if the enemy is left, rotate left
		if (threats.contains(Threat.LEFT)) {
			return Action.LEFT;
		} 
		// if the enemy is right, rotate right
		if (threats.contains(Threat.RIGHT)) {
			return Action.RIGHT;
		} 
		// if the enemy is behind us, rotate the default direction for the critter
		return defaultRotation;
	}
	
	// fortress protocol dynamically causes the critters to clump together
	private Action fortress(CritterInfo info) {
		// if the critter is surrounded by allies, rotate the default direction
		if (adjacentAllies.size() == 4) {
			return defaultRotation;
		}
		
		// if there is only one open space, stay pointing towards it
		if (adjacentAllies.size() == 3) {
			fortressTimer += 1;
			if (info.getFront() == Neighbor.EMPTY) {
				return Action.INFECT;
			} else if (info.getLeft() == Neighbor.EMPTY) {
				return Action.LEFT;
			} else if (info.getRight() == Neighbor.EMPTY) {
				return Action.RIGHT;
			} else {
				return defaultRotation;
			}
		}
		
		// if there are two adjacent allies, try to point toward empty space
		if (adjacentAllies.size() == 2) {
			fortressTimer += 2;
			if (info.getLeft() == info.getRight()) {
				return defaultRotation;
			} else {
				if (info.getLeft() == Neighbor.EMPTY) {
					return Action.LEFT;
				} else if (info.getRight() == Neighbor.EMPTY) {
					return Action.RIGHT;
				} else {
					return defaultRotation;
				}
			}
		}
		
		// otherwise, rotate the default direction for the critter
		fortressTimer += 3;
		return defaultRotation;
		
	}
	
	// populate the adjacentAllies ArrayList with the relative directions for our critter that contain an ally
	private void countAdjacentAllies(CritterInfo info) {
		// clear the ArrayList before adding new values for this cycle
		adjacentAllies.clear();
		
		// check each direction
		if (info.getFront() == Neighbor.SAME) {
			adjacentAllies.add(RelativeDirection.FRONT);
		}
		if (info.getLeft() == Neighbor.SAME) {
			adjacentAllies.add(RelativeDirection.LEFT);
		}
		if (info.getRight() == Neighbor.SAME) {
			adjacentAllies.add(RelativeDirection.RIGHT);
		}
		if (info.getBack() == Neighbor.SAME) {
			adjacentAllies.add(RelativeDirection.BACK);
		}
	}
}
