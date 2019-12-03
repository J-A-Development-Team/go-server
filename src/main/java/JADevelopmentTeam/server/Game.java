package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Stone;

import java.io.IOException;

public class Game implements Runnable {
    private int turn = 1;
    private Board board;
    private Player[] players;
    private boolean lastMoveWasPass = false;
    private Object lock = this;


    Game(Player[] players, int boardSize) {
        this.players = players;
        players[0].setLock(lock);
        players[1].setLock(lock);
        board = new Board(boardSize);
    }

    private void updatePlayersBoard() {
        DataPackage dataToSend = new DataPackage(board.getBoardAsStones(), DataPackage.Info.StoneTable);
        try {
            players[0].send(dataToSend);
            players[1].send(dataToSend);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void nextTurn() {
        turn = Math.abs(turn - 1);
        players[turn].setPlayerState(Player.PlayerState.Receive);
        players[Math.abs(turn - 1)].setPlayerState(Player.PlayerState.NotYourTurn);
        System.out.println("Players " + turn + " turn");
    }

    private void endGame() {
        System.out.println("Ending game");
    }
    private void startPlayers(){
        new Thread(players[0]).start();
        new Thread(players[1]).start();

    }
    @Override
    public void run() {
        System.out.println("Starting game");
        nextTurn();
        startPlayers();
        while (true) {
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            DataPackage receivedData = players[turn].getDataPackage();
            if (receivedData.getInfo() == DataPackage.Info.Pass) {
                if (lastMoveWasPass) {
                    break;
                }
                lastMoveWasPass = true;
                nextTurn();
            } else {
                Stone placedStone = (Stone) players[turn].getDataPackage().getData();

                if (board.isValidMove(placedStone)) {
                    lastMoveWasPass = false;
                    board.processMove(placedStone);
                } else {
                    try {
                        players[turn].send(new DataPackage("Not valid move",DataPackage.Info.Info));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                updatePlayersBoard();
                nextTurn();
            }

        }
        endGame();
    }
}

