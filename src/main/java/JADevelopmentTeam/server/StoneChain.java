package JADevelopmentTeam.server;

import java.util.ArrayList;

public class StoneChain implements Cloneable {
    private ArrayList<Stone> stones = new ArrayList<>();
    private int liberties;

    public StoneChain(ArrayList<Stone> stones) {
        this.stones = stones;
    }

    public StoneChain(Stone stone) {
        this.stones.add(stone);
    }

    void addStone(Stone stone) {
        stones.add(stone);
    }

    void addStones(ArrayList<Stone> newStones) {
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

    @Override
    public StoneChain clone() {
        StoneChain clone = null;
        try {
            clone = (StoneChain) super.clone();
        } catch (CloneNotSupportedException e) {
            clone = new StoneChain(new ArrayList<>());
        }
        clone.setLiberties(this.getLiberties());
        ArrayList<Stone> copy = new ArrayList<>();
        for (Stone original : this.getStones()){
            Stone temp = new Stone(original.getXCoordinate(),original.getYCoordinate());
            temp.setLiberties(original.getLiberties());
            copy.add(temp);
        }
        clone.stones = copy;
        return clone;
    }
}
