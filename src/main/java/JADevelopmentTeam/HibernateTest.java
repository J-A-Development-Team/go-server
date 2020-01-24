package JADevelopmentTeam;

import JADevelopmentTeam.database.Game;
import JADevelopmentTeam.database.Move;
import JADevelopmentTeam.database.MySQLConnector;

public class HibernateTest {
    public static void main(String[] args) {
        Game game = new Game(4,9,false);

        MySQLConnector.sendObject(game);
        game.setId(MySQLConnector.getLastGameID());
        Move move = new Move();
        move.setGameID(game.getId());
        move.setOrderOfMovement(1);
        move.configureMove(1,1,true);
        MySQLConnector.sendObject(move);
        MySQLConnector.sendObject(move);
        MySQLConnector.sendObject(move);
        MySQLConnector.sendObject(move);
        System.out.println(MySQLConnector.getLastGameID());
    }
}
