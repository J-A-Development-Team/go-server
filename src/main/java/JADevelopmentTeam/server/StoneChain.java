package JADevelopmentTeam.server;

import java.util.ArrayList;

public class StoneChain {
    private ArrayList<Stone> stones = new ArrayList<>();
    private int liberties;
    private boolean isDead;

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

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

    public StoneChain copy() {
        ArrayList<Stone> copy = new ArrayList<>();
        for (Stone original : this.getStones()){
            Stone temp = new Stone(original.getXCoordinate(),original.getYCoordinate());
            temp.setLiberties(original.getLiberties());
            copy.add(temp);
        }
        StoneChain clone = new StoneChain(copy);
        clone.setLiberties(this.getLiberties());
        return clone;
    }
}
