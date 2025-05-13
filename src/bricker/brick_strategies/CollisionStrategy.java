package bricker.brick_strategies;

import danogl.GameObject;

/**
 * Interface of a brick collision strategy
 * @author tal.ronen1, tomvig00
 */
public interface CollisionStrategy {
    /**
     * This method is called when a collision occurs.
     *
     * @param obj1 the first object in the collision
     * @param obj2 the second object in the collision
     */
    void onCollision(GameObject obj1, GameObject obj2);
}
