/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;
import problem.RNG;
import problem.learning.SimilarityType;

/**
 * @author timbrys
 * Updated by Lev Levin
 */
public class TCPredator extends Animal {

    protected PredatorWorld pw;
    protected int size;

    public TCPredator(PredatorWorld pw, int size, int x, int y) {
        super(pw, x, y);
        this.predator = true;
        this.size = size;
        this.pw = pw;

        maxNrTiles = 4096;
        nrTiles = 32;

        alpha = 0.1 / nrTiles;
        epsilon = 0.1;
        gamma = 0.9;
        //lambda = 0.9;

        initialize();
    }

    //relative x and y positions of the other problem.predator and the prey
    //tile width of 10
    public double[] getState() {
        Animal[] predators = pw.getPredators();
        Animal[] preys = pw.getPreys();
        double[] state = new double[4];
        for (int i = 0; i < predators.length; i++) {
            if (predators[i] != this) {
                state[0] = (x - predators[i].x) / 10.0;
                state[1] = (y - predators[i].y) / 10.0;
            }
        }

        state[2] = (x - preys[0].x) / 10.0;
        state[3] = (y - preys[0].y) / 10.0;
        return state;
    }

    @Override
    public void updateQBySimilarity(State state, int action, double error, SimilarityType similarityType) {
    }

    @Override
    public void selectAction() {

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
        }

        prevAction = action;


        /*double[] state = getState();

        //finds activated weights for each action
        int[][] Fas = new int[prob.getNumActions()][];
        for(int i=0; i<prob.getNumActions(); i++){
            Fas[i] = tileCoding(state, i);
        }


        double[] Qs = new double[prob.getNumActions()];

        //each tile separately
        double weights[][] = new double[prob.getNumActions()][nrTiles];

        for (int i = 0; i < prob.getNumActions(); i++) {
            for (int j = 0; j < Fas[i].length; j++) {
                Qs[i] += theta[Fas[i][j]];
                weights[i][j] = theta[Fas[i][j]];
            }
        }

        //adaptive or random objective selection + action selection
        int action = actionSelection(Qs);
        //random


        prevAction = action;*/
    }
}
