package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public class StoneChain {
    private ArrayList <Stone> stones = new ArrayList<>();
    private int liberties;

    public StoneChain(ArrayList<Stone> stones, int liberties) {
        this.stones = stones;
        this.liberties = liberties;
    }
    void addStone (Stone stone){
        stones.add(stone);
    }


    public int getLiberties() {
        return liberties;
    }

    public void setLiberties(int liberties) {
        this.liberties = liberties;
    }

    public ArrayList<Stone> getStones() {
        return stones;
    }
}
