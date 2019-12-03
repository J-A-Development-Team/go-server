package JADevelopmentTeam.common;

import java.io.Serializable;

public class Stone implements Serializable {
    private int xCoordinate;
    private int yCoordinate;
    private boolean isBlack;
    private boolean exist;

    public Stone(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        boolean isBlack;
        exist=true;
    }

    public boolean exist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public Stone(int xCoordinate, int yCoordinate, boolean exist) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        boolean isBlack;
        this.exist=exist;
    }

    public int getXCoordinate() {
        return xCoordinate;
    }

    public int getYCoordinate() {
        return yCoordinate;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }
}
