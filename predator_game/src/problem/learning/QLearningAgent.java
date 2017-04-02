/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.learning;

import problem.RNG;

import java.util.ArrayList;

/**
 *
 * @author timbrys
 * Updated by Lev Levin
 */
public abstract class QLearningAgent extends LearningAgent{

    public QLearningAgent(Problem prob){
        super(prob);
    }

    public int act(){
        if(prevFa == null){
            prevFa = tileCoding(getState(), prevAction);
        }

        return prevAction;
    }

    public void reward(double reward){

        //applies each time a different shaping to the base reward
        double delta = reward;

        //delta = r - Q(s,a)
        for (int i = 0; i < prevFa.length; i++) {
            delta -= theta[prevFa[i]];
        }

        double[] state = getState();

        //finds activated weights for each action
        int[][] Fas = new int[prob.getNumActions()][];
        for(int i=0; i<prob.getNumActions(); i++){
            Fas[i] = tileCoding(state, i);
        }

        //will store Q-values for each objective-action pair (given current state)
        double Qs[] = new double[prob.getNumActions()];
        double best = -Double.MAX_VALUE;

        //calculates Q-values and stores best for each objective
        for(int i=0; i<prob.getNumActions(); i++){
            for (int j = 0; j < Fas[i].length; j++) {
                Qs[i] += theta[Fas[i][j]];
            }
            if(Qs[i] > best){
                best = Qs[i];
            }
        }

        //delta = r + gamma max_a Q(s',a) - Q(s,a)
        delta += gamma * best;

        //update weights theta = alpha delta e
        for (int i = 0; i < theta.length; i++) {
            theta[i] += alpha * delta * es[i];
        }

        //action selection
        int action = 0;
        //greedy
        if (RNG.randomDouble() > epsilon) {
            Qs = new double[prob.getNumActions()];

            //each tile separately
            double weights[][] = new double[prob.getNumActions()][nrTiles];

            for(int i=0; i<prob.getNumActions(); i++){
                for (int j = 0; j < Fas[i].length; j++) {
                    Qs[i] += theta[Fas[i][j]];
                    weights[i][j] = theta[Fas[i][j]];
                }
            }
            //adaptive or random objective selection + action selection
            action = actionSelection(Qs);
            //random
        } else {
            action = RNG.randomInt(prob.getNumActions());
            //resets all traces. we should check whether the random action
            //happens to be greedy wrt to one of the objectives
            resetEs();
        }

        //s' = s
        prevFa = tileCoding(state, action);
        prevAction = action;

        //update traces
        for (int i = 0; i < prevFa.length; i++) {
            es[prevFa[i]] = 1;
        }
    }

    // Calculates optimal action
    public abstract void selectAction();
}
