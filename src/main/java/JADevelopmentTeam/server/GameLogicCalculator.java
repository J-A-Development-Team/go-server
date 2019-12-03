package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public abstract class GameLogicCalculator {
    public static int calculateLiberties(Stone stone,Board board){
        ArrayList<Intersection> intersectionsNextToStone = new ArrayList<>();
        Intersection [][]intersections = board.getIntersections();
        int size = intersections.length;
        //intersectionsNextToStone.add(board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()]);
        //intersectionsNextToStone.add(board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()]);
        //intersectionsNextToStone.add(board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()]);
        //intersectionsNextToStone.add(board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()]);

        return size;
    }
}
