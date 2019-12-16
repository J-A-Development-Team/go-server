package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.common.TerritoryStates;

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

    private boolean updatePlayersBoard() {
        DataPackage boardData = new DataPackage(gameManager.getBoardAsIntersections(), DataPackage.Info.StoneTable);
        DataPackage pointsDataOne = new DataPackage(gameManager.getPlayerOnePoints(), DataPackage.Info.Points);
        DataPackage pointsDataTwo = new DataPackage(gameManager.getPlayerTwoPoints(), DataPackage.Info.Points);

        try {
            players[0].send(boardData);
            players[1].send(pointsDataOne);
            players[1].send(boardData);
            players[0].send(pointsDataTwo);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }

    private void nextTurn() {
        turn = Math.abs(turn - 1);
        players[turn].setPlayerState(Player.PlayerState.Receive);
        players[Math.abs(turn - 1)].setPlayerState(Player.PlayerState.NotYourTurn);
        System.out.println("Players " + turn + " turn");
        players[0].sendTurnInfo();
        players[1].sendTurnInfo();
    }

    private boolean proceedToStoneRemoval() {
        players[0].setPlayerState(Player.PlayerState.Receive);
        players[1].setPlayerState(Player.PlayerState.Receive);
        try {
            players[0].send(new DataPackage("Remove Dead Stones", DataPackage.Info.Turn));
            players[1].send(new DataPackage("Remove Dead Stones", DataPackage.Info.Turn));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean handlePlayerRunningAway() {
        if (!players[0].inGame) {
            notifyPlayerAboutOpponentResignation(players[1]);
            System.out.println("Player 0 disconnected");
        } else if (!players[1].inGame) {
            notifyPlayerAboutOpponentResignation(players[0]);
            System.out.println("Player 1 disconnected");
        } else {
            return true;
        }
        return false;
    }

    private void endGame() {
        gameManager.addTerritoryPoints();
        sendTerritory();
        updatePlayersBoard();
        System.out.println("Ending game");
    }

    private boolean startPlayers() {
        try {
            players[0].send(new DataPackage("black", DataPackage.Info.PlayerColor));
            players[1].send(new DataPackage("white", DataPackage.Info.PlayerColor));
            if(players[0] instanceof Bot){
                new Thread(players[0]).start();
                players[0].inGame = true;
                ((Bot)players[0]).setGameManager(gameManager);
            }else if(players[1] instanceof Bot){
                new Thread(players[1]).start();
                players[1].inGame = true;
                ((Bot)players[1]).setGameManager(gameManager);

            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean sendLastPlacedStone(Intersection lastPlacedStone) {
        DataPackage dataPackage = new DataPackage(lastPlacedStone, DataPackage.Info.Stone);
        try {
            players[0].send(dataPackage);
            players[1].send(dataPackage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;

        }
    }

    private boolean sendTerritory() {
        TerritoryStates[][] territory = gameManager.getTerritories();
        DataPackage dataPackage = new DataPackage(territory, DataPackage.Info.TerritoryTable);
        try {
            players[0].send(dataPackage);
            players[1].send(dataPackage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean notifyOpponentAboutPass(Player player) {
        DataPackage dataPackage = new DataPackage("Opponent passed", DataPackage.Info.Pass);
        try {
            player.send(dataPackage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean notifyPlayerAboutOpponentResignation(Player player) {
        DataPackage dataPackage = new DataPackage("Connection to opponent lost", DataPackage.Info.Info);
        try {
            player.send(dataPackage);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {
        System.out.println("Starting game");
        if (!startPlayers()) {
            handlePlayerRunningAway();
            return;
        }
        while (true) {
            nextTurn();
            synchronized (lock) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!handlePlayerRunningAway()) {
                return;
            }
            DataPackage receivedData = players[turn].getDataPackage();
            if (receivedData.getInfo() == DataPackage.Info.Pass) {
                if (!notifyOpponentAboutPass(players[Math.abs(turn - 1)])) {
                    handlePlayerRunningAway();
                    return;
                }
                if (lastMoveWasPass) {
                    break;
                }
                lastMoveWasPass = true;
                nextTurn();
            } else {
                Intersection placedStone = (Intersection) players[turn].getDataPackage().getData();
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
                if (!updatePlayersBoard()) {
                    handlePlayerRunningAway();
                    return;
                }
                if (!sendLastPlacedStone(placedStone)) {
                    handlePlayerRunningAway();
                    return;
                }
            }
        }
        System.out.println("A teraz mili państwo usuwamy kamienie");
        if (!proceedToStoneRemoval()) {
            handlePlayerRunningAway();
            return;
        }
        while (true) {
            synchronized (lock) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (!handlePlayerRunningAway()) {
                return;
            }
            DataPackage receivedData;
            int playerThatSend;
            if (players[0].receivedData) {
                receivedData = players[0].getDataPackage();
                players[0].receivedData = false;
                playerThatSend = 0;
            } else {
                receivedData = players[1].getDataPackage();
                players[1].receivedData = false;
                playerThatSend = 1;
            }
            if (receivedData.getInfo() == DataPackage.Info.Pass) {
                players[playerThatSend].setAcceptedStones(true);
                if (players[Math.abs(playerThatSend - 1)].isAcceptedStones()) {

                    break;
                }
            } else {
                if (gameManager.processDeadDeclaration((Intersection) receivedData.getData()) == 0) {
                    if (!updatePlayersBoard()) {
                        handlePlayerRunningAway();
                        return;
                    }
                }
            }

        }
        endGame();
    }
}

