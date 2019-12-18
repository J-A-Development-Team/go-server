package JADevelopmentTeam.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

class Connector {
    private static Connector instance = null;
    private ServerSocket serverSocket = null;


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

    Human[] initializePlayers() {
        Human[] players = new Human[2];
        players[0] = connectPlayer();
        players[1] = connectPlayer();
        if (players[0] == null || players[1] == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }
        return players;
    }

    Human initializePlayer() {
        Human player = connectPlayer();
        if (player == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }
        return player;
    }

    private Human connectPlayer() {
        Socket socket;
        System.out.println("Oczekuję na klienta");
        try {
            socket = serverSocket.accept();
            Human player = new Human(socket);
            System.out.println("Connected First Player!");
            return player;
        } catch (IOException e) {
            return null;
        }
    }

}
