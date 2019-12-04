package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

class GameManager {
    private Board board;
    private Board previousBoardState;
    ArrayList <Stone> playerOneStones = new ArrayList<>();
    ArrayList <Stone> playerTwoStones = new ArrayList<>();
    ArrayList <StoneChain> playerOneStoneChains = new ArrayList<>();
    ArrayList <StoneChain> playerTwoStoneChains = new ArrayList<>();
    GameManager(int boardSize) {
        board = new Board(boardSize);
    }

    Intersection[][] getBoardAsIntersections(){
        return board.getIntersections();
    }
    boolean isValidMove(Intersection chosenIntersection,int turn){
        int validation = GameLogicCalculator.checkIfMoveIsValid(chosenIntersection,this,turn);
        if(validation==0) return true;
        return false;
    }

    public Board getBoard() {
        return board;
    }

    void processMove(Intersection chosenIntersection, int turn){
        board.setIntersection(chosenIntersection);
        Stone stone = new Stone(chosenIntersection.getXCoordinate(),chosenIntersection.getYCoordinate());
        processStoneAdding(stone,turn);
        System.out.println("Magiczne czary");
        System.out.println(GameLogicCalculator.calculateLiberties(stone,board));
    }
    private void processStoneAdding(Stone stone, int turn){
        if(turn ==1){
            playerOneStones.add(stone);
        }else{
            playerTwoStones.add(stone);
        }
        addStoneToChains(stone,turn);
        processStonesLiberties();
    }
    private void addStoneToChains(Stone stone, int turn){
        ArrayList <StoneChain> playerStoneChains = null;
        ArrayList <Stone> playerStones = null;
        if(turn ==1 ){
            playerStoneChains = playerOneStoneChains;
            playerStones = playerOneStones;
        }else{
            playerStoneChains = playerTwoStoneChains;
            playerStones = playerTwoStones;
        }
        ArrayList <StoneChain> neighborStoneChains = GameLogicCalculator.getNeighborChains(stone,board,playerStoneChains,playerStones);
        if (neighborStoneChains.size()==0){
            StoneChain stoneChain = new StoneChain(stone);
            playerStoneChains.add(stoneChain);
        }else if(neighborStoneChains.size()==1){
            playerStoneChains.get(playerStoneChains.indexOf(neighborStoneChains.get(0))).addStone(stone);
        }else{
            playerStoneChains.removeAll(neighborStoneChains);
            playerStoneChains.add(GameLogicCalculator.generateSuperStoneChain(neighborStoneChains,stone));
        }
    }
    void processStonesLiberties(){
        for(Stone stone : playerOneStones){
            stone.setLiberties(GameLogicCalculator.calculateLiberties(stone,board));
        }
        for(Stone stone : playerTwoStones){
            stone.setLiberties(GameLogicCalculator.calculateLiberties(stone,board));
        }
        for(StoneChain stoneChain : playerOneStoneChains){
            stoneChain.setLiberties(GameLogicCalculator.calculateLiberties(stoneChain,board));
        }
        for(StoneChain stoneChain : playerTwoStoneChains){
            stoneChain.setLiberties(GameLogicCalculator.calculateLiberties(stoneChain,board));
        }
    }
    void resetStoneChains(){
        playerOneStoneChains.clear();
        playerTwoStoneChains.clear();
        for(Stone stone : playerOneStones){
            addStoneToChains(stone,1);
        }
        for(Stone stone: playerTwoStones){
            addStoneToChains(stone,0);
        }
    }
}
