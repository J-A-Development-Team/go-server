package JADevelopmentTeam.server;

public class Server implements Runnable {
    Lobby lobby;
    Object lock = this;

    public Server() {
        lobby = Lobby.getInstance(this);
    }


    @Override
    public void run() {
        new Thread(lobby).start();
        while (true) {
            synchronized (lock) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            for(Game game : lobby.gamesLobby){
                new Thread(game).start();
            }
            lobby.gamesLobby.clear();
            System.out.println("Next Game");
        }
    }
}
