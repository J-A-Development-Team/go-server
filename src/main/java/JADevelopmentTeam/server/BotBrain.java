package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;
import java.util.Random;

public abstract class BotBrain {
    public static DataPackage getOptimalMove(GameManager gameManager, boolean isBlack) {
        GameManager backup = gameManager.copy();
        int turn = 0;
        if (isBlack) turn = 1;
        ArrayList<Move> possibleMoves = getPossibleMoves(gameManager, turn, backup);
        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        getMovesWithHighestGainInTerritory(gameManager, turn, backup, possibleMoves);
        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        if (possibleMoves.size() == 1)
            return getIntersectionDataPackage(possibleMoves.get(0));
        getMovesWithHighestKill(gameManager, turn, backup, possibleMoves);
        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        if (possibleMoves.size() == 1)
            return getIntersectionDataPackage(possibleMoves.get(0));
        getMovesWithMostLibertiesTakenFromOpponent(gameManager,turn,backup,possibleMoves);

        getMovesWithBestConnection(gameManager, turn, backup, possibleMoves);
        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        if (possibleMoves.size() == 1)
            return getIntersectionDataPackage(possibleMoves.get(0));
        getMovesWithBestConnection(gameManager, turn, backup, possibleMoves);

        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        if (possibleMoves.size() == 1)
            return getIntersectionDataPackage(possibleMoves.get(0));
        getMovesWithHighestGainInLiberties(gameManager, turn, backup, possibleMoves);
        if (possibleMoves.size() == 0)
            return getPassDataPackage();
        if (possibleMoves.size() == 1)
            return getIntersectionDataPackage(possibleMoves.get(0));
        return getIntersectionDataPackage(getRandomMove(possibleMoves));
    }

    private static Move getRandomMove(ArrayList<Move> possibleMoves) {
        Random r = new Random();
        int randomIndex = r.nextInt(possibleMoves.size());
        return possibleMoves.get(randomIndex);
    }

    private static DataPackage getPassDataPackage() {
        return new DataPackage(null, DataPackage.Info.Pass);
    }

    private static DataPackage getIntersectionDataPackage(Move move) {
        return new DataPackage(move.intersection, DataPackage.Info.Stone);
    }

    public static ArrayList<Move> getPossibleMoves(GameManager gameManager, int turn, GameManager backup) {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        for (int i = 0; i < gameManager.getBoard().getSize(); i++) {
            for (int j = 0; j < gameManager.getBoard().getSize(); j++) {
                Intersection intersection = new Intersection(i, j);
                if (gameManager.processMove(intersection, turn) == 0) {
                    Move move = new Move();
                    move.intersection = intersection;
                    possibleMoves.add(move);
                }
                gameManager.loadBackup(backup.copy());

            }
        }
        return possibleMoves;
    }

    private static void getMovesWithHighestGainInTerritory(GameManager gameManager, int turn, GameManager backup, ArrayList<Move> possibleMoves) {
        int oldTerritory = gameManager.countTerritory()[turn];
        int max = 0;
        for (Move move : possibleMoves) {
            gameManager.processMove(move.intersection, turn);
            int newTerritory = gameManager.countTerritory()[turn];
            gameManager.loadBackup(backup.copy());
            move.territoryChange = newTerritory - oldTerritory;
            if(move.territoryChange > max){
                max = move.territoryChange;
            }
        }
        for(int i =possibleMoves.size()-1;i>=0;i--){
            if(possibleMoves.get(i).territoryChange<max){
                possibleMoves.remove(i);
            }
        }
    }
    private static void getMovesWithHighestKill(GameManager gameManager, int turn, GameManager backup, ArrayList<Move> possibleMoves){
        int oldPoints = gameManager.playersPoints[turn];
        int max = oldPoints;
        for (Move move : possibleMoves) {
            gameManager.processMove(move.intersection, turn);
            int newPoints = gameManager.playersPoints[turn];
            gameManager.loadBackup(backup.copy());
            move.stonesKilled = newPoints - oldPoints;
            if(move.stonesKilled > max){
                max = move.stonesKilled;
            }
        }
        for(int i =possibleMoves.size()-1;i>=0;i--){
            if(possibleMoves.get(i).stonesKilled<max){
                possibleMoves.remove(i);
            }
        }
    }
    private static void getMovesWithBestConnection(GameManager gameManager, int turn, GameManager backup, ArrayList<Move> possibleMoves){
        int oldChainCount = gameManager.playersStoneChains.get(turn).size();
        int max = -1;
        for (Move move : possibleMoves) {
            gameManager.processMove(move.intersection, turn);
            int newChainsCount = gameManager.playersStoneChains.get(turn).size();
            gameManager.loadBackup(backup.copy());
            move.chainsConnected = oldChainCount - newChainsCount;
            if(move.chainsConnected > max){
                max = move.chainsConnected;
            }
        }
        for(int i =possibleMoves.size()-1;i>=0;i--){
            if(possibleMoves.get(i).chainsConnected<max){
                possibleMoves.remove(i);
            }
        }
    }
    private static void getMovesWithHighestGainInLiberties(GameManager gameManager, int turn, GameManager backup, ArrayList<Move> possibleMoves){
        int oldLiberties = GameLogicCalculator.getSumOfLiberties(gameManager,turn);
        int max = -10;
        for (Move move : possibleMoves) {
            gameManager.processMove(move.intersection, turn);
            int newLibertiesCount = GameLogicCalculator.getSumOfLiberties(gameManager,turn);
            gameManager.loadBackup(backup.copy());
            move.libertiesChange = newLibertiesCount - oldLiberties;
            if(move.libertiesChange > max){
                max = move.libertiesChange;
            }
        }
        for(int i =possibleMoves.size()-1;i>=0;i--){
            if(possibleMoves.get(i).libertiesChange<max){
                possibleMoves.remove(i);
            }
        }
    }
    private static void getMovesWithMostLibertiesTakenFromOpponent(GameManager gameManager, int turn, GameManager backup, ArrayList<Move> possibleMoves){
        int opponent = Math.abs(turn-1);
        int oldOpponentLiberties = GameLogicCalculator.getSumOfLiberties(gameManager,opponent);
        int max = 0;
        for (Move move : possibleMoves) {
            gameManager.processMove(move.intersection, turn);
            int newLibertiesCount = GameLogicCalculator.getSumOfLiberties(gameManager,opponent);
            gameManager.loadBackup(backup.copy());
            move.libertiesTaken = oldOpponentLiberties - newLibertiesCount;
            if(move.libertiesChange > max){
                max = move.libertiesChange;
            }
        }
        for(int i =possibleMoves.size()-1;i>=0;i--){
            if(possibleMoves.get(i).libertiesChange<max){
                possibleMoves.remove(i);
            }
        }
    }
}
