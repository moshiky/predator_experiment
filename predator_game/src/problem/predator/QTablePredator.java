package problem.predator;

import problem.RNG;
import problem.learning.DoubleHashTable;
import problem.learning.SimilarityType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import static java.lang.Math.sqrt;

/**
 * Created by Lev Levin on 15/07/2016.
 */
public class QTablePredator extends TCPredator {

    protected State prevState;
    protected SimilarityType similarityType;
    // the factor of the similarity algorithm - problem.learning factor on the similar <states, action> pair
    private double transactionSimilartyFactor;
    private long updatesNumber;

    static {
        // Initializing the QTable
        qTable = new DoubleHashTable<StateAction>(11567205);
    }

    public QTablePredator(PredatorWorld pw, int size, int x, int y, SimilarityType similarityType) {
        super(pw, size, x, y);
        qTable.reset();
        epsilon = 0.01;

        transactionSimilartyFactor = 0.9;
        this.similarityType = similarityType;
        updatesNumber = 0;
    }

    @Override
    public int act() {
        prevState = getCurrentState();
        return prevAction;
    }

    @Override
    public void reward(double reward) {
        //Q(S,A) = Q(S,A) + alfa * (reward + gamma * max_A' Q(S',A') - Q(S,A))
        State currentState = getCurrentState();
        double best = -Double.MAX_VALUE;
        ArrayList<Integer> bestActions = new ArrayList<>();

        // Calculates max_A' Q(S',A')
        for(int i=0; i<prob.getNumActions(); i++){
            double value = qTable.get(new StateAction(currentState, i));
            if(value >= best) {
                if (value > best) {
                    bestActions.clear();
                }

                best = value;
                bestActions.add(i);
            }
        }

        // if first times visited S' than best = 0
        if(bestActions.size() == 0) {
            best = 0;
        }

        StateAction currentStateAction = new StateAction(prevState, prevAction);
        double oldQValue = qTable.get(currentStateAction);
        double error = (reward + gamma * best - oldQValue);
        double newValue = oldQValue + alpha * error;

        qTable.put(currentStateAction, newValue);
        updatesNumber++;
        updateQBySimilarity(prevState, prevAction, error, similarityType);

        //action selection
        selectBestAction(bestActions, epsilon);
    }

    @Override
    public void selectAction(){
        State currentState = getCurrentState();
        double best = -Double.MAX_VALUE;
        ArrayList<Integer> bestActions = new ArrayList<>();

        for(int i=0; i<prob.getNumActions(); i++){
            double value = qTable.get(new StateAction(currentState, i));
            if(value >= best) {
                if (value > best) {
                    bestActions.clear();
                }

                best = value;
                bestActions.add(i);
            }
        }

        selectBestAction(bestActions, epsilon);
    }

    @Override
    public long getUpdatesNumber() {
        return updatesNumber;
    }

    // Current state is a 4 bytes vector
    // the state consists of the distance from the other problem.predator on x and y axis
    // and the distance from the prey on x and y axis
    public State getCurrentState() {
        Animal[] predators = pw.getPredators();
        Animal[] preys = pw.getPreys();
        byte[] state = new byte[4];
        for(int i=0; i<predators.length; i++){
            if(predators[i] != this){
                state[0] = (byte)(x-predators[i].x);
                state[1] = (byte)(y-predators[i].y);
            }
        }

        state[2] = (byte)(x-preys[0].x);
        state[3] = (byte)(y-preys[0].y);

        return new State((byte)state[0], state[1], state[2], state[3]);
    }

    protected void selectBestAction(ArrayList<Integer> bestActions, double epsilon) {
        //action selection
        int action = 0;

        //greedy
        if (RNG.randomDouble() > epsilon && bestActions.size() > 0) {
            action = bestActions.get(RNG.randomInt(bestActions.size()));
            //random
        } else {
            action = RNG.randomInt(prob.getNumActions());
        }

        prevAction = action;
    }

    @Override
    public void updateQBySimilarity(State state, int action, double error, SimilarityType similarityType) {
        switch (similarityType) {
            case Symmetry:
                symmetryUpdate(state, action, error);
                break;
            case Transaction:
                transactionUpdate(state, action, error);
                break;
            case AllCombined:
                symmetryUpdate(state, action, error);
                transactionUpdate(state, action, error);
                break;
        }

    }

    //Update fuction for transactional similarity
    protected void transactionUpdate(State state, int action, double error) {
        // update all neighbors of the target cell (the other 3 neighbors, with the similar action which brings the problem.predator to the same state)
        Map<Integer, State> similarTransactions = new Hashtable<>();
        SimilarityFunctions.getSimilarTransactions(state, action, ((PredatorWorld) prob).getSize(), similarTransactions);

        for (Map.Entry<Integer, State> entry : similarTransactions.entrySet()) {
            updateQtableValue(error, entry.getKey(), entry.getValue(), transactionSimilartyFactor);
            updatesNumber++;
        }
    }

    //Update fuction for symmetry similarity
    protected void symmetryUpdate(State state, int action, double error) {
        xAxisMirrorUpdate(state, action, error);
        yAxisMirrorUpdate(state, action, error);
        rotationUpdate(state, action, error);
    }

    //Update fuction for rotational symmetry similarity
    protected void rotationUpdate(State state, int action, double error) {
        // 90, 180, 270 degree rotation similarity
        State rotatedState = state;
        for (int i = 0; i < 3; i++) {
            rotatedState = SimilarityFunctions.get90DegreeRotationState(rotatedState, ((PredatorWorld) prob).getSize());
            int rotatedAction = SimilarityFunctions.get90DegreeRotationtAction(action);

            updateQtableValue(error, rotatedAction, rotatedState);
            updatesNumber++;
        }
    }

    //Update fuction for x-axis mirroring symmetry similarity
    protected void xAxisMirrorUpdate(State state, int action, double error) {
        // X axis mirror similarity update
        State xAxisMirrorState = SimilarityFunctions.getXAxisMirrorState(state);
        int xAxisMirrorAction = SimilarityFunctions.getXAxisMirrorAction(action);

        updateQtableValue(error, xAxisMirrorAction, xAxisMirrorState);
        updatesNumber++;
    }

    //Update fuction for y-axis mirroring symmetry similarity
    protected void yAxisMirrorUpdate(State state, int action, double error) {
        // Y axis mirror similarity update
        State yAxisMirrorState = SimilarityFunctions.getYAxisMirrorState(state);
        int yAxisMirrorAction = SimilarityFunctions.getYAxisMirrorAction(action);

        updateQtableValue(error, yAxisMirrorAction, yAxisMirrorState);
        updatesNumber++;
    }

    protected void updateQtableValue(double error, int action, State state) {
        updateQtableValue(error, action, state, 1.0);
    }

    protected void updateQtableValue(double error, int action, State state, double factor) {
        double oldValue = 0;
        double newValue;

        StateAction currentStateAction = new StateAction(state, action);
        oldValue = qTable.get(currentStateAction);

        newValue = oldValue + factor * alpha * error;
        qTable.put(currentStateAction, newValue);
    }
}
