package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;

public class Human implements Player {
    private boolean inGame = false;
    boolean receivedData = false;
    boolean acceptedStones = false;
    boolean gameConfigured = false;
    Object lock;
    GameConfig gameConfig = null;
    DataPackage dataPackage;
    private WebSocket webSocket;
    private PlayerState playerState = PlayerState.ConfigureGame;

    public Human(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public boolean isAcceptedStones() {
        return acceptedStones;
    }

    public void setAcceptedStones(boolean acceptedStones) {
        this.acceptedStones = acceptedStones;
    }

    public boolean getInGame() {
        return inGame;
    }

    public void setInGame(boolean b) {
        inGame = b;
    }

    public void sendTurnInfo() {

        switch (playerState) {
            case WaitForStart:
                System.out.println("Sending start info");
                send(new DataPackage("Wait for start", DataPackage.Info.Turn));
                break;
            case NotYourTurn:
                send(new DataPackage("Not your turn", DataPackage.Info.Turn));
                break;
            case ConfigureGame:
                break;
            default:
                send(new DataPackage("Your turn", DataPackage.Info.Turn));
                break;
        }

    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public boolean getReceivedData() {
        return receivedData;
    }

    public void setReceivedData(boolean b) {
        receivedData = b;
    }

    public void send(DataPackage dataPackage) {
        switch (dataPackage.getInfo()) {
            case StoneTable:
                webSocket.send(new Gson().toJson(new DataPackage(new Gson().toJson(dataPackage.getData()), DataPackage.Info.StoneTable)));
                break;
            case TerritoryTable:
                webSocket.send(new Gson().toJson(new DataPackage(new Gson().toJson(dataPackage.getData()), DataPackage.Info.TerritoryTable)));
                break;
            default:
                webSocket.send(new Gson().toJson(dataPackage));
        }
    }

    @Override
    public void receive() {

    }

    public void configureGame(DataPackage dataPackage) {
        if (dataPackage.getInfo() == DataPackage.Info.GameConfig) {
            GameConfig tempConfig = (GameConfig) dataPackage.getData();
            if (tempConfig.checkIfValid()) {
                gameConfig = tempConfig;
                gameConfigured = true;
                playerState = PlayerState.WaitForStart;
                sendTurnInfo();
            }
        }
    }

    @Override
    public void run() {
    }

    @Override
    public void update(DataPackage dataPackage) {
        if (!gameConfigured) {
            configureGame(dataPackage);
            inGame = true;
            return;
        }
        this.dataPackage = dataPackage;
        receivedData = true;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void delete() {
        inGame = false;
    }
}
