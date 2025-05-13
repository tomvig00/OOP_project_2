package bricker.game_parameters;

/**
 * parameters container for game rules related parameters
 *
 * @author tal.ronen1, tomvig00
 */
public class GameRules {

    /** max amount of hits an extra paddle can take */
    public static final int MAX_EXTRA_PADDLE_HITS = 4;

    /** max amount of objects a turbo ball can collide with */
    public static final int MAX_TURBO_COLLISIONS = 6;

    /** amount of bricks in a row */
    public static final int BRICKS_IN_ROW = 8;

    /** amount of initial rows */
    public static final int ROWS = 7;

    /** amount of initial hearts */
    public static final int INITIAL_HEARTS = 3;

    /** maximum heart amount possible */
    public static final int MAX_HEARTS = 4;

    /** maximum amount of strategies per brick */
    public static final int MAX_STRATEGY_PER_BRICK = 3;
}
