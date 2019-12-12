package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;

import java.io.IOException;

public class Game implements Runnable {
    private int turn = 1;
    private GameManager gameManager;
    private Player[] players;
    private boolean lastMoveWasPass = false;
    private Object lock = this;


    Game(Player[] players, int boardSize) {
        this.players = players;
        players[0].setLock(lock);
        players[1].setLock(lock);
        gameManager = new GameManager(boardSize);
    }

    private void updatePlayersBoard() {
        DataPackage boardData = new DataPackage(gameManager.getBoardAsIntersections(), DataPackage.Info.StoneTable);
        DataPackage pointsDataOne = new DataPackage(gameManager.getPlayersOnePoints(), DataPackage.Info.Points);
        DataPackage pointsDataTwo = new DataPackage(gameManager.getPlayerTwoPoints(), DataPackage.Info.Points);

        try {
            players[0].send(boardData);
            players[1].send(pointsDataOne);
            players[1].send(boardData);
            players[0].send(pointsDataTwo);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void nextTurn() {
        turn = Math.abs(turn - 1);
        players[turn].setPlayerState(Player.PlayerState.Receive);
        players[Math.abs(turn - 1)].setPlayerState(Player.PlayerState.NotYourTurn);
        System.out.println("Players " + turn + " turn");
        try {
            players[turn].send(new DataPackage("Your turn", DataPackage.Info.Turn));
            players[Math.abs(turn - 1)].send(new DataPackage("Not your turn", DataPackage.Info.Turn));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void proceedToEndGame() {
        players[0].setPlayerState(Player.PlayerState.Receive);
        players[1].setPlayerState(Player.PlayerState.Receive);
        try {
            players[0].send(new DataPackage("Remove Dead Stones", DataPackage.Info.Turn));
            players[1].send(new DataPackage("Remove Dead Stones", DataPackage.Info.Turn));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endGame() {
        System.out.println("Ending game");
    }

    private void startPlayers() {
        new Thread(players[0]).start();
        new Thread(players[1]).start();
        try {
            players[0].send(new DataPackage("black", DataPackage.Info.PlayerColor));
            players[1].send(new DataPackage("white", DataPackage.Info.PlayerColor));
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                Intersection placedStone = (Intersection) players[turn].getDataPackage().getData();
                if (turn == 1) {
                    placedStone.setStoneBlack(false);
                } else {
                    placedStone.setStoneBlack(true);
                }
                int moveResult = gameManager.processMove(placedStone, turn);
                if (moveResult == 0) {
                    lastMoveWasPass = false;
                } else {
                    DataPackage badMoveData = null;
                    switch (moveResult) {
                        case 2:
                            badMoveData = new DataPackage("Suicidal Move", DataPackage.Info.Info);
                            break;
                        case 3:
                            badMoveData = new DataPackage("Illegal KO Move", DataPackage.Info.Info);
                            break;
                        default:
                            badMoveData = new DataPackage("Bad Move", DataPackage.Info.Info);
                            break;
                    }
                    try {
                        players[turn].send(badMoveData);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                updatePlayersBoard();
                nextTurn();
            }
        }
        System.out.println("A teraz mili państwo usuwamy kamienie");
        proceedToEndGame();
        while (true) {
            synchronized (lock) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                DataPackage receivedData;
                int playerThatSend;
                if (players[0].receivedData) {
                    receivedData = players[0].getDataPackage();
                    playerThatSend = 0;
                } else {
                    receivedData = players[1].getDataPackage();
                    playerThatSend = 1;
                }
                if (receivedData.getInfo() == DataPackage.Info.Pass) {
                    players[playerThatSend].setAcceptedStones(true);
                    if (players[Math.abs(playerThatSend - 1)].isAcceptedStones()) {
                        break;
                    }
                } else {
                    if (gameManager.processDeadDeclaration((Intersection) receivedData.getData()) == 0) {
                        updatePlayersBoard();
                    }
                }
            }
        }
        endGame();
    }
}

