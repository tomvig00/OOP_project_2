package bricker.brick_strategies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * this class represents the collision strategy with a basic block.
 * @author tal.ronen1, tomvig00
 */
public class BasicCollisionStrategy implements CollisionStrategy {

    private final BrickerGameManager gameManager;
    private final Counter brickCounter;

    /**
     * constructor
     * @param gameManager - a BrickerGameManager
     * @param brickCounter - counter of current brick amount
     */
    public BasicCollisionStrategy(BrickerGameManager gameManager, Counter brickCounter) {
        this.gameManager = gameManager;
        this.brickCounter = brickCounter;
    }

    /**
     * overrides onCollision.
     * @param obj1 the first object in the collision
     * @param obj2 the second object in the collision
     */
    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        boolean foundBrick = gameManager.removeGameObject(obj1, Layer.STATIC_OBJECTS);
        if (foundBrick) {
            brickCounter.decrement();
        }
    }
}