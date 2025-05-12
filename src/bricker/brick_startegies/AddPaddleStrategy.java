package bricker.brick_startegies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

/**
 * collision strategy of adding a second paddle.
 * @author tal.ronen1, tomvig00
 */
public class AddPaddleStrategy extends CollisionStrategyDecorator{

    /**
     * constructor
     * @param baseStrategy - base strategy to wrap with this decorator
     * @param manager - game manager of current game.
     */
    public AddPaddleStrategy(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        super(baseStrategy, manager);
    }

    /**
     * strategyOnCollision override.
     * @param obj1 - first object in the collision.
     * @param obj2 - the second object in the collision.
     */
    @Override
    public void strategyOnCollision(GameObject obj1, GameObject obj2) {
        manager.createAdditionalPaddle();
    }
}