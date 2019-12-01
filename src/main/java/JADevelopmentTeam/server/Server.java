package JADevelopmentTeam.server;

import java.util.ArrayList;

public class Server {
    public static void main(String[] args) {
        Connector connector = Connector.getInstance();
        while (true){
            Player[]  players = connector.initializePlayers();
            Game game = new Game(players);
            game.run();
            System.out.println("Next Game");
        }
    }
}
