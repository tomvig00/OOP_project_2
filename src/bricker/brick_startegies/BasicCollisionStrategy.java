package bricker.brick_startegies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;

public class BasicCollisionStrategy implements CollisionStrategy {

    private final GameObjectCollection gameObjects;

    public BasicCollisionStrategy(GameObjectCollection gameObjects) {
        this.gameObjects = gameObjects;
    }

    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        //System.out.println("collision with brick detected");
        this.gameObjects.removeGameObject(obj1, Layer.STATIC_OBJECTS);
    }
}
