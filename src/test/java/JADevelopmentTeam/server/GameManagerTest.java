package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import org.junit.Assert;
import org.junit.Test;

public class GameManagerTest {

    @Test
    public void testClone() {
        GameManager gameManager = new GameManager(9);
        gameManager.getBoard().setIntersection(new Intersection(1,1,true));
        gameManager.getBoard().setIntersection(new Intersection(2,1,true));
        gameManager.getBoard().setIntersection(new Intersection(3,1,true));
        gameManager.getBoard().setIntersection(new Intersection(4,1,true));
        gameManager.getBoard().setIntersection(new Intersection(5,1,true));
        gameManager.getBoard().setIntersection(new Intersection(6,1,true));
        GameManager clone = gameManager.copy();

        gameManager.getBoard().getIntersections()[1][1].setStoneBlack(true);
        Assert.assertNotEquals(gameManager.getBoard().getIntersections()[1][1].isStoneBlack(),clone.getBoard().getIntersections()[1][1].isStoneBlack());
    }
}