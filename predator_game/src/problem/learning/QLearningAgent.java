/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.learning;

import problem.RNG;

/**
 *
 * @author timbrys
 * Updated by Lev Levin
 */
public abstract class QLearningAgent extends LearningAgent {

    public QLearningAgent(Problem problemWorld){
        super(problemWorld);
    }

    public int act(){
        if(previousFa == null){
            previousFa = getTileCodingTiles(getState(), previousAction);
        }

        return previousAction;
    }

    public void reward(double reward) {

        // applies each time a different shaping to the base reward
        double delta = reward;

        // delta = r - Q(s,a)
        for (int i = 0; i < previousFa.length; i++) {
            delta -= theta[previousFa[i]];
        }

        double[] state = getState();

        //finds activated weights for each action
        int[][] Fas = new int[p_problemWorld.getNumActions()][];
        for(int i = 0; i < p_problemWorld.getNumActions(); i++){
            Fas[i] = getTileCodingTiles(state, i);
        }

        //will store Q-values for each objective-action pair (given current state)
        double Qs[] = new double[p_problemWorld.getNumActions()];
        double best = -Double.MAX_VALUE;

        //calculates Q-values and stores best for each objective
        for(int i = 0; i< p_problemWorld.getNumActions(); i++){
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
            Qs = new double[p_problemWorld.getNumActions()];

            //each tile separately
            double weights[][] = new double[p_problemWorld.getNumActions()][nrTiles];

            for(int i = 0; i< p_problemWorld.getNumActions(); i++){
                for (int j = 0; j < Fas[i].length; j++) {
                    Qs[i] += theta[Fas[i][j]];
                    weights[i][j] = theta[Fas[i][j]];
                }
            }
            //adaptive or random objective selection + action selection
            action = selectBestAction(Qs);
            //random
        } else {
            action = RNG.randomInt(p_problemWorld.getNumActions());
            //resets all traces. we should check whether the random action
            //happens to be greedy wrt to one of the objectives
            resetEs();
        }

        //s' = s
        previousFa = getTileCodingTiles(state, action);
        previousAction = action;

        //update traces
        for (int i = 0; i < previousFa.length; i++) {
            es[previousFa[i]] = 1;
        }
    }

    // Calculates optimal action
    public abstract void selectAction();
}
