/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import problem.learning.QLearningAgent;

/**
 *
 * @author timbrys
 */
public abstract class Animal extends QLearningAgent{
    public boolean predator;
    public int x;
    public int y;
    
    public Animal(PredatorWorld pw, int x, int y){
        super(pw);
        this.predator = false;
        this.x = x;
        this.y = y;
    }

    public long getUpdatesNumber() {
        return 0;
    }
}
