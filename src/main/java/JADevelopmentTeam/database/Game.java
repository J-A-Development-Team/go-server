package JADevelopmentTeam.database;

import javax.persistence.*;

@Entity
@Table(name = "game")
public class Game {
    private int id;
    private int boardSize;
    private boolean withBot;

    public Game(int id, int boardSize, boolean withBot) {
        this.id = id;
        this.boardSize = boardSize;
        this.withBot = withBot;
    }
    public Game(){};

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    @Column(name = "board_size")
    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int board_size) {
        this.boardSize = board_size;
    }
    @Column(name = "with_bot")
    public boolean isWithBot() {
        return withBot;
    }

    public void setWithBot(boolean with_bot) {
        this.withBot = with_bot;
    }
}
