package JADevelopmentTeam.server;

import java.util.ArrayList;

public class Game implements Runnable {
    Player playerOne;
    Player playerTwo;

    public Game(Player[] players) {
        this.playerOne = players[0];
        this.playerTwo = players[1];
    }


    @Override
    public void run() {
        System.out.println("Starting players");
        synchronized (this){
            playerOne.inGame = true;
            playerTwo.inGame = true;
        }
    }
}
