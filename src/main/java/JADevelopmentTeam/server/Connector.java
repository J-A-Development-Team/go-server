package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.HashSet;

class Connector extends WebSocketServer {
    private static Connector instance = null;
    private static WebSocket lastWebSocket = null;
    private ServerSocket serverSocket = null;
    private HashSet<WebSocket> webSockets = new HashSet<>();


    private Connector() {
        super(new InetSocketAddress(8887));
        this.start();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        lastWebSocket = webSocket;
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        DataPackage dataPackage = new Gson().fromJson(s, DataPackage.class);
        Observable.notify(webSocket, dataPackage);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    static Connector getInstance() {
        if (instance == null) {
            instance = new Connector();
        }
        return instance;
    }

//    Human[] initializePlayers() {
//        Human[] players = new Human[2];
//        players[0] = connectPlayer();
//        players[1] = connectPlayer();
//        if (players[0] == null || players[1] == null) {
//            System.out.println("Accept failed: 4444");
//            System.exit(-1);
//        }
//        return players;
//    }

    Human initializePlayer() {
        Human player = connectPlayer();
        if (player == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }
        return player;
    }

    private Human connectPlayer() {
        System.out.println("Oczekuję na klienta");
        webSockets.add(lastWebSocket);
        while (true) {
            if (lastWebSocket != null) {
                if (!webSockets.contains(lastWebSocket)) {
                    webSockets.add(lastWebSocket);
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        Human player = new Human(lastWebSocket);
        System.out.println("Connected First Player!");
        return player;
    }

}
