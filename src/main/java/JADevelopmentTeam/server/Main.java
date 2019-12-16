package JADevelopmentTeam.server;

public class Main {
    public static void main(String[] args) {
        Server server = new Server();
        new Thread(server).start();
        Connector connector = Connector.getInstance();
        while (true){
            server.lobby.addPlayerToLobby(connector.initializePlayer());
        }
    }
}
