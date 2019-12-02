package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Stone;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player implements Runnable{
    boolean inGame = true;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    private DataPackage dataPackage;
    public enum PlayerState {
        Receive, Send, WaitForStart,NotYourTurn,EndGame
    }
    private PlayerState playerState = PlayerState.WaitForStart;

    public Player(Socket socket) {
        try {
            is = new ObjectInputStream(socket.getInputStream());
            os = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void setDataPackage(DataPackage dataPackage) {
        this.dataPackage = dataPackage;
    }

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    private void send() throws IOException {
        os.writeObject(dataPackage);
        os.flush();
    }

    private void receive() throws IOException, ClassNotFoundException {
        //(Stone) is.readObject();
    }


    @Override
    public void run() {
        while(inGame){
            switch (playerState){
                case Send:
                    try {
                        send();
                    } catch (IOException e) {
                        System.out.println("Coś nie wyszło w send() player");
                    }
                    break;
                case Receive:
                    try {
                        receive();
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case EndGame:
                    inGame = false;
                    break;
                case NotYourTurn:

                case WaitForStart:
            }
        }
        System.out.println("Lets Play!");
    }
}
