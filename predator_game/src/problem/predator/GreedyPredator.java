package problem.predator;

import problem.RNG;
import problem.learning.SimilarityType;

/**
 * Created by Lev Levin on 14/08/2016.
 */
public class GreedyPredator extends Animal {

    public GreedyPredator(PredatorWorld pw, int x, int y) {
        super(pw, x, y);
        this.predator = true;
    }

    @Override
    public void selectAction() {
    }

    @Override
    public void reward(double reward) {
        selectAction();
    }

    @Override
    public int act(){
        return RNG.randomInt(Direction.values().length);
    }

    @Override
    public double[] getState() {
        return new double[0];
    }

    @Override
    public void updateQBySimilarity(State state, int action, double error, SimilarityType similarityType) {
    }
}
