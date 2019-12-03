package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;

import java.util.ArrayList;

public class StoneChain {
    private ArrayList <Intersection> stones = new ArrayList<>();
    private int liberties;

    public StoneChain(ArrayList<Intersection> stones, int liberties) {
        this.stones = stones;
        this.liberties = liberties;
    }
    void addStone (Intersection stone){
        stones.add(stone);
    }


    public int getLiberties() {
        return liberties;
    }

    public void setLiberties(int liberties) {
        this.liberties = liberties;
    }
}
