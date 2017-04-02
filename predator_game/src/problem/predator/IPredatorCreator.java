package problem.predator;

import problem.learning.SimilarityType;

/**
 * Created by Lev Levin on 22/07/2016.
 */
public interface IPredatorCreator {
    Animal Create(PredatorWorld pw, int size, int x, int y);
}
