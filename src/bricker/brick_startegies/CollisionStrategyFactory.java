package bricker.brick_startegies;

import bricker.main.BrickerGameManager;

import java.util.Random;

public class CollisionStrategyFactory {

    public static CollisionStrategy getCollisionStrategyDecorator(CollisionStrategyEnum strategyEnum, CollisionStrategy baseStrategy, BrickerGameManager manager) {
        assert strategyEnum != CollisionStrategyEnum.DOUBLE_STRATEGY;
        switch (strategyEnum) {
            case ADD_PUCK:
                return new AddPuckStrategy(baseStrategy, manager);
            case ADD_PADDLE:
                return new AddPaddleStrategy(baseStrategy, manager);
            case ENTER_TURBO:
                return new EnterTurboStrategy(baseStrategy, manager);
            case ADD_HEART:
                return new AddHeartStrategy(baseStrategy, manager);
        }
        return null;
    }
}
