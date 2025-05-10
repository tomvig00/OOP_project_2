package bricker.brick_startegies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

public class BasicCollisionStrategy implements CollisionStrategy {

    private final GameObjectCollection gameObjects;
    private final Counter brickCounter;

    public BasicCollisionStrategy(GameObjectCollection gameObjects, Counter brickCounter) {
        this.gameObjects = gameObjects;
        this.brickCounter = brickCounter;
    }

    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        //System.out.println("collision with brick detected");
        this.gameObjects.removeGameObject(obj1, Layer.STATIC_OBJECTS);
        brickCounter.decrement();
    }
}
