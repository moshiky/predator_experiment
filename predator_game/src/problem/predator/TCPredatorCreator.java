package problem.predator;

/**
 * Created by Lev Levin on 22/07/2016.
 */
public class TCPredatorCreator implements IPredatorCreator {
    @Override
    public TCPredator Create(PredatorWorld pw, int size, int x, int y) {
        return new TCPredator(pw, size, x, y);
    }
}
