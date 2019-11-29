package JADevelopmentTeam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Connector {
    private static Connector instance = null;
    private ServerSocket serverSocket = null;
    private ExecutorService pool;


    private Connector() {
        try {
            serverSocket = new ServerSocket(4444);
            pool = Executors.newFixedThreadPool(200);
            System.out.println("port assigned");
        } catch (IOException ex) {
            System.out.println("Could not listen on port 4444");
            System.exit(-1);
        }
    }

    static Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
        }
        return instance;
    }

    ArrayList <Player> initializePlayers() {
        ArrayList <Player> players = new ArrayList<>();
        Player playerOne = connectPlayer(pool);
        Player playerTwo = connectPlayer(pool);
        if (playerOne == null || playerTwo == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }else{
            players.add(playerOne);
            players.add(playerTwo);
        }
        return players;
    }

    private Player connectPlayer(ExecutorService pool) {
        Socket socket;
        System.out.println("Oczekuję na klienta");
        try {
            socket = serverSocket.accept();
            Player player = new Player(socket);
            pool.execute(player);
            System.out.println("Connected First Player!");
            return player;
        } catch (IOException e) {
            return null;
        }
    }

}
