/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.learning;

/**
 *
 * @author timbrys
 * Updated by Lev Levin
 */
public abstract class Problem {

    public Problem() {
    }

    // Returns the amount of the states of the problem
    public abstract int getNumStates();

    public abstract void reset();

    public abstract void update(boolean isLearningStage);
    
    public abstract boolean isGoalReached();
    
    public abstract int getNumActions();
    
    public abstract double episode(boolean isLearningStage);
    
}
