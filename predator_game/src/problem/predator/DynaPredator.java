package problem.predator;

import org.apache.commons.math3.util.Pair;
import problem.RNG;
import problem.learning.ShortHashTable;
import problem.learning.SimilarityType;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Lev Levin on 05/11/2016.
 */
public class DynaPredator extends QTablePredator{
    private static ShortHashTable<Transaction> transactionsModel;
    private int kFactor = 50;

    static{
        transactionsModel = new ShortHashTable<Transaction>(300747330); // 39 * 39 * 39 * 39 * 5 * 26 = 300747330
    }

    public DynaPredator(PredatorWorld pw, int size, int x, int y, SimilarityType similarityType) {
        super(pw, size, x, y, similarityType);
        transactionsModel.reset();
    }

    @Override
    public void reward(double reward) {
        updateModel(reward);

        //Q(S,A) = R(S,A) + gamma * SUM_S'(T(S,A,S') * max_A' Q(S',A'))
        State currentState = getCurrentState();
        double newValue = updateStateAction(prevState, previousAction, (int)reward);

        double best = -Double.MAX_VALUE;
        ArrayList<Integer> bestActions = new ArrayList<>();

        // Calculates max_A' Q(S',A')
        for (int i = 0; i < p_problemWorld.getNumActions(); i++) {
            double value = qTable.get(new StateAction(prevState, i));
            if (value >= best) {
                if (value > best) {
                    bestActions.clear();
                }

                best = value;
                bestActions.add(i);
            }
        }

        updateQBySimilarity(prevState, previousAction, newValue, similarityType);
        updateKRandomPairs();

        //action selection
        selectBestAction(bestActions, epsilon);
    }

    @Override
    protected void updateQtableValue(double value, int action, State state, double factor){
        StateAction currentStateAction = new StateAction(state, action);
        double oldValue = qTable.get(currentStateAction);
        double newValue;

        newValue = oldValue + factor * (value - oldValue);
        qTable.put(currentStateAction, newValue);
    }

    private void updateModel(double reward) {
        // Update Transaction Model
        int targetStateIndex = getTargetStateIndex(this.prevState, this.previousAction, this.getCurrentState());
        Transaction transaction = new Transaction(this.prevState, this.previousAction, targetStateIndex);
        Transaction totalTransaction = new Transaction(this.prevState, this.previousAction, 25);

        // Update actual target state seen
        short newValue = (short)(transactionsModel.get(transaction) + 1);
        transactionsModel.put(transaction, newValue);

        // Update total times stateAction seen
        newValue = (short)(transactionsModel.get(totalTransaction) + 1);
        transactionsModel.put(totalTransaction, newValue);
    }

    private int getTargetStateIndex(State source, int action, State targetState){
        int expectedPredX = source.predDistX;
        int expectedPredY = source.predDistY;
        int expectedPreyX = source.preyDistX;
        int expectedPreyY = source.preyDistY;
        int preyAction = 4; // None
        int predAction = 4; // None

        switch (action) {
            case 0:
                expectedPredY--;
                expectedPreyY--;
            case 1:
                expectedPredY++;
                expectedPreyY++;
            case 2:
                expectedPredX--;
                expectedPreyX--;
            case 3:
                expectedPredX++;
                expectedPreyX++;
        }

        if (targetState.predDistX != expectedPredX){
            predAction = targetState.predDistX > expectedPredX ? 2 : 3; // LEFT = 2, RIGHT = 3
        }
        if (targetState.predDistY != expectedPredY){
            predAction = targetState.predDistY > expectedPredY ? 0 : 1; // UP = 0, DOWN = 1
        }
        if (targetState.preyDistX != expectedPreyX){
            preyAction = targetState.preyDistX > expectedPreyX ? 2 : 3; // LEFT = 2, RIGHT = 3
        }
        if (targetState.preyDistY != expectedPreyY){
            preyAction = targetState.preyDistY > expectedPreyY ? 0 : 1; // UP = 0, DOWN = 1
        }

        return predAction + preyAction * 5; // Returns one out of 25 possible target states index (result in range 0 - 24)
    }

