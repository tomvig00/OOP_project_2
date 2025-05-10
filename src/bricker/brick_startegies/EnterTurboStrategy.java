package bricker.brick_startegies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

import java.util.Random;

public class EnterTurboStrategy extends CollisionStrategyDecorator{

    public EnterTurboStrategy(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        super(baseStrategy, manager);
    }

    @Override
    public void strategyOnCollision(GameObject obj1, GameObject obj2) {
        if(manager.isMainBall(obj2)) {
            manager.enterTurboMode();
        }
    }

}
