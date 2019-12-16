package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;

import java.io.IOException;


public class Bot extends Player {
    boolean botTurn;
    boolean isPlayingBlack;
    boolean opponentPassed;
    int points;
    GameManager gameManager;

    public Bot() {
        setPlayerState(PlayerState.WaitForStart);
    }

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }

    @Override
    public void send(DataPackage dataPackage) throws IOException {
        switch (dataPackage.getInfo()) {
            case Info:
                String i = (String) dataPackage.getData();
                if (i.equals("Connection to opponent lost"))
                    inGame = false;
                break;
            case Turn:
                String info = (String) dataPackage.getData();
                botTurn = info.equals("Your turn");
                if(botTurn){
                    setPlayerState(PlayerState.Receive);
                }else{
                    setPlayerState(PlayerState.NotYourTurn);
                }
                if (info.equals("Remove Dead Stones")) {
                    setPlayerState(PlayerState.EndGame);
                }
                break;
            case PlayerColor:
                String color = (String) dataPackage.getData();
                isPlayingBlack = color.equals("black");
                break;
            case Points:
                points = (Integer) dataPackage.getData();
                break;
            case Pass:
                opponentPassed = true;
                break;
            default:
                break;
        }
    }

    private void makeMove() {
        dataPackage =  BotBrain.getOptimalMove(gameManager,isPlayingBlack);
    }

    @Override
    public void receive() throws IOException, ClassNotFoundException {
        makeMove();
        receivedData = true;
        setPlayerState(PlayerState.NotYourTurn);
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void run() {
        while (true) {
            switch (getPlayerState()) {
                case Receive:
                    try {
                        receive();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
                case NotYourTurn:
                    synchronized (this){
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
                        synchronized (this){
                            wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
            }
        }
    }
}
