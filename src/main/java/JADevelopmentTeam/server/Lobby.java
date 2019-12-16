package JADevelopmentTeam.server;

import java.util.ArrayList;
import java.util.Stack;

public class Lobby implements Runnable {
    private static Lobby instance = null;
    Stack<Player> playersWaitingFor9X9Game = new Stack<>();
    Stack<Player> playersWaitingFor5X5Game = new Stack<>();
    Stack<Player> playersWaitingFor13X13Game = new Stack<>();
    Stack<Player> playersWaitingFor9X9GameWithBot = new Stack<>();
    Stack<Player> playersWaitingFor5X5GameWithBot = new Stack<>();
    Stack<Player> playersWaitingFor13X13GameWithBot = new Stack<>();
    ArrayList<Player> playersChilling = new ArrayList<>();
    ArrayList<Game> gamesLobby = new ArrayList<>();
    Object lock = null;
    Connector connector;
    private Lobby(Object lock) {
        this.lock = lock;
        connector = Connector.getInstance();
    }

    static Lobby getInstance(Object lock) {
        if (instance == null) {
            instance = new Lobby(lock);
        }
        return instance;
    }

    public void addPlayerToLobby(Player player) {
        playersChilling.add(player);
    }

    Game prepareGame(Player[] players, int boardSize) {
        Game game = new Game(players, boardSize);
        return game;
    }

    private void processChillingPlayers() {
        for (Player chillingPlayer : playersChilling) {
            if (chillingPlayer.getPlayerState() == Player.PlayerState.WaitForStart) {
                switch (chillingPlayer.getGameConfig().getBoardSize()) {
                    case 5:
                        if (chillingPlayer.getGameConfig().isWithBot()) {
                            playersWaitingFor5X5GameWithBot.push(chillingPlayer);
                        } else {
                            playersWaitingFor5X5Game.push(chillingPlayer);
                        }
                        break;
                    case 9:
                        if (chillingPlayer.getGameConfig().isWithBot()) {
                            playersWaitingFor9X9GameWithBot.push(chillingPlayer);
                        } else {
                            playersWaitingFor9X9Game.push(chillingPlayer);
                        }
                        break;
                    case 13:
                        if (chillingPlayer.getGameConfig().isWithBot()) {
                            playersWaitingFor13X13GameWithBot.push(chillingPlayer);
                        } else {
                            playersWaitingFor13X13Game.push(chillingPlayer);
                        }
                        break;
                }
            }
        }
        for (int i = playersChilling.size() - 1; i >= 0; i--) {
            if (playersChilling.get(i).getPlayerState() == Player.PlayerState.WaitForStart) {
                playersChilling.remove(i);
            }
        }
    }

    private void groupPlayersIntoGames() {
        if (playersWaitingFor5X5Game.size() > 1) {
            Player[] players = new Player[2];
            players[0] = playersWaitingFor5X5Game.pop();
            players[1] = playersWaitingFor5X5Game.pop();
            gamesLobby.add(prepareGame(players, 5));
        }
        if (playersWaitingFor9X9Game.size() > 1) {
            Player[] players = new Player[2];
            players[0] = playersWaitingFor9X9Game.pop();
            players[1] = playersWaitingFor9X9Game.pop();
            gamesLobby.add(prepareGame(players, 9));
        }
        if (playersWaitingFor13X13Game.size() > 1) {
            Player[] players = new Player[2];
            players[0] = playersWaitingFor13X13Game.pop();
            players[1] = playersWaitingFor13X13Game.pop();
            gamesLobby.add(prepareGame(players, 13));
        }
        if (playersWaitingFor5X5GameWithBot.size() > 0) {
            Player[] players = new Player[2];
            players[0] = playersWaitingFor5X5GameWithBot.pop();
            players[1] = new Bot(null);
            gamesLobby.add(prepareGame(players, 5));
        }
        if (playersWaitingFor9X9GameWithBot.size() > 0) {
            Player[] players = new Player[2];
            players[0] = playersWaitingFor9X9GameWithBot.pop();
            players[1] = new Bot(null);
            gamesLobby.add(prepareGame(players, 9));
        }
        if (playersWaitingFor13X13GameWithBot.size() > 0) {
            Player[] players = new Player[2];
            players[0] = playersWaitingFor13X13GameWithBot.pop();
            players[1] = new Bot(null);
            gamesLobby.add(prepareGame(players, 13));
        }
    }

    @Override
    public void run() {
        while (true) {
            processChillingPlayers();
            groupPlayersIntoGames();
            if(gamesLobby.size()>0){
                synchronized (lock){
                    lock.notify();
                }
            }
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
