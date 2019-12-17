package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class BotBrainTest {
    @Test
    public void testPossibleMoves(){
        GameManager gameManager = new GameManager(5);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 0),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 1),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 2),0));
        GameManager backup = gameManager.copy();
        ArrayList <Intersection> possibleMoves =  BotBrain.getPossibleMoves(gameManager,false,backup);
        Assert.assertEquals(gameManager.getBoardAsIntersections()[0][1].isHasStone(),backup.getBoardAsIntersections()[0][1].isHasStone());
        Assert.assertEquals(21,possibleMoves.size());
        for(Intersection intersection : possibleMoves){
            Assert.assertEquals(false,intersection.getXCoordinate()==0 && intersection.getYCoordinate()==0);
        }


    }
}
