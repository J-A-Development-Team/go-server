package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public abstract class GameLogicCalculator {
    public static int calculateLiberties(Stone stone,Board board){
        int liberties = 0;
        int x = stone.getXCoordinate();
        int y = stone.getYCoordinate();
        ArrayList<Intersection> possibleLiberties = new ArrayList<>();
        Intersection [][]intersections = board.getIntersections();
        int size = intersections.length;
        if(x<size-1) possibleLiberties.add(intersections[x+1][y]);
        if(y<size-1) possibleLiberties.add(intersections[x][y+1]);
        if(x>0) possibleLiberties.add(intersections[x-1][y]);
        if(y>0) possibleLiberties.add(intersections[x][y-1]);
        for (Intersection intersection : possibleLiberties){
            if(!intersection.isHasStone()) liberties++;
        }
        return liberties;
    }
    public static int calculateLiberties(StoneChain stoneChain,Board board){
        int liberties = 0;
        for(Stone stone: stoneChain.getStones()){
            liberties += calculateLiberties(stone,board);
        }
        return liberties;
    }
}
