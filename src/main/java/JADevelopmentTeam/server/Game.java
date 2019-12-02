package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Stone;

import java.io.IOException;

public class Game implements Runnable {
    private int turn = 1;
    private Board board;
    private Player[] players;
    private boolean lastMoveWasPass = false;

    Game(Player[] players) {
        this.players = players;
        players[0].setContext(this);
        players[1].setContext(this);
        board = new Board(9);
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
        if (turn == 0) {
            turn = 1;
        } else {
            turn = 0;
        }
        players[turn].setPlayerState(Player.PlayerState.Receive);
        players[turn].setPlayerState(Player.PlayerState.NotYourTurn);
    }
    private void endGame(){
        System.out.println("Ending game");
    }
    @Override
    public void run() {
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
            if(receivedData.getInfo()== DataPackage.Info.Pass){
                if(lastMoveWasPass){
                    break;
                }
                lastMoveWasPass = true;
                nextTurn();
            }
            Stone placedStone = (Stone) players[turn].getDataPackage().getData();
            if (board.isValidMove(placedStone)) {
                lastMoveWasPass = false;
                board.processMove(placedStone);
            } else {
                continue;
            }
            updatePlayersBoard();
            nextTurn();
        }
        endGame();
    }
}

