package bricker.brick_startegies;

import bricker.main.BrickerGameManager;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
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

    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        boolean foundBrick = gameManager.removeGameObject(obj1, Layer.STATIC_OBJECTS);
        if (foundBrick) {
            brickCounter.decrement();
        }
    }
}