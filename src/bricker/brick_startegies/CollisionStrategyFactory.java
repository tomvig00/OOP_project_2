package bricker.brick_startegies;

import bricker.main.BrickerGameManager;

/**
 * A factory for brick strategies.
 *
 * @author tal.ronen1, tomvig00
 */

public class CollisionStrategyFactory {

    /**
     * wraps a given strategy with a new behavior.
     *
     * @param strategyEnum - the enum of the behavior to add
     * @param baseStrategy - strategy to wrap
     * @param manager      - current game manager
     * @return - collision strategy representing the wrapped strategy
     */
    public static CollisionStrategy getCollisionStrategyDecorator(CollisionStrategyEnum strategyEnum,
                                                                  CollisionStrategy baseStrategy,
                                                                  BrickerGameManager manager) {
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
