package JADevelopmentTeam.server.Bot;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;
import JADevelopmentTeam.server.GameLogic.GameManager;
import JADevelopmentTeam.server.Player;

import java.io.IOException;


public class Bot implements Player {
    boolean botTurn;
    boolean isPlayingBlack;
    boolean opponentPassed;
    int points;
    GameManager gameManager;
    boolean inGame = false;
    boolean receivedData = false;
    boolean acceptedStones = false;
    Object lock = null;
    DataPackage dataPackage = null;
    PlayerState playerState = PlayerState.ConfigureGame;

    public Bot() {
        setPlayerState(PlayerState.WaitForStart);
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

    public void setAcceptedStones(boolean b) {
        acceptedStones = b;
    }

    public boolean getInGame() {
        return inGame;
    }

    public void setInGame(boolean b) {
        inGame = b;
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

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    public void send(DataPackage dataPackageToSend) throws IOException {
        switch (dataPackageToSend.getInfo()) {
            case InfoMessage:
                String i = (String) dataPackageToSend.getData();
                if (i.equals("Connection to opponent lost"))
                    inGame = false;
                System.out.println(i);
                break;
            case Turn:
                String info = (String) dataPackageToSend.getData();
                botTurn = info.equals("Your turn");
                if (botTurn) {
                    setPlayerState(PlayerState.Receive);
                } else {
                    setPlayerState(PlayerState.NotYourTurn);
                }
                if (info.equals("Remove Dead Stones")) {
                    setPlayerState(PlayerState.EndGame);
                    setAcceptedStones(true);
                }
                break;
            case PlayerColor:
                String color = (String) dataPackageToSend.getData();
                isPlayingBlack = color.equals("black");
                break;
            case Points:
                points = (Integer) dataPackageToSend.getData();
                break;
            case Pass:
                opponentPassed = true;
                break;
            default:
                System.out.println(dataPackageToSend.getInfo());
                break;
        }
    }

    public void sendTurnInfo() {
    }

    private void makeMove() {
        dataPackage = BotBrain.getOptimalMove(gameManager, isPlayingBlack);
        System.out.println("Robię ruch: " + dataPackage.getInfo());
    }

    public void receive() throws IOException, ClassNotFoundException {
        makeMove();
        receivedData = true;
        setPlayerState(PlayerState.NotYourTurn);
        synchronized (lock) {
            lock.notify();
        }
    }

    public GameConfig getGameConfig() {
        return null;
    }

    public void run() {
        while (true) {
            switch (getPlayerState()) {
                case Receive:
                    synchronized (this) {
                        try {
                            wait(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        receive();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case NotYourTurn:
                    synchronized (this) {
                        try {
                            wait(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    receivedData = false;
                    break;
                default:
                    try {
                        synchronized (this) {
                            wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }


    @Override
    public void update(DataPackage dataPackage) {

    }

    @Override
    public void delete() {

    }
}
