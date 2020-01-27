package JADevelopmentTeam;

import JADevelopmentTeam.common.DataPackage;
import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.database.Game;
import JADevelopmentTeam.database.Move;
import JADevelopmentTeam.database.MySQLConnector;
import JADevelopmentTeam.server.GameLogic.Stone;
import com.google.gson.Gson;

public class HibernateTest {
    public static void main(String[] args) {
        Game game = new Game(4,9,false);

        MySQLConnector mySQLConnector = MySQLConnector.getInstance();
        mySQLConnector.sendObject(game);
        game.setId(mySQLConnector.getLastGameID());
        Move move = new Move();
        move.setGameID(game.getId());
        move.setOrderOfMovement(1);
        move.configureMove(1,1,true);
        mySQLConnector.sendObject(move);
        mySQLConnector.sendObject(move);
        mySQLConnector.sendObject(move);
        mySQLConnector.sendObject(move);
        System.out.println(mySQLConnector.getLastGameID());


    }
}
