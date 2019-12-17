package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.common.TerritoryStates;

import java.util.ArrayList;


class GameManager {
    ArrayList< ArrayList<StoneChain>>  playersStoneChains = new ArrayList<>();
    ArrayList< ArrayList <Stone>> playersStones = new ArrayList<>();
    int [] playersPoints = new int[2];
    TerritoryStates[][] territories = null;
    int playerOnePoints = 0;
    int playerTwoPoints = 0;
    Stone lastRemovedStone = null;
    private Board board;

    public TerritoryStates[][] getTerritories() {
        return territories;
    }

    public void setTerritories(TerritoryStates[][] territories) {
        this.territories = territories;
    }

    GameManager(int boardSize) {
        board = new Board(boardSize);
        playersStoneChains.add(new ArrayList<>());
        playersStoneChains.add(new ArrayList<>());
        playersStones.add(new ArrayList<>());
        playersStones.add(new ArrayList<>());
    }

    Intersection[][] getBoardAsIntersections() {
        return board.getIntersections();
    }


    public Board getBoard() {
        return board;
    }

    int processMove(Intersection chosenIntersection, int turn) {
        if (turn == 1) {
            chosenIntersection.setStoneBlack(true);
        } else {
            chosenIntersection.setStoneBlack(false);
        }
        return GameLogicCalculator.processMove(chosenIntersection, this, turn);
    }

    int processDeadDeclaration(Intersection chosenIntersection) {
        int x = chosenIntersection.getXCoordinate();
        int y = chosenIntersection.getYCoordinate();
        if (board.getIntersections()[x][y].isHasStone()) {
            Stone stone;
            stone = GameLogicCalculator.getStoneForIntersection(board.getIntersections()[x][y], playersStones.get(0));
            if (stone != null) {
                int stoneChainID = GameLogicCalculator.getStoneChainIDForStone(stone, playersStoneChains.get(0));
                StoneChain stoneChain = playersStoneChains.get(0).get(stoneChainID);
                changeChainFlag(stoneChain);
            } else {
                stone = GameLogicCalculator.getStoneForIntersection(board.getIntersections()[x][y], playersStones.get(1));
                int stoneChainID = GameLogicCalculator.getStoneChainIDForStone(stone, playersStoneChains.get(1));
                StoneChain stoneChain = playersStoneChains.get(1).get(stoneChainID);
                changeChainFlag(stoneChain);
            }
            return 0;
        } else {
            return 1;
        }
    }

    void changeChainFlag(StoneChain chainToFlag) {
        if (!chainToFlag.isDead()) {
            setChainFlag(chainToFlag, true);
        } else {
            setChainFlag(chainToFlag, false);
        }
    }

    void setChainFlag(StoneChain chainToMakeFlag, boolean flag) {
        ArrayList<Intersection> chainIntersections = new ArrayList<>();
        for (Stone stone : chainToMakeFlag.getStones()) {
            chainIntersections.add(GameLogicCalculator.getIntersectionForStone(stone, board));
        }
        for (Intersection intersection : chainIntersections) {
            intersection.setStoneDead(flag);
        }
        chainToMakeFlag.setDead(flag);
    }

    void processStoneAdding(Stone stone, int turn) {
        playersStones.get(turn).add(stone);
        addStoneToChains(stone, turn);
        processStonesLiberties();
    }

    private void addStoneToChains(Stone stone, int turn) {
        ArrayList<StoneChain> neighborStoneChains = GameLogicCalculator.getNeighborChains(stone, board, playersStoneChains.get(turn), playersStones.get(turn));
        if (neighborStoneChains.size() == 0) {
            StoneChain stoneChain = new StoneChain(stone);
            playersStoneChains.get(turn).add(stoneChain);
        } else if (neighborStoneChains.size() == 1) {
            playersStoneChains.get(turn).get(playersStoneChains.get(turn).indexOf(neighborStoneChains.get(0))).addStone(stone);
        } else {
            playersStoneChains.get(turn).removeAll(neighborStoneChains);
            playersStoneChains.get(turn).add(GameLogicCalculator.generateSuperStoneChain(neighborStoneChains, stone));
        }
        processStonesLiberties();
    }

