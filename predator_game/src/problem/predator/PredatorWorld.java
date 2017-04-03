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
    private int m_worldSize;  // the predator is a square with edge length of m_worldSize
    private int m_numberOfPredators;
    private Animal[][] m_worldGrid;
    private Animal[] m_predators;
    private Animal[] m_preys;
    
    public PredatorWorld (int worldSize, int numberOfPredators, IPredatorCreator predatorCreator) {
        this.m_worldSize = worldSize;
        this.m_numberOfPredators = numberOfPredators;
        
        this.m_worldGrid = new Animal[worldSize][worldSize];
        
        this.m_predators = new Animal[numberOfPredators];
        this.m_preys = new Animal[numberOfPredators-1];
        for (int i = 0 ; i < numberOfPredators ; i++) {
            if(i < numberOfPredators - 1) {
                Animal a;
                do {
                    a = new Prey(this, worldSize, RNG.randomInt(worldSize), RNG.randomInt(worldSize));
                } while (occupied(a));
                this.m_preys[i] = a;
                this.m_worldGrid[a.y][a.x] = a;
            }
            Animal a;
            do{
                a = predatorCreator.Create(this, worldSize, RNG.randomInt(worldSize), RNG.randomInt(worldSize));
            } while (occupied(a));
            this.m_predators[i] = a;
            this.m_worldGrid[a.y][a.x] = a;
        }
    }
    
    public int getWorldSize() {
        return m_worldSize;
    }
    
    public int getNumStates() {
        return m_worldSize * m_worldSize;
    }

    // Initialize the world again, randomly placing the m_predators and prey
    public void reset(){
        this.m_worldGrid = new Animal[m_worldSize][m_worldSize];
        for(int i = 0; i< m_numberOfPredators; i++){
            if(i < m_numberOfPredators -1){
                m_preys[i].x = -1;
                m_preys[i].y = -1;
            }
            m_predators[i].x = -1;
            m_predators[i].y = -1;
            m_predators[i].resetEs();
        }
        for(int i = 0; i< m_numberOfPredators; i++){
            if(i < m_numberOfPredators -1){
                do{
                    m_preys[i].x = RNG.randomInt(m_worldSize);
                    m_preys[i].y = RNG.randomInt(m_worldSize);
                } while (occupied(m_preys[i]));
                this.m_worldGrid[m_preys[i].y][m_preys[i].x] = m_preys[i];
            }
            do{
                m_predators[i].x = RNG.randomInt(m_worldSize);
                m_predators[i].y = RNG.randomInt(m_worldSize);
            } while (occupied(m_predators[i]));
            this.m_worldGrid[m_predators[i].y][m_predators[i].x] = m_predators[i];
        }
    }
    
    public boolean occupied(Animal a){
        return m_worldGrid[a.y][a.x] != null;
    }
    
    //moves a problem.predator or prey, checking whether
    public void move(Animal a, boolean isLearningStage){
        int x = a.x;
        int y = a.y;
        this.m_worldGrid[a.y][a.x] = null;
        int dir = a.act();
        
        switch(dir){
            case 0: //UP
                if(a.y > 0){
                    a.y--;
                }
                break;
            case 1: //DOWN
                if(a.y < m_worldSize -1){
                    a.y++;
                }
                break;
            case 2: //LEFT
                if(a.x > 0){
                    a.x--;
                }
                break;
            case 3: //RIGHT
                if(a.x < m_worldSize -1){
                    a.x++;
                }
                break;
        }
        //if available, move onto location
        if(this.m_worldGrid[a.y][a.x] == null){
            this.m_worldGrid[a.y][a.x] = a;
            if(a.predator && isLearningStage){
                a.reward(0);
            }
            else if(a.predator) {
                a.selectAction();
            }
        //if this is a problem.predator, and the next location holds a prey, move onto location
        } else if(!this.m_worldGrid[a.y][a.x].predator && a.predator){
            this.m_worldGrid[a.y][a.x] = a;
        } else {
            //otherwise, do not move problem.predator
            a.y = y;
            a.x = x;
            this.m_worldGrid[a.y][a.x] = a;
            if(a.predator && isLearningStage){
                a.reward(0);
            }
            else if(a.predator) {
                a.selectAction();
            }
        }
    }
    
    public Animal[] getPredators() {
        return m_predators;
    }
    
    public Animal[] getPreys() {
        return m_preys;
    }

    @Override
    public void update(boolean isLearningStage) {
        for (Animal aPrey : m_preys) {
            move(aPrey, isLearningStage);
        }
        for (Animal aPredator : m_predators) {
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

        // if the prey was caught, reward the m_predators with a 1
        if(isGoalReached() && isLearningStage) {
            for (Animal aPredator : m_predators) {
                aPredator.reward(1);
            }
        }
        return iteration;
    }
    
    //check if the prey is caught
    public boolean isGoalReached(){
        for (Animal aPredator : m_predators) {
            for (Animal aPrey : m_preys) {
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
        for(int i = 0; i< m_worldSize; i++){
            for(int j = 0; j< m_worldSize; j++){
                boolean set = false;
                for(int l = 0; l< m_predators.length; l++){
                    if(m_predators[l] != null && m_predators[l].x == j && m_predators[l].y == i){
                        s.append("x");
                        set = true;
                        break;
                    }
                    if(l < m_preys.length && m_preys[l] != null && m_preys[l].x == j && m_preys[l].y == i){
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
