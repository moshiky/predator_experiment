package problem.learning;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;

import problem.RNG;
import problem.predator.State;
import problem.predator.StateAction;

/**
 *
 * @author timbrys
 * Updated by Lev Levin
 */
public abstract class LearningAgent{
    
    //problem.learning params
    protected double alpha;
    protected double epsilon;
    protected double gamma;

    //weights and traces for each 'objective'
    protected double[] theta;
    protected double[] es;
    protected static DoubleHashTable<StateAction> qTable;

    //tile coding params
    protected int nrTiles;
    protected int maxNrTiles;

    // Represents the Q(S,A) function - represented by table
    protected double[][] qs;

    // Previous action, and Q(S,A)
    protected int previousAction;
    protected int[] previousFa;

    // Problem class
    protected Problem p_problemWorld;
    
    public LearningAgent(Problem problemWorld) {
        this.p_problemWorld = problemWorld;

        maxNrTiles = 4096;
        nrTiles = 14;

        alpha = 0.1 / nrTiles;
        epsilon = 0.1;
        gamma = 0.99;
    }
    
    public void initialize() {
        previousAction = RNG.randomInt(p_problemWorld.getNumActions());
        previousFa = null;

        theta = new double[maxNrTiles];
        es = new double[maxNrTiles];
        qs = new double[p_problemWorld.getNumStates()][p_problemWorld.getNumActions()];
    }

    public void resetEs() {
        es = new double[maxNrTiles];
    }

    public abstract double[] getState();

    protected int selectBestAction(double[] availableActionReturns) {
        double bestActionReturn = -Double.MAX_VALUE;
        ArrayList<Integer> bestActionIndexes = new ArrayList <>();
        
        for (int actionIndex = 0 ; actionIndex < availableActionReturns.length ; actionIndex++) {
            if (availableActionReturns[actionIndex] >= bestActionReturn) {
                if (availableActionReturns[actionIndex] > bestActionReturn) {
                    bestActionIndexes.clear();
                }
                bestActionIndexes.add(actionIndex);
                bestActionReturn = availableActionReturns[actionIndex];
            }
        }
        
        return bestActionIndexes.get(RNG.randomInt(bestActionIndexes.size()));
    }

    protected int[] getTileCodingTiles(double[] state, int action) {
        int extra1 = action;
        int extra2 = -1;
        int extra3 = -1;

        return TileCoding.GetTiles(nrTiles, state, maxNrTiles, extra1, extra2, extra3);
    }

    public abstract int act();

    public abstract void reward(double reward);

    // abstract update function, to use when updating the QTable by similarity
    public abstract void updateQBySimilarity(State state, int action, double error, SimilarityType similarityType);
}