    void processStonesLiberties() {
        for (Stone stone : playersStones.get(0)) {
            stone.setLiberties(GameLogicCalculator.calculateLiberties(stone, board));
        }
        for (Stone stone : playersStones.get(1)) {
            stone.setLiberties(GameLogicCalculator.calculateLiberties(stone, board));
        }
        for (StoneChain stoneChain : playersStoneChains.get(0)) {
            stoneChain.setLiberties(GameLogicCalculator.calculateLiberties(stoneChain, board));
        }
        for (StoneChain stoneChain : playersStoneChains.get(1)) {
            stoneChain.setLiberties(GameLogicCalculator.calculateLiberties(stoneChain, board));
        }
    }

    void resetStoneChains() {
        playersStoneChains.set(0,new ArrayList<>()) ;
        playersStoneChains.set(1,new ArrayList<>()) ;
        for (Stone stone : playersStones.get(0)) {
            addStoneToChains(stone, 0);
        }
        for (Stone stone : playersStones.get(1)) {
            addStoneToChains(stone, 1);
        }
        processStonesLiberties();
    }

    void removeDeadStoneChains(int turn) {
        lastRemovedStone = null;
        int deleteCounter = 0;
        int opponent = Math.abs(turn-1);
        for (int i = playersStoneChains.get(opponent).size() - 1; i >= 0; i--) {
            if (GameLogicCalculator.calculateLiberties(playersStoneChains.get(opponent).get(i), board) == 0) {
                ArrayList<Stone> stones = playersStoneChains.get(opponent).get(i).getStones();
                playersPoints[turn] +=stones.size();
                for (int j = stones.size() - 1; j >= 0; j--) {
                    Stone stone = stones.get(j);
                    board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()].setHasStone(false);
                    playersStones.get(opponent).remove(stone);
                }
                deleteCounter++;
                if (playersStoneChains.get(opponent).get(i).getStones().size() == 1)
                    lastRemovedStone = playersStoneChains.get(opponent).get(i).getStones().get(0);
                playersStoneChains.get(opponent).remove(i);
            }
            if (deleteCounter != 1) {
                lastRemovedStone = null;
            }
        }
        resetStoneChains();
    }

    public void loadBackup(GameManager backup) {
        this.board = backup.board.copy();
        this.lastRemovedStone = backup.lastRemovedStone;
        this.playersStoneChains = backup.playersStoneChains;
        this.playersStones = backup.playersStones;
    }

    public GameManager copy() {
        GameManager clone = new GameManager(this.getBoard().getSize());
        clone.board = this.board.copy();
        if (this.lastRemovedStone != null){
            clone.lastRemovedStone = new Stone(this.lastRemovedStone.getXCoordinate(),this.lastRemovedStone.getYCoordinate());
        } else {
            clone.lastRemovedStone = null;
        }
        ArrayList<Stone> stones = new ArrayList<>();
        for (Stone original : playersStones.get(0)) {
            Stone copy = new Stone(original.getXCoordinate(), original.getYCoordinate());
            copy.setLiberties(original.getLiberties());
            stones.add(copy);
        }
        clone.playersStones.set(0,stones);
        stones = new ArrayList<>();
        for (Stone original : playersStones.get(1)) {
            Stone copy = new Stone(original.getXCoordinate(), original.getYCoordinate());
            copy.setLiberties(original.getLiberties());
            stones.add(copy);
        }
        clone.playersStones.set(1,stones);
        clone.playersPoints[0] = this.playersPoints[0];
        clone.playersPoints[1] = this.playersPoints[1];
        clone.resetStoneChains();
        return clone;
    }
    public void addTerritoryPoints(){
        Integer [] pointToAdd = countTerritory();
        playersPoints[0] += pointToAdd[0];
        playersPoints[1] += pointToAdd[1];
    }
    public Integer[] countTerritory() {
        Integer[] counter = new Integer[2];
        counter[0]=0;
        counter[1]=0;
        territories = TerritoryCalculator.calculateTerritory(board.getIntersections(), board.getSize());
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                switch (territories[j][i]){
                    case White:
                        System.out.print(" 0 ");
                        break;
                    case WhiteTerritory:
                        counter[0]++;
                        System.out.print(" W ");
                        break;
                    case BlackTerritory:
                        counter[1]++;
                        System.out.print(" B ");
                        break;
                    case Black:
                        System.out.print(" 1 ");
                        break;
                    case Verified:
                        System.out.print(" V ");
                        break;
                    case None:
                        System.out.print(" N ");
                        break;
                    case Unknown:
                        System.out.print(" U ");
                        break;
                    case ProbablyBlack:
                        System.out.print(" A ");
                        break;
                    case ProbablyWhite:
                        System.out.println(" Q ");
                        break;
                }
            }
            System.out.print("\n");

        }
        return counter;
    }
}
