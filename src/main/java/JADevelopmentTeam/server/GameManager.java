package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;


class GameManager {
    private Board board;
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
        int validation = GameLogicCalculator.processMove(chosenIntersection,this,turn);
        if(validation==0) return true;
        return false;
    }

    public Board getBoard() {
        return board;
    }

    int processMove(Intersection chosenIntersection, int turn){
        return GameLogicCalculator.processMove(chosenIntersection, this,turn);
    }
    void processStoneAdding(Stone stone, int turn){
        if(turn ==1){
            playerOneStones.add(stone);
        }else{
            playerTwoStones.add(stone);
        }
        addStoneToChains(stone,turn);
        processStonesLiberties();
        removeDeadStoneChains(turn);
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
        processStonesLiberties();
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
        playerOneStoneChains = new ArrayList<>();
        playerTwoStoneChains = new ArrayList<>();
        for(Stone stone : playerOneStones){
            addStoneToChains(stone,1);
        }
        for(Stone stone: playerTwoStones){
            addStoneToChains(stone,0);
        }
        processStonesLiberties();
    }

    void removeDeadStoneChains(int turn) {
        ArrayList<StoneChain> playerStoneChains = null;
        ArrayList<Stone> playerStones = null;
        if (turn == 0) {
            playerStoneChains = playerOneStoneChains;
            playerStones = playerOneStones;
        } else {
            playerStoneChains = playerTwoStoneChains;
            playerStones = playerTwoStones;
        }
        for (int i = playerStoneChains.size() - 1; i >= 0; i--) {
            if (GameLogicCalculator.calculateLiberties(playerStoneChains.get(i), board) == 0) {
                ArrayList<Stone> stones = playerStoneChains.get(i).getStones();
                for (int j =stones.size() - 1; j >= 0; j--) {
                    Stone stone = stones.get(j);
                    board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()].setHasStone(false);
                    playerStones.remove(stone);

                    //getBoardAsIntersections()[stone.getXCoordinate()][stone.getYCoordinate()].setHasStone(false);
                }
                System.out.println("USUWAM chaina");
                playerStoneChains.remove(playerStoneChains.get(i));

            }
        }
        resetStoneChains();
    }

    public void loadBackup(GameManager backup){
        this.board = backup.board;
        this.playerOneStoneChains = backup.playerOneStoneChains;
        this.playerTwoStoneChains = backup.playerTwoStoneChains;
        this.playerOneStones = backup.playerOneStones;
        this.playerTwoStones = backup.playerTwoStones;
    }

    public GameManager copy() {
        GameManager clone = new GameManager(this.getBoard().getSize());
        clone.board = this.board.copy();
        ArrayList<Stone> stones = new ArrayList<>();
        for (Stone original : playerOneStones) {
            Stone copy = new Stone(original.getXCoordinate(), original.getYCoordinate());
            copy.setLiberties(original.getLiberties());
            stones.add(copy);
        }
        clone.playerOneStones = stones;
        stones = new ArrayList<>();
        for (Stone original : playerTwoStones) {
            Stone copy = new Stone(original.getXCoordinate(), original.getYCoordinate());
            copy.setLiberties(original.getLiberties());
            stones.add(copy);
        }
        clone.playerTwoStones = stones;
        ArrayList<StoneChain> chains = new ArrayList<>();
        for (StoneChain originalChain : playerOneStoneChains){
            chains.add(originalChain.copy());
        }
        clone.playerOneStoneChains = chains;
        chains = new ArrayList<>();
        for (StoneChain originalChain : playerTwoStoneChains){
            chains.add(originalChain.copy());
        }
        clone.playerTwoStoneChains = chains;
        return clone;
    }

}
