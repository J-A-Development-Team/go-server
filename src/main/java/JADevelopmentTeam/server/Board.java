package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Stone;
import JADevelopmentTeam.common.StoneChain;

import java.util.ArrayList;

public class Board {
    private Stone[][] stones;
    private ArrayList <StoneChain> stoneChains;
    private int size;

    public Board(int size) {
        this.size = size;
        stones = new Stone[size][size];
        for(int i=0;i<size;i++){
            for(int j=0;j<size;j++){
                stones[i][j] = new Stone(i,j,false);
            }
        }
        stoneChains = new ArrayList<>();
    }
    boolean isValidMove(Stone stone){
        return true;
    }
    void processMove(Stone stone){
        stones[stone.getXCoordinate()][stone.getYCoordinate()] = stone;
        System.out.println("Magiczne czary");

    }

    Stone [][] getBoardAsStones(){
        return stones;
    }
}
