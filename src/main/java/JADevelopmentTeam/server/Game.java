package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.common.TerritoryStates;

import java.io.IOException;

public class Game implements Runnable {
    private int turn = 0;
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

    private String wonInfo(int turn) {
        gameManager.addTerritoryPoints();
        int yourPoints = gameManager.playersPoints[turn] + gameManager.playersTerritoryPoints[turn];
        int opponentPoints = gameManager.playersPoints[Math.abs(turn - 1)] + gameManager.playersTerritoryPoints[Math.abs(turn - 1)];
        String result = "";
        String komi;
        if (turn == 0) {
            komi = "You had komi +6 points\n";
        } else {
            komi = "Your opponent had komi +6 points\n";
        }
        if (yourPoints > opponentPoints) {
            result += "You won\n";
        } else if (yourPoints<opponentPoints){
            result += "You lost\n";
        } else {
            result += "Draw";
        }
        result += "Your points: " + gameManager.playersPoints[turn] + "\n" +
                "Opponent points: " + gameManager.playersPoints[Math.abs(turn - 1)] + "\n" +
                "Your territory points: " + gameManager.playersTerritoryPoints[turn] + "\n" +
                "Opponent territory points:" + gameManager.playersTerritoryPoints[Math.abs(turn - 1)] + "\n" +
                komi + "\n" +
                "Sum of Your points: " + yourPoints + "\n" +
                "Sum of Opponent points" + opponentPoints + "\n" +
                "Thanks for playing\n" +
                "Artur Pazurkiewicz & Joachim Schmidt";
        return result;
    }

    private boolean updatePlayersBoard() {
        DataPackage boardData = new DataPackage(gameManager.getBoardAsIntersections(), DataPackage.Info.StoneTable);
        DataPackage pointsDataOne = new DataPackage(gameManager.playersPoints[0], DataPackage.Info.Points);
        DataPackage pointsDataTwo = new DataPackage(gameManager.playersPoints[1], DataPackage.Info.Points);

        try {
            players[0].send(boardData);
            players[0].send(pointsDataOne);
            players[1].send(boardData);
            players[1].send(pointsDataTwo);
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
        if (!players[0].getInGame()) {
            notifyPlayerAboutOpponentResignation(players[1]);
            System.out.println("Player 0 disconnected");
        } else if (!players[1].getInGame()) {
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
        for (int i = 0; i < 2; i++) {
            try {
                players[i].send(new DataPackage(wonInfo(i), DataPackage.Info.GameResult));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Ending game");
    }

    private boolean startPlayers() {
        try {
            players[1].send(new DataPackage("black", DataPackage.Info.PlayerColor));
            players[0].send(new DataPackage("white", DataPackage.Info.PlayerColor));
            if (players[0] instanceof Bot) {
                new Thread(players[0]).start();
                players[0].setInGame(true);
                ((Bot) players[0]).setGameManager(gameManager);
            } else if (players[1] instanceof Bot) {
                new Thread(players[1]).start();
                players[1].setInGame(true);
                ((Bot) players[1]).setGameManager(gameManager);

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
        nextTurn();
        if (!startPlayers()) {
            handlePlayerRunningAway();
            return;
        }
        while (true) {
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
                nextTurn();
            }
        }
        System.out.println("A teraz mili państwo usuwamy kamienie");
        if (!proceedToStoneRemoval()) {
            handlePlayerRunningAway();
            return;
        }
        int playerThatSend = -1;
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
            DataPackage receivedData = null;
            if (players[0].getReceivedData()) {
                receivedData = players[0].getDataPackage();
                players[0].setReceivedData(false);
                playerThatSend = 0;
            } else if (players[1].getReceivedData()) {
                receivedData = players[1].getDataPackage();
                players[1].setReceivedData(false);
                playerThatSend = 1;
            }
            assert receivedData != null;
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

