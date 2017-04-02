package problem.predator;

/**
 * Created by Lev Levin on 14/08/2016.
 */
public class GreedyPredatorCreator implements IPredatorCreator {

    @Override
    public Animal Create(PredatorWorld pw, int size, int x, int y) {
        return new GreedyPredator(pw, x, y);
    }
}
