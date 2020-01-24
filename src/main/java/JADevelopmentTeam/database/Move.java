package JADevelopmentTeam.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name = "move")
public class Move {
    private int gameID;
    private int orderOfMovement = 0;
    private int x = 0;
    private int y = 0;
    private boolean isBlack;
    private boolean isPass;

    @Id
    @Column(name = "game_id")
    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
    @Column(name = "order_of_movement")
    public int getOrderOfMovement() {
        return orderOfMovement;
    }

    public void setOrderOfMovement(int orderOfMovement) {
        this.orderOfMovement = orderOfMovement;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Column(name = "is_black")
    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    @Column(name = "is_pass")
    public boolean isPass() {
        return isPass;
    }

    public void setPass(boolean pass) {
        isPass = pass;
    }

    public void configureMove(int x, int y, boolean isBlack) {
        this.x = x;
        this.y = y;
        this.isPass = false;
        this.isBlack = isBlack;
    }

    public void makeOneStep(){
        orderOfMovement++;
    }
}
