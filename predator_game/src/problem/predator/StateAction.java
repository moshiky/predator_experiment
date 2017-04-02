package problem.predator;

/**
 * Created by Lev Levin on 10/11/2016.
 */
public class StateAction {
    private State state;
    private int action;

    public StateAction(State state, int action) {
        this.state = state;
        this.action = action;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof StateAction) {
            StateAction otherTransaction = (StateAction) obj;
            return otherTransaction.action == this.action &&
                    otherTransaction.state.equals(this.state);
        }

        return false;
    }

    @Override
    public int hashCode() {
        int x1 = this.state.predDistX;
        int x2 = this.state.predDistY;
        int x3 = this.state.preyDistX;
        int x4 = this.state.preyDistY;
        int x5 = action;
        int result = (x1 + 19) * 1 + (x2 + 19) * 39 + (x3 + 19) * 1521 + (x4 + 19) * 59319 + x5 * 2313441;
        return result;
    }
}
