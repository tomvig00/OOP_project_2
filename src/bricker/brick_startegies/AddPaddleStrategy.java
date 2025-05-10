package bricker.brick_startegies;

import bricker.main.BrickerGameManager;
import danogl.GameObject;

import java.util.Random;

public class AddPaddleStrategy implements CollisionStrategy{

    private final CollisionStrategy baseStrategy;
    private final BrickerGameManager manager;

    public AddPaddleStrategy(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        this.baseStrategy = baseStrategy;
        this.manager = manager;
    }

    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        baseStrategy.onCollision(obj1, obj2);
        addExtraPaddle();
    }

    private void addExtraPaddle() {
        manager.createAdditionalPaddle();
    }


}



