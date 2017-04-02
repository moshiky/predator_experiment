package problem.learning;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.ArrayList;
import java.util.HashMap;

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

    //Represents the Q(S,A) function - represented by table
    protected double[][] qs;

    //previous action, and Q(S,A)
    protected int prevAction;
    protected int[] prevFa;

    //problem class, in this case predatorworld
    protected Problem prob;
    
    public LearningAgent(Problem prob){
        this.prob = prob;

        maxNrTiles = 4096;
        nrTiles = 14;

        alpha = 0.1 / nrTiles;
        epsilon = 0.1;
        gamma = 0.99;
    }
    
    public void initialize(){
        prevAction = RNG.randomInt(prob.getNumActions());
        prevFa = null;

        theta = new double[maxNrTiles];
        es = new double[maxNrTiles];
        qs = new double[prob.getNumStates()][prob.getNumActions()];
    }

    public void resetEs(){
        es = new double[maxNrTiles];
    }

    public abstract double[] getState();

    protected int actionSelection(double[] QS){
        double best = -Double.MAX_VALUE;
        ArrayList<Integer> ibest = new ArrayList <Integer>();
        
        for(int i=0; i<QS.length; i++){
            if(QS[i] >= best){
                if(QS[i] > best){
                    ibest.clear();
                }
                ibest.add(i);
                best = QS[i];
            }
        }
        
        int b = ibest.get(RNG.randomInt(ibest.size()));
        return b;
    }

    protected int[] tileCoding(double[] state, int action) {
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
