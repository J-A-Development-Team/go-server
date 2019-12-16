package JADevelopmentTeam.server;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;
import java.util.Random;

public abstract class BotBrain {
    public static DataPackage getOptimalMove(GameManager gameManager,boolean isBlack){
        GameManager backup = gameManager.copy();
        ArrayList <Intersection> possibleMoves = getPossibleMoves(gameManager,isBlack,backup);
        if(possibleMoves.size()==0) return getPassDataPackage();
        return getIntersectionDataPackage(getRandomMove(possibleMoves));
    }
    private static  Intersection getRandomMove(ArrayList <Intersection> possibleMoves){
        Random r = new Random();
        int randomIndex =  r.nextInt(possibleMoves.size());
        return possibleMoves.get(randomIndex);
    }
    private static void restoreGameManager(GameManager gameManager, GameManager backup){
        gameManager.loadBackup(backup);
    }
    private static DataPackage getPassDataPackage(){
        return new DataPackage(null, DataPackage.Info.Pass);
    }
    private static DataPackage getIntersectionDataPackage(Intersection intersection){
        return new DataPackage(intersection, DataPackage.Info.Stone);
    }
    private static ArrayList<Intersection> getPossibleMoves(GameManager gameManager, boolean isBlack,GameManager backup){
        ArrayList <Intersection> possibleMoves = new ArrayList<>();
        int turn=1;
        if(isBlack) turn = 0;
        for(int i =0; i<gameManager.getBoard().getSize();i++){
            for(int j=0; j<gameManager.getBoard().getSize();j++){
                Intersection intersection = new Intersection(i,j);
                intersection.setStoneBlack(isBlack);
                if(GameLogicCalculator.processMove(intersection,gameManager,turn)==0){
                    possibleMoves.add(intersection);
                }
                gameManager.loadBackup(backup.copy());

            }
        }
        return possibleMoves;
    }
}
