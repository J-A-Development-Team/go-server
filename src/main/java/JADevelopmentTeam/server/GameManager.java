package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

class GameManager {
    private Board board;
    ArrayList <Stone> stones = new ArrayList<>();
    ArrayList <StoneChain> PlayerOneStoneChains = new ArrayList<>();
    ArrayList <StoneChain> PlayerTwoStoneChains = new ArrayList<>();
    GameManager(int boardSize) {
        board = new Board(boardSize);

    }

    Intersection[][] getBoardAsIntersections(){
        return board.getIntersections();
    }
    boolean isValidMove(Intersection chosenIntersection,int turn){

        return true;
    }
    void processMove(Intersection chosenIntersection,int turn){
        board.setIntersection(chosenIntersection);
        Stone stone = new Stone(chosenIntersection.getXCoordinate(),chosenIntersection.getYCoordinate());
        stones.add(stone);
        System.out.println("Magiczne czary");
        System.out.println(GameLogicCalculator.calculateLiberties(stone,board));
    }




}
