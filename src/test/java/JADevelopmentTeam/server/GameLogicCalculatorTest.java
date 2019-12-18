package JADevelopmentTeam.server;

import JADevelopmentTeam.common.Intersection;
import JADevelopmentTeam.server.GameLogic.GameLogicCalculator;
import JADevelopmentTeam.server.GameLogic.GameManager;
import JADevelopmentTeam.server.GameLogic.Stone;
import JADevelopmentTeam.server.GameLogic.StoneChain;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class GameLogicCalculatorTest {
private GameManager gameManager;
    @Before
    public  void initialize(){
        gameManager = new GameManager(5);
        gameManager.processMove(new Intersection(0, 0),0);
        gameManager.processMove(new Intersection(2, 0),0);
        gameManager.processMove(new Intersection(4, 0),0);
        gameManager.processMove(new Intersection(3, 1),0);
        gameManager.processMove(new Intersection(0, 2),1);
        gameManager.processMove(new Intersection(1, 2),1);
        gameManager.processMove(new Intersection(0, 3),0);
        gameManager.processMove(new Intersection(1, 4),0);
        gameManager.processMove(new Intersection(3, 4),1);
        gameManager.processMove(new Intersection(4, 3),0);
    }

    @Test
    public void testCalculateLiberties() {
        Assert.assertEquals(2, GameLogicCalculator.calculateLiberties(new Stone(0,0),gameManager.getBoard()));
        Assert.assertEquals(4,GameLogicCalculator.calculateLiberties(new Stone(3,1),gameManager.getBoard()));
        Assert.assertEquals(3,GameLogicCalculator.calculateLiberties(new Stone(1,2),gameManager.getBoard()));

    }

    @Test
    public void testCalculateLiberties1() {
        Assert.assertEquals(2, GameLogicCalculator.calculateLiberties(new Intersection(0, 0), gameManager.getBoard()));
        Assert.assertEquals(4, GameLogicCalculator.calculateLiberties(new Intersection(3, 1), gameManager.getBoard()));
        Assert.assertEquals(3, GameLogicCalculator.calculateLiberties(new Intersection(1, 2), gameManager.getBoard()));
    }
    @Test
    public void testCalculateLiberties2() {
        Assert.assertEquals(2, GameLogicCalculator.calculateLiberties(new StoneChain(new Stone(0, 0)), gameManager.getBoard()));
        Assert.assertEquals(4, GameLogicCalculator.calculateLiberties(new StoneChain(new Stone(3, 1)), gameManager.getBoard()));
        Assert.assertEquals(3, GameLogicCalculator.calculateLiberties(new StoneChain(new Stone(1, 2)), gameManager.getBoard()));
    }

    @Test
    public void getNeighborChains() {

        Assert.assertEquals(2,GameLogicCalculator.getNeighborChains(new Stone(1,3),gameManager.getBoard(),gameManager.playersStoneChains.get(0),gameManager.playersStones.get(0)).size());
        Assert.assertEquals(1,GameLogicCalculator.getNeighborChains(new Stone(1,3),gameManager.getBoard(),gameManager.playersStoneChains.get(1),gameManager.playersStones.get(1)).size());
        Assert.assertEquals(0,GameLogicCalculator.getNeighborChains(new Stone(2,2),gameManager.getBoard(),gameManager.playersStoneChains.get(0),gameManager.playersStones.get(0)).size());
    }

    @Test
    public void getStoneForIntersection() {
        Assert.assertEquals(null,GameLogicCalculator.getStoneForIntersection(new Intersection(0,1),gameManager.playersStones.get(0)));
        Assert.assertEquals(null,GameLogicCalculator.getStoneForIntersection(new Intersection(0,1),gameManager.playersStones.get(1)));
        Assert.assertEquals(gameManager.playersStones.get(1).get(0),GameLogicCalculator.getStoneForIntersection(new Intersection(0,2),gameManager.playersStones.get(1)));
    }

    @Test
    public void getIntersectionForStone() {
        Intersection intersection = GameLogicCalculator.getIntersectionForStone(new Stone(0,2),gameManager.getBoard());
        Assert.assertEquals(gameManager.getBoard().getIntersections()[0][2],intersection);
    }

    @Test
    public void getStoneChainIDForStone() {
        Assert.assertEquals(0,GameLogicCalculator.getStoneChainIDForStone(new Stone(0,2),gameManager.playersStoneChains.get(1)));
    }

    @Test
    public void generateSuperStoneChain() {
        ArrayList<StoneChain> stoneChains = new ArrayList<>();
        stoneChains.add(new StoneChain(new Stone(0,0)));
        stoneChains.add(new StoneChain(new Stone(2,0)));
        stoneChains.add(new StoneChain(new Stone(1,1)));
        Assert.assertEquals(4,GameLogicCalculator.generateSuperStoneChain(stoneChains,new Stone(1,0)).getStones().size());
    }

    @Test
    public void processMove() {
        gameManager = new GameManager(5);
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 0),0));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(1, 0),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 1),1));
        Assert.assertEquals(0,gameManager.processMove(new Intersection(0, 2),0));
        Intersection intersection = new Intersection(0,0);
        Assert.assertEquals(2,GameLogicCalculator.processMove(intersection,gameManager,0));
    }

    @Test
    public void getSumOfLiberties() {
        Assert.assertEquals(19,GameLogicCalculator.getSumOfLiberties(gameManager,0));
        Assert.assertEquals(7,GameLogicCalculator.getSumOfLiberties(gameManager,1));

    }
}