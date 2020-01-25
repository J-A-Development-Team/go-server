package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;
import org.apache.log4j.BasicConfigurator;

public class Main {
    private static final Object lock = Main.class;
    public static void main(String[] args) {
        Server server = new Server();
        BasicConfigurator.configure();
        new Thread(server).start();
        Connector connector = Connector.getInstance(lock);
        while (true) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Human player = connector.initializePlayer();
            new Thread(player).start();
            server.lobby.addPlayerToLobby(player);
            DataPackage dataPackage = new DataPackage(new Intersection(1,1), DataPackage.Info.Stone);

            player.send(dataPackage);
        }
    }
}
