package JADevelopmentTeam.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Player implements Runnable{
    boolean inGame = false;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;

    public Player(Socket socket) {
        try {
            in = new ObjectInputStream(socket.getInputStream());
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    void waitForStartOfGame(){
        while(!inGame){

        }
    }
    public void run() {
        waitForStartOfGame();
        System.out.println("Lets Play!");
    }
}
