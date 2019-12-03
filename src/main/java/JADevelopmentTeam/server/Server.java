package JADevelopmentTeam.server;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class Server {
    public static void main(String[] args) {
        Connector connector = Connector.getInstance();
        while (true){
            Player[]  players = connector.initializePlayers();
            Game game = new Game(players,9);
            new Thread(game).start();
            System.out.println("Next Game");
        }
    }

}
