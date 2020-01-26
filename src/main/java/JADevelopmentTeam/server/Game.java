package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.GameConfig;
import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.common.TerritoryStates;
import JADevelopmentTeam.database.EndGame;
import JADevelopmentTeam.database.Move;
import JADevelopmentTeam.database.MySQLConnector;
import JADevelopmentTeam.server.Bot.Bot;
import JADevelopmentTeam.server.GameLogic.GameManager;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

public class Game implements Runnable {
    private int turn = 0;
    private GameManager gameManager;
    private Player[] players;
    private boolean lastMoveWasPass = false;
    private final Object lock = this;
    private int gameID;
    private Move move;
    Game(Player[] players, int boardSize, int id) {
        this.players = players;
        players[0].setLock(lock);
        players[1].setLock(lock);
        gameManager = new GameManager(boardSize);
        gameID = MySQLConnector.getLastGameID();
        this.gameID = id;
        move = new Move();
        move.setGameID(gameID);
    }

    private String wonInfo(int turn) {
        gameManager.addTerritoryPoints();
        int yourPoints = gameManager.playersPoints[turn] + gameManager.playersTerritoryPoints[turn];
        int opponentPoints = gameManager.playersPoints[Math.abs(turn - 1)] + gameManager.playersTerritoryPoints[Math.abs(turn - 1)];
        String result = "";
        String komi;
        if(gameManager.getBoard().getSize()>12){
            if (turn == 0) {
                komi = "You had komi +6 points\n";
                yourPoints += 6;
            } else {
                komi = "Your opponent had komi +6 points\n";
                opponentPoints += 6;
            }
        }else{
            komi = "";
        }

        if (yourPoints > opponentPoints) {
            result += "You won\n";
        } else if (yourPoints < opponentPoints) {
            result += "You lost\n";
        } else {
            result += "Draw\n";
        }
        result += "Your Prisoners: " + gameManager.playersPoints[turn] + "\n" +
                "Opponent Prisoners: " + gameManager.playersPoints[Math.abs(turn - 1)] + "\n" +
                "Your territory points: " + gameManager.playersTerritoryPoints[turn] + "\n" +
                "Opponent territory points:" + gameManager.playersTerritoryPoints[Math.abs(turn - 1)] + "\n" +
                komi + "\n" +
                "Sum of Your points: " + yourPoints + "\n" +
                "Sum of Opponent points: " + opponentPoints + "\n\n" +
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
        if(!sendTerritory()){
            handlePlayerRunningAway();
            return;
        }
        updatePlayersBoard();
        for (int i = 0; i < 2; i++) {
            try {
                players[i].send(new DataPackage(wonInfo(i), DataPackage.Info.GameResult));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MySQLConnector.sendObject(new EndGame(gameID,gameManager.playersTerritoryPoints[1],gameManager.playersPoints[1],gameManager.playersTerritoryPoints[0],gameManager.playersPoints[0]));
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

    private void notifyPlayerAboutOpponentResignation(Player player) {
        DataPackage dataPackage = new DataPackage("Connection to opponent lost", DataPackage.Info.InfoMessage);
        try {
            player.send(dataPackage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
//    private void sendMove(int x,int y,boolean isPass){
//        if(isPass){
//            move.setPass(true);
//            move.setBlack(turn==1);
//        }else{
//            move.configureMove(x,y,turn==1);
//        }
//        MySQLConnector.sendObject(move);
//    }
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
                move.setPass(true);
                move.setBlack(turn==1);
                if (!notifyOpponentAboutPass(players[Math.abs(turn - 1)])) {
                    handlePlayerRunningAway();
                    return;
                }
                if (lastMoveWasPass) {
                    MySQLConnector.sendObject(move);
                    break;
                }
                lastMoveWasPass = true;
            } else {
                Intersection placedStone = (Intersection) players[turn].getDataPackage().getData();
                int moveResult = gameManager.processMove(placedStone, turn);
                move.configureMove(placedStone.getXCoordinate(),placedStone.getYCoordinate(),turn==1);
                if (moveResult == 0) {
                    lastMoveWasPass = false;
                } else {
                    DataPackage badMoveData;
                    switch (moveResult) {
                        case 2:
                            badMoveData = new DataPackage("Suicidal Move", DataPackage.Info.InfoMessage);
                            break;
                        case 3:
                            badMoveData = new DataPackage("Illegal KO Move", DataPackage.Info.InfoMessage);
                            break;
                        default:
                            badMoveData = new DataPackage("Bad Move", DataPackage.Info.InfoMessage);
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
            MySQLConnector.sendObject(move);
            nextTurn();
        }
        System.out.println("A teraz mili państwo usuwamy kamienie");



        if (!proceedToStoneRemoval()) {
            handlePlayerRunningAway();
            return;
        }
        boolean first = false;
        int playerThatSend = -1;
        do {
            if (!first)
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
                players[0].setReceivedData(false);
                playerThatSend = 0;
            } else if (players[1].getReceivedData()) {
                players[1].setReceivedData(false);
                playerThatSend = 1;
            }
            receivedData = players[playerThatSend].getDataPackage();
            assert receivedData != null;
            if (receivedData.getInfo() == DataPackage.Info.Pass) {
                players[playerThatSend].setAcceptedStones(true);
            } else {
                if (players[playerThatSend].getDataPackage().getData() instanceof Intersection) {
                    Intersection placedStone = (Intersection) players[playerThatSend].getDataPackage().getData();
                    if (gameManager.processDeadDeclaration(placedStone) == 0) {
                        if(!(players[0] instanceof Bot)) {
                            players[0].setAcceptedStones(false);
                        }
                        if(!(players[1] instanceof Bot)) {
                            players[1].setAcceptedStones(false);
                        }
                        if (!updatePlayersBoard()) {
                            handlePlayerRunningAway();

                        }
                    }
                }
            }
            first = false;
        } while (!players[0].isAcceptedStones() || !players[1].isAcceptedStones());
        endGame();
    }
}

