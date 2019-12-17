package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.common.TerritoryStates;

import java.util.ArrayList;


class GameManager {
    ArrayList<Stone> playerOneStones = new ArrayList<>();
    ArrayList<Stone> playerTwoStones = new ArrayList<>();
    ArrayList<StoneChain> playerOneStoneChains = new ArrayList<>();
    ArrayList<StoneChain> playerTwoStoneChains = new ArrayList<>();
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

    public int getPlayerOnePoints() {
        return playerOnePoints;
    }

    public void setPlayerOnePoints(int playerOnePoints) {
        this.playerOnePoints = playerOnePoints;
    }

    public int getPlayerTwoPoints() {
        return playerTwoPoints;
    }

    public void setPlayerTwoPoints(int playerTwoPoints) {
        this.playerTwoPoints = playerTwoPoints;
    }


    GameManager(int boardSize) {
        board = new Board(boardSize);
    }

    Intersection[][] getBoardAsIntersections() {
        return board.getIntersections();
    }


    public Board getBoard() {
        return board;
    }

    int processMove(Intersection chosenIntersection, int turn) {
        if (turn == 1) {
            chosenIntersection.setStoneBlack(false);
        } else {
            chosenIntersection.setStoneBlack(true);
        }
        return GameLogicCalculator.processMove(chosenIntersection, this, turn);
    }

    int processDeadDeclaration(Intersection chosenIntersection) {
        int x = chosenIntersection.getXCoordinate();
        int y = chosenIntersection.getYCoordinate();
        if (board.getIntersections()[x][y].isHasStone()) {
            Stone stone;
            stone = GameLogicCalculator.getStoneForIntersection(board.getIntersections()[x][y], playerOneStones);
            if (stone != null) {
                int stoneChainID = GameLogicCalculator.getStoneChainIDForStone(stone, playerOneStoneChains);
                StoneChain stoneChain = playerOneStoneChains.get(stoneChainID);
                changeChainFlag(stoneChain);
            } else {
                stone = GameLogicCalculator.getStoneForIntersection(board.getIntersections()[x][y], playerTwoStones);
                int stoneChainID = GameLogicCalculator.getStoneChainIDForStone(stone, playerTwoStoneChains);
                StoneChain stoneChain = playerTwoStoneChains.get(stoneChainID);
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
        if (turn == 1) {
            playerOneStones.add(stone);
        } else {
            playerTwoStones.add(stone);
        }
        addStoneToChains(stone, turn);
        processStonesLiberties();
    }

    private void addStoneToChains(Stone stone, int turn) {
        ArrayList<StoneChain> playerStoneChains = null;
        ArrayList<Stone> playerStones = null;
        if (turn == 1) {
            playerStoneChains = playerOneStoneChains;
            playerStones = playerOneStones;
        } else {
            playerStoneChains = playerTwoStoneChains;
            playerStones = playerTwoStones;
        }
        ArrayList<StoneChain> neighborStoneChains = GameLogicCalculator.getNeighborChains(stone, board, playerStoneChains, playerStones);
        if (neighborStoneChains.size() == 0) {
            StoneChain stoneChain = new StoneChain(stone);
            playerStoneChains.add(stoneChain);
        } else if (neighborStoneChains.size() == 1) {
            playerStoneChains.get(playerStoneChains.indexOf(neighborStoneChains.get(0))).addStone(stone);
        } else {
            playerStoneChains.removeAll(neighborStoneChains);
            playerStoneChains.add(GameLogicCalculator.generateSuperStoneChain(neighborStoneChains, stone));
        }
        processStonesLiberties();
    }

    void processStonesLiberties() {
        for (Stone stone : playerOneStones) {
            stone.setLiberties(GameLogicCalculator.calculateLiberties(stone, board));
        }
        for (Stone stone : playerTwoStones) {
            stone.setLiberties(GameLogicCalculator.calculateLiberties(stone, board));
        }
        for (StoneChain stoneChain : playerOneStoneChains) {
            stoneChain.setLiberties(GameLogicCalculator.calculateLiberties(stoneChain, board));
        }
        for (StoneChain stoneChain : playerTwoStoneChains) {
            stoneChain.setLiberties(GameLogicCalculator.calculateLiberties(stoneChain, board));
        }
    }

    void resetStoneChains() {
        playerOneStoneChains = new ArrayList<>();
        playerTwoStoneChains = new ArrayList<>();
        for (Stone stone : playerOneStones) {
            addStoneToChains(stone, 1);
        }
        for (Stone stone : playerTwoStones) {
            addStoneToChains(stone, 0);
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
        lastRemovedStone = null;
        int deleteCounter = 0;
        for (int i = playerStoneChains.size() - 1; i >= 0; i--) {
            if (GameLogicCalculator.calculateLiberties(playerStoneChains.get(i), board) == 0) {
                ArrayList<Stone> stones = playerStoneChains.get(i).getStones();
                if (turn == 0) {
                    playerTwoPoints += stones.size();
                } else {
                    playerOnePoints += stones.size();
                }
                for (int j = stones.size() - 1; j >= 0; j--) {
                    Stone stone = stones.get(j);
                    board.getIntersections()[stone.getXCoordinate()][stone.getYCoordinate()].setHasStone(false);
                    playerStones.remove(stone);
                }
                System.out.println("USUWAM chaina");
                deleteCounter++;
                if (playerStoneChains.get(i).getStones().size() == 1)
                    lastRemovedStone = playerStoneChains.get(i).getStones().get(0);
                playerStoneChains.remove(playerStoneChains.get(i));
            }
            if (deleteCounter != 1) {
                lastRemovedStone = null;
            }
        }
        resetStoneChains();
    }

    public void loadBackup(GameManager backup) {
        this.board = backup.board;
        this.lastRemovedStone = backup.lastRemovedStone;
        this.playerOneStoneChains = backup.playerOneStoneChains;
        this.playerTwoStoneChains = backup.playerTwoStoneChains;
        this.playerOneStones = backup.playerOneStones;
        this.playerTwoStones = backup.playerTwoStones;
        this.playerOnePoints = backup.playerOnePoints;
        this.playerTwoPoints = backup.playerTwoPoints;
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
        for (StoneChain originalChain : playerOneStoneChains) {
            chains.add(originalChain.copy());
        }
        clone.playerOneStoneChains = chains;
        chains = new ArrayList<>();
        for (StoneChain originalChain : playerTwoStoneChains) {
            chains.add(originalChain.copy());
        }
        clone.playerTwoStoneChains = chains;
        clone.playerOnePoints = this.playerOnePoints;
        clone.playerTwoPoints = this.playerTwoPoints;
        return clone;
    }
    public void addTerritoryPoints(){
        Integer [] pointToAdd = countTerritory();
        playerOnePoints += pointToAdd[0];
        playerTwoPoints += pointToAdd[1];
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
