/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package problem.predator;

import problem.RNG;
import problem.learning.Problem;

/**
 * @author timbrys.
 * Updated by Lev Levin.
 * Updated by Moshe Cohen.
 */
public class PredatorWorld extends Problem {
    private int size;
    private int nrPredators;
    private Animal[][] map;
    private Animal[] aPredators;
    private Animal[] aPreys;
    
    public PredatorWorld (int size, int nrPredators, IPredatorCreator predatorCreator) {
        this.size = size;
        this.nrPredators = nrPredators;
        
        this.map = new Animal[size][size];
        
        this.aPredators = new Animal[nrPredators];
        this.aPreys = new Animal[nrPredators-1];
        for(int i=0; i<nrPredators; i++){
            if(i < nrPredators-1){
                Animal a;
                do{
                    a = new Prey(this, size, RNG.randomInt(size), RNG.randomInt(size));
                } while (occupied(a));
                this.aPreys[i] = a;
                this.map[a.y][a.x] = a;
            }
            Animal a;
            do{
                a = predatorCreator.Create(this, size, RNG.randomInt(size), RNG.randomInt(size));
            } while (occupied(a));
            this.aPredators[i] = a;
            this.map[a.y][a.x] = a;
        }
    }
    
    public int getSize() {
        return size;
    }
    
    public int getNumStates() {
        return size * size;
    }

    // Initialize the world again, randomly placing the predators and prey
    public void reset(){
        this.map = new Animal[size][size];
        for(int i=0; i<nrPredators; i++){
            if(i < nrPredators-1){
                aPreys[i].x = -1;
                aPreys[i].y = -1;
            }
            aPredators[i].x = -1;
            aPredators[i].y = -1;
            aPredators[i].resetEs();
        }
        for(int i=0; i<nrPredators; i++){
            if(i < nrPredators-1){
                do{
                    aPreys[i].x = RNG.randomInt(size);
                    aPreys[i].y = RNG.randomInt(size);
                } while (occupied(aPreys[i]));
                this.map[aPreys[i].y][aPreys[i].x] = aPreys[i];
            }
            do{
                aPredators[i].x = RNG.randomInt(size);
                aPredators[i].y = RNG.randomInt(size);
            } while (occupied(aPredators[i]));
            this.map[aPredators[i].y][aPredators[i].x] = aPredators[i];
        }
    }
    
    public boolean occupied(Animal a){
        return map[a.y][a.x] != null;
    }
    
    //moves a problem.predator or prey, checking whether
    public void move(Animal a, boolean isLearningStage){
        int x = a.x;
        int y = a.y;
        this.map[a.y][a.x] = null;
        int dir = a.act();
        
        switch(dir){
            case 0: //UP
                if(a.y > 0){
                    a.y--;
                }
                break;
            case 1: //DOWN
                if(a.y < size-1){
                    a.y++;
                }
                break;
            case 2: //LEFT
                if(a.x > 0){
                    a.x--;
                }
                break;
            case 3: //RIGHT
                if(a.x < size-1){
                    a.x++;
                }
                break;
        }
        //if available, move onto location
        if(this.map[a.y][a.x] == null){
            this.map[a.y][a.x] = a;
            if(a.predator && isLearningStage){
                a.reward(0);
            }
            else if(a.predator) {
                a.selectAction();
            }
        //if this is a problem.predator, and the next location holds a prey, move onto location
        } else if(!this.map[a.y][a.x].predator && a.predator){
            this.map[a.y][a.x] = a;
        } else {
            //otherwise, do not move problem.predator
            a.y = y;
            a.x = x;
            this.map[a.y][a.x] = a;
            if(a.predator && isLearningStage){
                a.reward(0);
            }
            else if(a.predator) {
                a.selectAction();
            }
        }
    }
    
    public Animal[] getPredators() {
        return aPredators;
    }
    
    public Animal[] getPreys() {
        return aPreys;
    }

    @Override
    public void update(boolean isLearningStage) {
        for (Animal aPrey : aPreys) {
            move(aPrey, isLearningStage);
        }
        for (Animal aPredator : aPredators) {
            move(aPredator, isLearningStage);
        }
    }
    
    public double episode(boolean isLearningStage){
        int iteration = 0;
        while(!isGoalReached() ){
            update(isLearningStage);
            iteration++;
        }

        System.out.println("Iteration - " + iteration);

        // if the prey was caught, reward the predators with a 1
        if(isGoalReached() && isLearningStage) {
            for (Animal aPredator : aPredators) {
                aPredator.reward(1);
            }
        }
        return iteration;
    }
    
    //check if the prey is caught
    public boolean isGoalReached(){
        for (Animal aPredator : aPredators) {
            for (Animal aPrey : aPreys) {
                if (aPredator.x == aPrey.x && aPredator.y == aPrey.y) {
                    return true;
                }
            }
        }
        return false;
    }
    
    // print world
    public String toString(){
        StringBuilder s = new StringBuilder();
        for(int i=0; i<size; i++){
            for(int j=0; j<size; j++){
                boolean set = false;
                for(int l=0; l<aPredators.length; l++){
                    if(aPredators[l] != null && aPredators[l].x == j && aPredators[l].y == i){
                        s.append("x");
                        set = true;
                        break;
                    }
                    if(l < aPreys.length && aPreys[l] != null && aPreys[l].x == j && aPreys[l].y == i){
                        s.append("O");
                        set = true;
                        break;
                    }
                }
                if(!set)
                    s.append(" ");
            }
            s.append("\n");
        }
        return s.toString();
    }
    
    @Override
    public int getNumActions() {
        return 5;
    }
    
}
