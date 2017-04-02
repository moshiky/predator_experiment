package problem.predator;

import problem.learning.SimilarityType;

/**
 * Created by Lev Levin on 05/11/2016.
 */
public class DynaPredatorCreator implements IPredatorCreator {
    private SimilarityType similarityType;

    public DynaPredatorCreator(SimilarityType similarityType) {
        this.similarityType = similarityType;
    }

    @Override
    public TCPredator Create(PredatorWorld pw, int size, int x, int y) {
        return new DynaPredator(pw, size, x, y, similarityType);
    }
}
