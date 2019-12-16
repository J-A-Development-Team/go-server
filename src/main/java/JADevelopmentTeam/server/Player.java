package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player implements Runnable {
    boolean inGame = false;
    boolean receivedData = false;
    boolean acceptedStones = false;
    private Object lock;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    GameConfig gameConfig = null;
    DataPackage dataPackage;
    private PlayerState playerState = PlayerState.ConfigureGame;

    public Player() {
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public enum PlayerState {
        Receive, Send, WaitForStart, NotYourTurn, EndGame, ConfigureGame
    }


    public Player(Socket socket) {
        try {
            is = new ObjectInputStream(socket.getInputStream());
            os = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GameConfig getGameConfig() {
        return gameConfig;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public boolean isAcceptedStones() {
        return acceptedStones;
    }

    public void setAcceptedStones(boolean acceptedStones) {
        this.acceptedStones = acceptedStones;
    }

    public void sendTurnInfo() {
        try {
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
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void setLock(Object lock) {
        this.lock = lock;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void send(DataPackage dataPackage) throws IOException {
        os.writeObject(dataPackage);
        os.flush();
        os.reset();
    }

    public void receive() throws IOException, ClassNotFoundException {
        dataPackage = (DataPackage) is.readObject();
        if (playerState == PlayerState.Receive) {
            synchronized (lock) {
                receivedData = true;
                lock.notify();
            }
        }
    }
    public void configureGame() throws IOException, ClassNotFoundException{
        DataPackage dataPackage = (DataPackage) is.readObject();
        if (dataPackage.getInfo() == DataPackage.Info.GameConfig){
            GameConfig tempConfig = (GameConfig) dataPackage.getData();
            if (tempConfig.checkIfValid()){
                gameConfig = tempConfig;
                playerState = PlayerState.WaitForStart;
                sendTurnInfo();
            }
        }
    }

    @Override
    public void run() {
        while (playerState==PlayerState.ConfigureGame){
            try {
                configureGame();
            }catch (IOException | ClassNotFoundException e){
                System.out.println("Gracz się rozłączył przedwcześnie");
                return;
            }
        }
        inGame = true;
        System.out.println("Lets Play!");
        while (inGame) {
            try {
                receive();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Gracz się rozłączył");
                synchronized (lock) {
                    lock.notify();
                }
                inGame = false;
            }
        }

    }
}
