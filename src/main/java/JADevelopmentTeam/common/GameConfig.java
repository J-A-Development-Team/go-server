package JADevelopmentTeam.common;

import java.io.Serializable;

public class GameConfig implements Serializable {
    boolean withBot;
    int boardSize;
    boolean wantsToStart;

    public GameConfig(boolean withBot, int boardSize, boolean wantsToStart) {
        this.withBot = withBot;
        this.boardSize = boardSize;
        this.wantsToStart = wantsToStart;
    }

    public boolean isWithBot() {
        return withBot;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public boolean isWantsToStart() {
        return wantsToStart;
    }

    public boolean checkIfValid() {
        switch (boardSize) {
            case 5:
            case 9:
            case 13:
            case 19:
                return true;
            default:
                return false;
        }
    }
}
