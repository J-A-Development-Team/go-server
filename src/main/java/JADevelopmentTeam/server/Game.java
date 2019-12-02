package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Stone;

import java.io.IOException;

public class Game implements Runnable {
    private int turn = 1;
    private Board board;
    private Player[] players;
    private boolean lastMoveWasPass = false;

    Game(Player[] players, int boardSize) {
        this.players = players;
        players[0].setContext(this);
        players[1].setContext(this);
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
        players[0].run();
        players[1].run();

    }
    @Override
    public void run() {
        startPlayers();
        System.out.println("Starting game");
        nextTurn();
        while (true) {
            synchronized (this) {
                try {
                    wait();
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

