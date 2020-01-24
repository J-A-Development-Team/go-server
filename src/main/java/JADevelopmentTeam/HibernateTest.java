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
//        Game game = new Game(4,9,false);
//
//        MySQLConnector.sendObject(game);
//        game.setId(MySQLConnector.getLastGameID());
//        Move move = new Move();
//        move.setGameID(game.getId());
//        move.setOrderOfMovement(1);
//        move.configureMove(1,1,true);
//        MySQLConnector.sendObject(move);
//        MySQLConnector.sendObject(move);
//        MySQLConnector.sendObject(move);
//        MySQLConnector.sendObject(move);
//        System.out.println(MySQLConnector.getLastGameID());

        DataPackage dataPackage = new DataPackage(new Intersection(1,1), DataPackage.Info.Stone);
        System.out.println(new Gson().toJson(dataPackage));
    }
}
