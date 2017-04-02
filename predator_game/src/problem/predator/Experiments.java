/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import org.apache.commons.math3.util.Pair;
import problem.learning.SimilarityType;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * @author timbrys
 * Updated by Lev Levin
 */
public class Experiments {

    public static void main(String args[]) {
        double iterationsCount = 0;

        // definition of the different predators
        Pair qsPredator1 = new Pair(new QTablePredatorCreator(SimilarityType.Transaction), "QSTPredator");
        Pair qsPredator2 = new Pair(new QTablePredatorCreator(SimilarityType.AllCombined), "QSAPredator");
        Pair qsPredator3 = new Pair(new QTablePredatorCreator(SimilarityType.Symmetry), "QSSYPredator");
        Pair qsPredator4 = new Pair(new QTablePredatorCreator(SimilarityType.None), "QPredator");
        Pair tcPredator = new Pair(new TCPredatorCreator(), "TCPredator");
        Pair dynaPredator = new Pair(new DynaPredatorCreator(SimilarityType.None), "DynaPredator");
        Pair dynaSimPredator = new Pair(new DynaPredatorCreator(SimilarityType.AllCombined), "DynaSimPredator");

        // runs the Non Learning Greedy Predator
        //runNoLearningGreedyPredator();

        // the agents to run
        Pair[] agents = {/*tcPredator, qsPredator4, */dynaPredator, dynaSimPredator/*, qsPredator2*/};

        // initial learning episodes
        int initial = 100;
        for (Pair<IPredatorCreator, String> pair : agents) {
            for (int learningEpisodes = initial; learningEpisodes <= 10000; learningEpisodes += 100) {
                //runs the experiment on the given problem.learning amount of episodes
                //returns the average iterations count after problem.learning
                System.out.println("begin experiment..");
                iterationsCount = experiment(pair.getKey(), learningEpisodes);
                System.out.println(pair.getValue() + ": " + learningEpisodes + " - " + iterationsCount);
            }
        }
    }

    private static void runNoLearningGreedyPredator() {
        int episodes = 1000;
        PredatorWorld p = new PredatorWorld(20, 2, new GreedyPredatorCreator());
        double[] results = new double[episodes];
        for (int ep = 0; ep < episodes; ep++) {
            p.reset();
            results[ep] = p.episode(/*isLearningStage = */ false);
        }
        System.out.println("GreedyPredator : " + mean(results));
    }

    // Runs the experiment on a given problem.predator type, with given of problem.learning episodes amount
    // Returns the average amount of iterations it take the agent to win after al the problem.learning session
    public static double experiment(IPredatorCreator predatorCreator, int learningEpisodes) {
        PredatorWorld p = new PredatorWorld(20, 2, predatorCreator);
        //StopWatch sw = new StopWatch();
        for (int ep = 0; ep < learningEpisodes; ep++) {
            p.reset();
            System.out.println("start episode #" + ep);
            p.episode(/*isLearningStage = */ true);
        }

        //double iters = 0;
        //while (sw.getTime(TimeUnit.MILLISECONDS) < learningEpisodes) {
        //    p.reset();
        //    if (!sw.isStarted()) {
        //        sw.start();
        //    } else {
        //        sw.resume();
        //    }
        //    p.episode(/*isLearningStage = */ true);
        //    sw.suspend();
        //    iters++;
        //}

        //System.out.println("Iterations: " + iters);
        //System.out.println("Updates: " + p.getUpdatesNumber());
        //System.out.println("Time: " + stopWatch.getTime(TimeUnit.SECONDS));

        int episodes = 1000;
        double[] results = new double[episodes];
        for (int ep = 0; ep < episodes; ep++) {
            p.reset();
            results[ep] = p.episode(/*isLearningStage = */ false);
        }

        return mean(results);
    }

    //averages the results of a number of runs
    public static double[] means(double[][] stats) {
        double[] means = new double[stats[0].length];
        for (int j = 0; j < stats[0].length; j++) {
            for (int i = 0; i < stats.length; i++) {
                means[j] += stats[i][j];
            }
            means[j] = 1.0 * means[j] / (stats.length);
        }
        return means;
    }

    //averages the array of doubles
    public static double mean(double[] stats) {
        double means = 0.0;
        for (int i = 0; i < stats.length; i++) {
            means += stats[i];
        }
        means = 1.0 * means / (stats.length);
        return means;
    }
}
