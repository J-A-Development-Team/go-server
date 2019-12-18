package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;

import java.io.IOException;

public interface Player extends Runnable {

    public void sendTurnInfo();

    public PlayerState getPlayerState();

    public void setPlayerState(PlayerState playerState);

    public DataPackage getDataPackage();

    public boolean isAcceptedStones();

    void setAcceptedStones(boolean b);

    public boolean getInGame();

    public void setInGame(boolean b);

    public void setLock(Object lock);

    public boolean getReceivedData();

    public void setReceivedData(boolean b);

    public void send(DataPackage dataPackage) throws IOException;

    public void receive() throws IOException, ClassNotFoundException;

    @Override
    public void run();

    GameConfig getGameConfig();


    public enum PlayerState {
        Receive, Send, WaitForStart, NotYourTurn, EndGame, ConfigureGame
    }
}
