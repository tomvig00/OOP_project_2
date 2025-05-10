package bricker.game_parameters;

import danogl.util.Vector2;

public class HeartParameters {

    // path to heart png image
    public static final String HEART_IMAGE_PATH = "assets/heart.png";

    // velocity of a falling heart
    public static final Vector2 FALL_VELOCITY = new Vector2(0, 100);

    // heart dimensions
    public static final Vector2 HEART_SIZE = new Vector2(20, 20);

    // heart bar position offset
    public static final Vector2 HEART_BAR_POSITION_OFFSET = new Vector2(WallParameters.WALL_WIDTH, 0);
}
