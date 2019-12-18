package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;

import java.io.IOException;

public interface Player extends Runnable {

    void sendTurnInfo();

    PlayerState getPlayerState();

    void setPlayerState(PlayerState playerState);

    DataPackage getDataPackage();

    boolean isAcceptedStones();

    void setAcceptedStones(boolean b);

    boolean getInGame();

    void setInGame(boolean b);

    void setLock(Object lock);

    boolean getReceivedData();

    void setReceivedData(boolean b);

    void send(DataPackage dataPackage) throws IOException;

    void receive() throws IOException, ClassNotFoundException;

    @Override
    void run();

    GameConfig getGameConfig();


    enum PlayerState {
        Receive, Send, WaitForStart, NotYourTurn, EndGame, ConfigureGame
    }
}
