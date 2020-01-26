package JADevelopmentTeam.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "end_game")
public class EndGame {
    private int gameID;
    private int blackTerritory;
    private int blackPrisoners;
    private int whiteTerritory;
    private int whitePrisoners;

    public EndGame() {
    }

    public EndGame(int gameID, int blackTerritory, int blackPrisoners, int whiteTerritory, int whitePrisoners) {
        this.gameID = gameID;
        this.blackTerritory = blackTerritory;
        this.blackPrisoners = blackPrisoners;
        this.whiteTerritory = whiteTerritory;
        this.whitePrisoners = whitePrisoners;
    }

    @Id
    @Column(name = "game_id")
    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    @Column(name = "black_territory")
    public int getBlackTerritory() {
        return blackTerritory;
    }

    public void setBlackTerritory(int blackTerritory) {
        this.blackTerritory = blackTerritory;
    }

    @Column(name = "black_prisoners")
    public int getBlackPrisoners() {
        return blackPrisoners;
    }

    public void setBlackPrisoners(int blackPrisoners) {
        this.blackPrisoners = blackPrisoners;
    }

    @Column(name = "white_territory")
    public int getWhiteTerritory() {
        return whiteTerritory;
    }

    public void setWhiteTerritory(int whiteTerritory) {
        this.whiteTerritory = whiteTerritory;
    }

    @Column(name = "white_prisoners")
    public int getWhitePrisoners() {
        return whitePrisoners;
    }

    public void setWhitePrisoners(int whitePrisoners) {
        this.whitePrisoners = whitePrisoners;
    }
}
