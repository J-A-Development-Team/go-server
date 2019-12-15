package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import org.junit.Assert;
import org.junit.Test;

public class GameManagerTest {

    @Test
    public void testClone() {
        GameManager gameManager = new GameManager(9);
        gameManager.getBoard().setIntersection(new Intersection(1, 1, true));
        gameManager.getBoard().setIntersection(new Intersection(2, 1, true));
        gameManager.getBoard().setIntersection(new Intersection(3, 1, true));
        gameManager.getBoard().setIntersection(new Intersection(4, 1, true));
        gameManager.getBoard().setIntersection(new Intersection(5, 1, true));
        gameManager.getBoard().setIntersection(new Intersection(6, 1, true));
        GameManager clone = gameManager.copy();

        gameManager.getBoard().getIntersections()[1][1].setStoneBlack(true);
        Assert.assertNotEquals(gameManager.getBoard().getIntersections()[1][1].isStoneBlack(), clone.getBoard().getIntersections()[1][1].isStoneBlack());
    }

    @Test
    public void testProcessMove() {
        GameManager gameManager = new GameManager(9);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 0),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 1),1));
        //KO
        Assert.assertEquals(3,gameManager.processMove(new Intersection(0, 0),0));
        //stone already there
        Assert.assertEquals(1,gameManager.processMove(new Intersection(0, 1),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 2),0));
        //suicidal move
        Assert.assertEquals(2,gameManager.processMove(new Intersection(0, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 0),1));
        Assert.assertEquals(1,gameManager.playerOneStoneChains.size());
        Assert.assertEquals(1,gameManager.playerTwoStoneChains.size());

    }

    @Test
    public void testProcessMove2() {
        GameManager gameManager = new GameManager(9);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 0),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 1),1));
        Assert.assertEquals(2,gameManager.processMove(new Intersection(0, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 0),1));

    }
    @Test
    public void countTerritory() {
        GameManager gameManager = new GameManager(9);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 4),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 3),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 2),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(3, 0),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(4, 0),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(4, 1),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(5, 1),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(5, 4),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(6, 5),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(7, 6),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(8, 6),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(8, 3),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(7, 3),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(6, 3),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 3),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 4),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 5),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 6),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 7),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 8),0));
        gameManager.addTerritoryPoints();
        Assert.assertEquals(5,gameManager.getPlayerOnePoints());
        Assert.assertEquals(9,gameManager.getPlayerTwoPoints());
    }
    @Test
    public void countTerritory2() {
        GameManager gameManager = new GameManager(9);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 4),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 3),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 2),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(3, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(4, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(4, 1),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(5, 1),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(5, 4),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(6, 5),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(7, 6),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(8, 6),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(8, 3),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(7, 3),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(6, 3),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 3),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 4),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 5),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 6),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 7),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(2, 8),1));
        gameManager.addTerritoryPoints();
        Assert.assertEquals(9,gameManager.getPlayerOnePoints());
        Assert.assertEquals(5,gameManager.getPlayerTwoPoints());
    }
    @Test
    public void countTerritory3() {
        GameManager gameManager = new GameManager(7);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(6, 6),0));
        gameManager.addTerritoryPoints();
        Assert.assertEquals(0,gameManager.getPlayerOnePoints());
        Assert.assertEquals(48,gameManager.getPlayerTwoPoints());
    }
    @Test
    public void countTerritory4() {
        GameManager gameManager = new GameManager(4);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 1),1));
        gameManager.addTerritoryPoints();
        Assert.assertEquals(15,gameManager.getPlayerOnePoints());
        Assert.assertEquals(0,gameManager.getPlayerTwoPoints());
    }
}