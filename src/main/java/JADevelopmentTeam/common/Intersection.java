package JADevelopmentTeam.common;

import java.io.Serializable;

public class Intersection implements Serializable {
    private int xCoordinate;
    private int yCoordinate;
    private boolean isStoneBlack;
    private boolean hasStone;
    private boolean isStoneDead = false;

    public Intersection(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        hasStone =true;
    }

    public boolean exist() {
        return hasStone;
    }

    public void setHasStone(boolean hasStone) {
        this.hasStone = hasStone;
    }

    public Intersection(int xCoordinate, int yCoordinate, boolean hasStone) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.hasStone = hasStone;
    }

    public boolean isHasStone() {
        return hasStone;
    }

    public boolean isStoneDead() {
        return isStoneDead;
    }

    public void setStoneDead(boolean stoneDead) {
        isStoneDead = stoneDead;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public boolean isStoneBlack() {
        return isStoneBlack;
    }

    public void setStoneBlack(boolean stoneBlack) {
        isStoneBlack = stoneBlack;
    }
}
