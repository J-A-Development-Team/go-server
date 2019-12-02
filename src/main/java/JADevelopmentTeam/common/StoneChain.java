package JADevelopmentTeam.common;

import java.util.ArrayList;

public class StoneChain {
    private ArrayList <Stone> stones = new ArrayList<>();
    private int breaths;

    public StoneChain(ArrayList<Stone> stones, int breaths) {
        this.stones = stones;
        this.breaths = breaths;
    }
    void addStone (Stone stone){
        stones.add(stone);
    }


    public int getBreaths() {
        return breaths;
    }

    public void setBreaths(int breaths) {
        this.breaths = breaths;
    }
}