    private State getTargetStateFromIndex(int index, State sourceState, int action) {
        State result;
        int predAction = index % 5;
        int preyAction = index / 5;

        int targetPredDistX = sourceState.predDistX;
        int targetPredDistY = sourceState.predDistY;
        int targetPreyDistX = sourceState.preyDistX;
        int targetPreyDistY = sourceState.preyDistY;

        switch (action) {
            case 0: // UP
                targetPredDistY--;
                targetPreyDistY--;
            case 1: // DOWN
                targetPredDistY++;
                targetPreyDistY++;
            case 2: //LEFT
                targetPredDistX--;
                targetPreyDistX--;
            case 3: // RIGHT
                targetPredDistX++;
                targetPreyDistX++;
        }

        if (predAction == 2 || predAction == 3){
            targetPredDistX += predAction == 2 ? 1 : -1; // LEFT = 2, RIGHT = 3
        }
        if (predAction == 0 || predAction == 1){
            targetPredDistY += predAction == 0 ? 1 : -1; // UP = 0, DOWN = 1
        }
        if (preyAction == 2 || preyAction == 3){
            targetPreyDistX += preyAction == 2 ? 1 : -1; // LEFT = 2, RIGHT = 3
        }
        if (preyAction == 0 || preyAction == 1){
            targetPreyDistY += preyAction == 0 ? 1 : -1; // UP = 0, DOWN = 1
        }

        targetPredDistX = Math.max(-19, targetPredDistX);
        targetPredDistY = Math.max(-19, targetPredDistY);
        targetPreyDistX = Math.max(-19, targetPreyDistX);
        targetPreyDistY = Math.max(-19, targetPreyDistY);
        targetPredDistX = Math.min(19, targetPredDistX);
        targetPredDistY = Math.min(19, targetPredDistY);
        targetPreyDistX = Math.min(19, targetPreyDistX);
        targetPreyDistY = Math.min(19, targetPreyDistY);
        result = new State(targetPredDistX, targetPredDistY, targetPreyDistX, targetPreyDistY);
        return result;
    }

    private double updateStateAction(State sourceState, int action, int reward) {
        HashMap<Integer, State> possibleStates = new HashMap();
        for (int i = 0; i < 25; i++) {
            if (transactionsModel.get(new Transaction(sourceState, action, i)) > 0) {
                State targetState = getTargetStateFromIndex(i, sourceState, action);
                possibleStates.put(i, targetState);
            }
        }

        double sum = 0;
        for (int index : possibleStates.keySet()) {
            double best = -Double.MAX_VALUE;
            ArrayList<Integer> bestActions = new ArrayList<>();

            // Calculates max_A' Q(S',A')
            for (int i = 0; i < p_problemWorld.getNumActions(); i++) {

                double value = qTable.get(new StateAction(possibleStates.get(index), i));
                if (value >= best) {
                    if (value > best) {
                        bestActions.clear();
                    }

                    best = value;
                    bestActions.add(i);
                }
            }

            // if first times visited S' than best = 0
            if (bestActions.size() == 0) {
                best = 0;
            }

            // Calculates T(S,A,S')
            int transitionVisitsCount = transactionsModel.get(new Transaction(sourceState, action, index));
            int totalStateActionVisits= transactionsModel.get(new Transaction(sourceState, action, 25));
            double transactionProbability = (double)transitionVisitsCount / (double)totalStateActionVisits;
            // Calculates T(S,A,S') * max_A' Q(S',A')
            double partialUpdate = transactionProbability * best;
            // Calculates gamma * SUM_S'(T(S,A,S') * max_A' Q(S',A'))
            sum += gamma * partialUpdate;
        }

        double newValue = reward + sum;

        qTable.put(new StateAction(sourceState, action), newValue);
        return newValue;
    }

    private void updateKRandomPairs() {
        int totalQTableSize = 0;
        for (int i = 0; i < kFactor; i++) {
            //System.out.println("KUpdate : " + i);
            Pair<State, Integer> pair = ChooseRandomStateAction();

            if (pair != null) {
                updateStateAction(pair.getFirst(), pair.getSecond(), 0);
            }
        }
    }

    private Pair<State, Integer> ChooseRandomStateAction() {
        int randomAction = RNG.randomInt(p_problemWorld.getNumActions());
        State randomState = new State();
        randomState.predDistX = (byte)RNG.randomInt(-19, 19);
        randomState.predDistY = (byte)RNG.randomInt(-19, 19);
        randomState.preyDistX = (byte)RNG.randomInt(-19, 19);
        randomState.preyDistY = (byte)RNG.randomInt(-19, 19);
        return new Pair<State, Integer>(randomState, randomAction);
    }
}
