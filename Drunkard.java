import java.awt.*;

public class Drunkard extends Critter {

    private Color bearColor = Color.GREEN;
    
    public Drunkard() {
    }
    
    public Action getMove(CritterInfo info) {

        if (info.getFront() == Neighbor.OTHER){
            return Action.INFECT;
        }
        int die1 = (int)(Math.random() * 6.0) + 1;
        int die2 = (int)(Math.random() * 6.0) + 1;
        int sum = die1 + die2;

        if (sum < 5) {
            return Action.LEFT;
        } else if (sum > 9){
            return Action.RIGHT;
        } else {
            if (info.getFront() == Neighbor.WALL) {
                return Action.LEFT;
            } else {
                return Action.HOP;
            }
        }
    }

    public Color getColor() {
        return bearColor;
    }

    public String toString() {
        return "D";
    }
}
