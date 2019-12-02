package JADevelopmentTeam.common;

import java.io.Serializable;

public class Stone implements Serializable {
    private int xCoordinate;
    private int yCoordinate;
    private boolean isBlack;

    public Stone(int xCoordinate, int yCoordinate) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        boolean isBlack;
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
