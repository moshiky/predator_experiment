package problem.learning;

/**
 * Created by Lev Levin on 14/08/2016.
 */

/*
 * Symmetry: similarity described by rotation and mirroring
 * Transaction: similarity described by transactions leading to the same state
 * AllCombined: similrity which combines all the types of similarities
 * None: using none of th aobve similarities
 * */
public enum SimilarityType {
    Symmetry, Transaction, AllCombined, None
}
