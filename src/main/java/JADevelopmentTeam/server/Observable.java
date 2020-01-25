package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import org.java_websocket.WebSocket;

import java.util.HashMap;

public class Observable {
    private static HashMap<WebSocket,Observer> observers = new HashMap<>();
    public static void notify(WebSocket webSocket, DataPackage dataPackage){
        observers.get(webSocket).update(dataPackage);
    }
    public static void addObserver(WebSocket webSocket,Observer observer){
        observers.put(webSocket,observer);
    }
    public static void delete(WebSocket webSocket){
        observers.get(webSocket).delete();
    }
    public static void notifyAll(DataPackage dataPackage){
        observers.forEach((a,b) -> b.update(dataPackage));
    }
}
