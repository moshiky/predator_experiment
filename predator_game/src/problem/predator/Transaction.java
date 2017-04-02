package problem.predator;

/**
 * Created by Lev Levin on 08/01/2017.
 */
public class Transaction {
    private State sourceState;
    private int targetStateIndex;
    private int action;

    public Transaction(State sourceState, int action, int targetStateIndex) {
        this.sourceState= sourceState;
        this.action = action;
        this.targetStateIndex = targetStateIndex;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Transaction) {
            Transaction otherTransaction = (Transaction) obj;
            return otherTransaction.action == this.action &&
                    otherTransaction.sourceState.equals(this.sourceState) &&
                    otherTransaction.targetStateIndex == this.targetStateIndex;
        }

        return false;
    }

    @Override
    public int hashCode() {
        int x1 = this.sourceState.predDistX;
        int x2 = this.sourceState.predDistY;
        int x3 = this.sourceState.preyDistX;
        int x4 = this.sourceState.preyDistY;
        int x5 = action;
        int x6 = this.targetStateIndex;
        int result = (x1 + 19) * 1 + (x2 + 19) * 39 + (x3 + 19) * 1521 + (x4 + 19) * 59319 + x5 * 2313441 + x6 * 11567205;
        return result;
    }
}
