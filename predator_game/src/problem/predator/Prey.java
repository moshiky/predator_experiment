/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import java.util.ArrayList;
import problem.RNG;
import problem.learning.SimilarityType;

/**
 *
 * @author timbrys
 * Updated by Lev Levin
 */
public class Prey extends Animal {
    
    private int size;
    private PredatorWorld pw;
    
    public Prey(PredatorWorld pw, int size, int x, int y){
        super(pw, x, y);
        this.size = size;
        this.pw = pw;
    }
    
    @Override
    public int act(){
        if(RNG.randomDouble() < 0.2){
            return RNG.randomInt(Direction.values().length);
        }
        
        Animal[] predators = pw.getPredators();
        double best = -Double.MAX_VALUE;
        ArrayList<Integer> ibest = new ArrayList<>();
        for(int i=0; i<Direction.values().length; i++){
            double[] coords = new double[]{x,y};
            simMove(Direction.values()[i], coords);
            double dist = distance(coords[0], coords[1], predators[0].x, predators[0].y) 
                    + distance(coords[0], coords[1], predators[1].x, predators[1].y);
            if(dist >= best){
                if(dist > best){
                    ibest.clear();
                }
                ibest.add(i);
                best = dist;
            }
        }
        return ibest.get(RNG.randomInt(ibest.size()));
    }

    @Override
    public void updateQBySimilarity(State state, int action, double error, SimilarityType similarityType) {
    }

    @Override
    public double[] getState() {
        return new double[0];
    }

    @Override
    public void reward(double reward){

    }

    @Override
    public void selectAction() {
    }

    protected double distance(double x1, double y1, double x2, double y2){
        return Math.sqrt((x1-x2)*(x1-x2) + (y1-y2)*(y1-y2));
    }

    protected void simMove(Direction dir, double[] coords){
        switch(dir){
            case Up:
                if(coords[1] > 0){
                    coords[1]--;
                }
                break;
            case Down:
                if(coords[1] < size-1){
                    coords[1]++;
                }
                break;
            case Left:
                if(coords[0] > 0){
                    coords[0]--;
                }
                break;
            case Right:
                if(coords[0] < size-1){
                    coords[0]++;
                }
                break;
        }
    }
}
