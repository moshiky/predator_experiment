package problem.predator;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by Lev Levin on 23/07/2016.
 */

// Class with static help functions for similarity calculations
public class SimilarityFunctions {
    // Returns the similar state by x-axis mirroring
    public static State getXAxisMirrorState(State state) {
        State result;
        int mirrorPredX = state.predDistX;
        int mirrorPredY = (byte) -state.predDistY;
        int mirrorPreyX = state.preyDistX;
        int mirrorPreyY = (byte) -state.preyDistY;

        result = new State(mirrorPredX, mirrorPredY, mirrorPreyX, mirrorPreyY);
        return result;
    }

    // Returns the similar state by y-axis mirroring
    public static State getYAxisMirrorState(State state) {
        State result;

        int mirrorPredX = (byte) -state.predDistX;
        int mirrorPredY = state.predDistY;
        int mirrorPreyX = (byte) -state.preyDistX;
        int mirrorPreyY = state.preyDistY;

        result = new State(mirrorPredX, mirrorPredY, mirrorPreyX, mirrorPreyY);
        return result;
    }


    /**
     * Returns the similar action by x-axis mirroring
     * UP == 0
     * DOWN == 1
     * LEFT == 2
     * RIGHT == 3
     * NONE == 4
     */
    public static int getXAxisMirrorAction(int action) {
        switch (action) {
            case 0:
                return 1;
            case 1:
                return 0;
        }

        return action;
    }

    /**
     * Returns the similar action by y-axis mirroring
     * UP == 0
     * DOWN == 1
     * LEFT == 2
     * RIGHT == 3
     * NONE == 4
     */
    public static int getYAxisMirrorAction(int action) {
        switch (action) {
            case 2:
                return 3;
            case 3:
                return 2;
        }

        return action;
    }

    //Returns the similar state by 90 degree rotation
    public static State get90DegreeRotationState(State state, int size) {
        State result;
        int newPredX = (byte) -state.predDistY;
        int newPredY = state.predDistX;
        int newPreyX = (byte) -state.preyDistY;
        int newPreyY = state.preyDistX;

        result = new State(newPredX, newPredY, newPreyX, newPreyY);
        return result;
    }

    /**
     * Returns the similar action by 90 degree rotation
     * UP == 0
     * DOWN == 1
     * LEFT == 2
     * RIGHT == 3
     * NONE == 4
     */
    public static int get90DegreeRotationtAction(int action) {
        switch (action) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 2:
                return 0;
            case 3:
                return 1;
        }

        return action;
    }

    /**
     * calculates the similar <state, action> pairs which has transactional similarity to the original <state, action> pair
     * UP == 0
     * DOWN == 1
     * LEFT == 2
     * RIGHT == 3
     * NONE == 4
     */
    public static void getSimilarTransactions(State state, int action, int problemSize, Map<Integer, State> similarTransactions) {
        State destinationState = new State(state.predDistX, state.predDistY, state.preyDistX, state.preyDistY);
        switch (action) {
            case 0:
                destinationState.predDistY--;
                destinationState.preyDistY--;
                break;
            case 1:
                destinationState.predDistY++;
                destinationState.preyDistY++;
                break;
            case 2:
                destinationState.predDistX--;
                destinationState.preyDistX--;
                break;
            case 3:
                destinationState.predDistX++;
                destinationState.preyDistX++;
                break;
            case 4:
                return;
        }

        for (int i = 0; i < 4; i++) {
            if (i == action) {
                continue;
            }

            State neighborState;
            switch (i) {
                case 0:
                    if (destinationState.predDistY + 1 == problemSize ||
                            destinationState.preyDistY + 1 == problemSize) {
                        continue;
                    }
                    neighborState = new State(destinationState.predDistX,
                            (byte)(destinationState.predDistY + 1),
                            destinationState.preyDistX,
                            (byte)(destinationState.preyDistY + 1));
                    similarTransactions.put(i, neighborState);
                    break;
                case 1:
                    if (destinationState.predDistY - 1 == -problemSize ||
                            destinationState.preyDistY - 1 == -problemSize) {
                        continue;
                    }
                    neighborState = new State(destinationState.predDistX,
                            (byte)(destinationState.predDistY - 1),
                            destinationState.preyDistX,
                            (byte)(destinationState.preyDistY - 1));
                    similarTransactions.put(i, neighborState);
                    break;
                case 2:
                    if (destinationState.predDistX + 1 == problemSize ||
                            destinationState.preyDistX + 1 == problemSize) {
                        continue;
                    }
                    neighborState = new State((byte)(destinationState.predDistX + 1),
                            destinationState.predDistY,
                            (byte)(destinationState.preyDistX + 1),
                            destinationState.preyDistY);
                    similarTransactions.put(i, neighborState);
                    break;
                case 3:
                    if (destinationState.predDistX - 1 == -problemSize ||
                            destinationState.preyDistX - 1 == -problemSize) {
                        continue;
                    }
                    neighborState = new State((byte)(destinationState.predDistX - 1),
                            destinationState.predDistY,
                            (byte)(destinationState.preyDistX - 1),
                            destinationState.preyDistY);
                    similarTransactions.put(i, neighborState);
                    break;
            }
        }
    }
}
