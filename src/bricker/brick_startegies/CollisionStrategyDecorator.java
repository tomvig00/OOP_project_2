package bricker.brick_startegies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

public abstract class CollisionStrategyDecorator implements CollisionStrategy {
    protected BrickerGameManager manager;
    protected CollisionStrategy baseStrategy;

    public CollisionStrategyDecorator(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        this.baseStrategy = baseStrategy;
        this.manager = manager;
    }

    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        baseStrategy.onCollision(obj1, obj2);
        strategyOnCollision(obj1, obj2);
    }

    public abstract void strategyOnCollision(GameObject obj1, GameObject obj2);
}
