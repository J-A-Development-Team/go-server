package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;
import JADevelopmentTeam.common.Intersection;
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
        super(new InetSocketAddress(8888));
        this.start();
        this.lock = lock;
    }

    static Connector getInstance(Object lock) {
        if (instance == null) {
            instance = new Connector(lock);
        }
        return instance;
    }

    private static Human connectPlayer() {
        webSockets.add(lastWebSocket);
        Human player = new Human(lastWebSocket);
        Observable.addObserver(lastWebSocket, player);
        System.out.println("Connected First Player!");
        return player;
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
        Observable.delete(webSocket);
    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        DataPackage dataPackage = new DataPackage(null, null);
        try {
            System.out.println(s);
            dataPackage = new Gson().fromJson(s, DataPackage.class);

        } catch (IllegalStateException ex) {
            System.out.println(s);
        }
        if (dataPackage.getInfo() == DataPackage.Info.GameConfig) {
            GameConfig gameConfig = new Gson().fromJson(dataPackage.getData().toString(), GameConfig.class);
            dataPackage = new DataPackage(gameConfig, DataPackage.Info.GameConfig);
        } else if (dataPackage.getInfo() == DataPackage.Info.Stone) {
            dataPackage = new DataPackage(new Gson().fromJson(dataPackage.getData().toString(), Intersection.class), DataPackage.Info.Stone);
        }
        if (dataPackage != null)
            if (dataPackage.getInfo() != null && dataPackage.getData() != null) {
                Observable.notify(webSocket, dataPackage);
                System.out.println("dostałem" + s);
            }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        Observable.delete(webSocket);
    }

    @Override
    public void onStart() {

    }

    Human initializePlayer() {
        Human player = connectPlayer();
        if (player == null) {
            System.out.println("Accept failed: 4444");
            System.exit(-1);
        }
        return player;
    }

}
