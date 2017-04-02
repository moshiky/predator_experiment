package problem.predator;

/**
 * Created by Lev Levin on 15/07/2016.
 */
public class State {
    //Distance from other problem.predator on x-axis
    public int predDistX;
    //Distance from other problem.predator on y-axis
    public int predDistY;
    //Distance from prey on x-axis
    public int preyDistX;
    //Distance from prey on x-axis
    public int preyDistY;

    public State() {
    }

    public State (int predDistX, int predDistY, int preyDistX, int preyDistY) {
        this.predDistX = predDistX;
        this.predDistY = predDistY;
        this.preyDistX = preyDistX;
        this.preyDistY = preyDistY;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof State) {
            State otherState = (State)obj;
            return otherState.preyDistX == this.preyDistX &&
                    otherState.preyDistY == this.preyDistY &&
                    otherState.predDistX == this.predDistX &&
                    otherState.predDistY == this.predDistY;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int x1 = predDistX;
        int x2 = predDistY;
        int x3 = preyDistX;
        int x4 = preyDistY;

        return (x1 + 19) * 1 + (x2 + 19) * 39 + (x3 + 19) * 1521 + (x4 + 19) * 59319;
    }
}
