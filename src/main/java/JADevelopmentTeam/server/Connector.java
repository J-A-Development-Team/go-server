package JADevelopmentTeam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Connector {
    private static Connector instance = null;
    private ServerSocket serverSocket = null;
    private ExecutorService pool;


    private Connector() {
        try {
            serverSocket = new ServerSocket(4444);
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

    Player[] initializePlayers() {
        Player[] players = new Player[2];
        players[0] = connectPlayer(pool);
        players[1] = connectPlayer(pool);
        if (players[0] == null || players[1] == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }
        return players;
    }

    private Player connectPlayer(ExecutorService pool) {
        Socket socket;
        System.out.println("Oczekuję na klienta");
        try {
            socket = serverSocket.accept();
            Player player = new Player(socket);
            System.out.println("Connected First Player!");
            return player;
        } catch (IOException e) {
            return null;
        }
    }

}
