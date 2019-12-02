package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player implements Runnable {
    boolean inGame = true;
    private Game context;
    private ObjectInputStream is = null;
    private ObjectOutputStream os = null;
    DataPackage dataPackage;

    public DataPackage getDataPackage() {
        return dataPackage;
    }

    public enum PlayerState {
        Receive, Send, WaitForStart, NotYourTurn, EndGame
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

    public void setContext(Game context) {
        this.context = context;
    }

    public void setPlayerState(PlayerState playerState) {
        this.playerState = playerState;
    }

    public void send(DataPackage dataPackage) throws IOException {
        os.writeObject(dataPackage);
        os.flush();
    }

    private void receive() throws IOException, ClassNotFoundException {
        DataPackage dataPackage = (DataPackage) is.readObject();
        switch (playerState) {
            case WaitForStart:
                send(new DataPackage("Wait for start", DataPackage.Info.Info));
                break;
            case NotYourTurn:
                send(new DataPackage("Not your turn", DataPackage.Info.Info));
                break;
            default:
                synchronized (context){
                    notify();
                }
        }
    }


    @Override
    public void run() {
        System.out.println("Lets Play!");

        while (inGame) {
            try {
                receive();
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("error :(");
            }
        }

    }
}
