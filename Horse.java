import java.awt.*;

public class Horse extends Critter {

    private Color bearColor = Color.PINK;
    private int forwardCount = 0;
    private boolean turnedRight = false;
    
    public Horse() {
    }
    
    public Action getMove(CritterInfo info) {
        if (forwardCount < 2) {
            if (info.getFront() == Neighbor.EMPTY){
                forwardCount++;
                return Action.HOP;
            } else if (info.getFront() == Neighbor.SAME || info.getFront() == Neighbor.WALL) {
                return Action.LEFT;
            } else {
                return Action.INFECT;
            }
        } else {
            if (info.getFront() == Neighbor.OTHER){
                return Action.INFECT;
            } else {
                if (!turnedRight){
                    turnedRight = true;
                    return Action.RIGHT;
                } else {
                    if (info.getFront() == Neighbor.WALL) {
                        return Action.RIGHT;
                    } else {
                        forwardCount = 0;
                        turnedRight = false;
                        return Action.HOP;
                    }
                }
            }
        }
    }

    public Color getColor() {
        return bearColor;
    }

    public String toString() {
        return "H";
    }
}