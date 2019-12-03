package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public class StoneChain {
    private ArrayList <Stone> stones = new ArrayList<>();
    private int liberties;

    public StoneChain(ArrayList<Stone> stones) {
        this.stones = stones;
    }
    public StoneChain(Stone stone) {
        this.stones.add(stone);
    }

    void addStone (Stone stone){
        stones.add(stone);
    }
    void addStones (ArrayList <Stone> newStones){
        stones.addAll(newStones);
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
