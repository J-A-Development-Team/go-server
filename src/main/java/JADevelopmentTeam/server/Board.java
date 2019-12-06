package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public class Board {
    private Intersection[][] intersections;
    private int size;

    public Board(int size) {
        this.size = size;
        intersections = new Intersection[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                intersections[i][j] = new Intersection(i,j,false);
            }
        }
    }

    public int getSize() {
        return size;
    }

    public Intersection[][] getIntersections() {
        return intersections;
    }

    public void setIntersection(Intersection intersection) {
        this.intersections[intersection.getXCoordinate()][intersection.getYCoordinate()] = intersection;
    }

    public Board copy() {
        Board clone = new Board(this.getSize());
        for (int i = 0; i < this.getSize(); i++) {
            for (int j = 0; j < this.getSize(); j++) {
                Intersection original = this.getIntersections()[j][i];
                Intersection copy = new Intersection(j,i);
                copy.setHasStone(original.isHasStone());
                copy.setStoneBlack(original.isStoneBlack());
                clone.setIntersection(copy);
            }
        }
        return clone;
    }
}
