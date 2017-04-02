package problem.predator;

import problem.learning.SimilarityType;

/**
 * Created by Lev Levin on 23/07/2016.
 */
public class QTablePredatorCreator implements IPredatorCreator {

    private SimilarityType similarityType;

    public QTablePredatorCreator(SimilarityType similarityType) {
        this.similarityType = similarityType;
    }

    @Override
    public TCPredator Create(PredatorWorld pw, int size, int x, int y) {
        return new QTablePredator(pw, size, x, y, similarityType);
    }
}
