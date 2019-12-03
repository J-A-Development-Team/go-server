package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public abstract class GameLogicCalculator {
    public static int calculateLiberties(Stone stone, Board board) {
        int liberties = 0;
        ArrayList<Intersection> possibleLiberties = getNeighborIntersections(stone, board);
        for (Intersection intersection : possibleLiberties) {
            if (!intersection.isHasStone()) liberties++;
        }
        return liberties;
    }

    public static int calculateLiberties(StoneChain stoneChain, Board board) {
        int liberties = 0;
        for (Stone stone : stoneChain.getStones()) {
            liberties += calculateLiberties(stone, board);
        }
        return liberties;
    }

    public static ArrayList<StoneChain> getNeighborChains(Stone stone, Board board, ArrayList<StoneChain> stoneChains, ArrayList<Stone> stones) {
        ArrayList<Intersection> neighbors = getNeighborIntersections(stone, board);
        ArrayList<Stone> neighborStones = new ArrayList<>();
        ArrayList<StoneChain> neighborStoneChains = new ArrayList<>();
        for (Intersection intersection : neighbors) {
            Stone stoneOnIntersection = getStoneForIntersection(intersection, stones);
            if (stoneOnIntersection != null) neighborStones.add(stoneOnIntersection);
        }
        for (Stone neighborStone : neighborStones) {
            StoneChain neighborStoneChain = stoneChains.get(getStoneChainIDForStone(neighborStone, stoneChains));
            if (!neighborStoneChains.contains(neighborStoneChain)) {
                neighborStoneChains.add(neighborStoneChain);
            }
        }
        return neighborStoneChains;
    }

    private static Stone getStoneForIntersection(Intersection intersection, ArrayList<Stone> stones) {
        for (Stone stone : stones) {
            if (stone.getXCoordinate() == intersection.getXCoordinate()
                    && stone.getYCoordinate() == intersection.getYCoordinate()) {
                return stone;
            }
        }
        return null;
    }

    private static int getStoneChainIDForStone(Stone stone, ArrayList<StoneChain> stoneChains) {
        for (StoneChain stoneChain : stoneChains) {
            if (stoneChain.getStones().indexOf(stone) != -1) {
                return stoneChains.indexOf(stoneChain);
            }
        }
        return -1;
    }

    private static ArrayList<Intersection> getNeighborIntersections(Stone stone, Board board) {
        int x = stone.getXCoordinate();
        int y = stone.getYCoordinate();
        ArrayList<Intersection> neighbors = new ArrayList<>();
        Intersection[][] intersections = board.getIntersections();
        int size = intersections.length;
        if (x < size - 1) neighbors.add(intersections[x + 1][y]);
        if (y < size - 1) neighbors.add(intersections[x][y + 1]);
        if (x > 0) neighbors.add(intersections[x - 1][y]);
        if (y > 0) neighbors.add(intersections[x][y - 1]);
        return neighbors;
    }

    public static StoneChain generateSuperStoneChain(ArrayList<StoneChain> stoneChains, Stone connector) {
        StoneChain superStoneChain = new StoneChain(connector);
        for (StoneChain stoneChain : stoneChains) {
            superStoneChain.addStones(stoneChain.getStones());
        }
        return superStoneChain;
    }

    public static int checkIfMoveIsValid(Intersection chosenIntersection, Board board, int turn) {
        Stone testStone = new Stone(chosenIntersection.getXCoordinate(), chosenIntersection.getYCoordinate());
        //TODO implement some real validation
        return 0;
    }
}
