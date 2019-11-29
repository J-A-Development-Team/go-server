package JADevelopmentTeam.server;

import java.util.ArrayList;

public class Game implements Runnable {
    Player playerOne;
    Player playerTwo;

    public Game(ArrayList <Player> players) {
        this.playerOne = players.get(0);
        this.playerTwo = players.get(1);
    }


    @Override
    public void run() {
        System.out.println("Starting players");
        playerOne.inGame = true;
        playerTwo.inGame = true;
    }
}
