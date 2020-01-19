package JADevelopmentTeam;

import JADevelopmentTeam.database.Game;
import JADevelopmentTeam.database.Move;
import JADevelopmentTeam.database.MySQLConnector;

public class HibernateTest {
    public static void main(String[] args) {
        Game game = new Game(2,9,false);
        MySQLConnector.sendObject(game);
        Move move = new Move();
        move.setGameID(game.getId());
        move.move(1,1,true);
        MySQLConnector.sendObject(move);
    }
}
