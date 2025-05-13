package bricker.brick_startegies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;


/**
 * an abstract class representing a decorator that stack a new behavior on top of an existing strategy.
 * @author tal.ronen1, tomvig00
 */
public abstract class CollisionStrategyDecorator implements CollisionStrategy {

    /** the game manager of the current game. */
    protected BrickerGameManager manager;

    /** the base strategy to wrap. */
    protected CollisionStrategy baseStrategy;

    /**
     * constructor
     * @param baseStrategy - the base strategy to wrap
     * @param manager - the game manager of the current game
     */
    public CollisionStrategyDecorator(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        this.baseStrategy = baseStrategy;
        this.manager = manager;
    }

    /**
     * onCollision override
     * @param obj1 the first object in the collision
     * @param obj2 the second object in the collision
     */
    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        baseStrategy.onCollision(obj1, obj2);
        strategyOnCollision(obj1, obj2);
    }

    /**
     * the behavior to perform after performing the baseStrategy.
     * @param obj1 - first object in the collision.
     * @param obj2 - the second object in the collision.
     */
    public abstract void strategyOnCollision(GameObject obj1, GameObject obj2);
}
