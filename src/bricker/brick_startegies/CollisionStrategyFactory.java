package bricker.brick_startegies;

import bricker.main.BrickerGameManager;

import java.util.Random;

public class CollisionStrategyFactory {

    private static final Random rand = new Random();

    public static CollisionStrategy getCollisionStrategyDecorator(CollisionStrategyEnum strategyEnum, CollisionStrategy baseStrategy, BrickerGameManager manager) {
        switch (strategyEnum) {
            case ADD_PUCK:
                return new AddPuckStrategy(baseStrategy, manager);
            case ADD_PADDLE:
                return new AddPaddleStrategy(baseStrategy, manager);
            case ENTER_TURBO:
            return new EnterTurboStrategy(baseStrategy, manager);
        }
        return null;
    }

    public static CollisionStrategy generateRandomStrategy(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        if(rand.nextBoolean()) {
            return baseStrategy;
        }
        CollisionStrategyEnum strategyToUse = CollisionStrategyEnum.values()[rand.nextInt(CollisionStrategyEnum.values().length)];
        return getCollisionStrategyDecorator(strategyToUse, baseStrategy, manager);
    }
}
