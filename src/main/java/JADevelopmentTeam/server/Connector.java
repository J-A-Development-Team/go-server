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
    private static ServerSocket serverSocket = null;
    private static HashSet<WebSocket> webSockets = new HashSet<>();
    private final Object lock;


    private Connector(Object lock) {
        super(new InetSocketAddress(8887));
        this.start();
        this.lock=lock;
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        System.out.println(webSocket);
        lastWebSocket = webSocket;
        synchronized (lock) {
            lock.notify();
        }
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        DataPackage dataPackage = new Gson().fromJson(s, DataPackage.class);
        Observable.notify(webSocket, dataPackage);
        System.out.println(webSocket);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }

    static Connector getInstance(Object lock) {
        if (instance == null) {
            instance = new Connector(lock);
        }
        return instance;
    }

    Human initializePlayer() {
        Human player = connectPlayer();
        if (player == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }
        return player;
    }

    private static Human connectPlayer() {
        webSockets.add(lastWebSocket);
        Human player = new Human(lastWebSocket);
        System.out.println("Connected First Player!");
        return player;
    }

}
