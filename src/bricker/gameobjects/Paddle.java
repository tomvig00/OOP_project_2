package bricker.gameobjects;

import bricker.game_parameters.BallParameters;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * game object of a paddle.
 * @author tal.ronen1, tomvig00
 */
public class Paddle extends GameObject {

    private static final float MOVEMENT_SPEED = 350;
    private final UserInputListener inputListener;
    private final float leftEdge;
    private final float rightEdge;
    private int collisionCounter;

    /**
     * Construct a new Paddle instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the Paddle will not be rendered.
     * @param inputListener - input listener to draw movement from.
     * @param leftBorder - left border of the paddle
     * @param rightBorder - right border of the paddle
     */
    public Paddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, UserInputListener
            inputListener, float leftBorder, float rightBorder) {
        super(topLeftCorner, dimensions, renderable);
        this.inputListener = inputListener;
        this.leftEdge = leftBorder;
        this.rightEdge = rightBorder;
        collisionCounter = 0;

    }

    /**
     * update Override
     * @param deltaTime The time elapsed, in seconds, since the last frame. Can
     *                  be used to determine a new position/velocity by multiplying
     *                  this delta with the velocity/acceleration respectively
     *                  and adding to the position/velocity:
     *                  velocity += deltaTime*acceleration
     *                  pos += deltaTime*velocity
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        Vector2 movementDir = Vector2.ZERO;
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) &&
                getTopLeftCorner().x() > leftEdge) {
            movementDir = movementDir.add(Vector2.LEFT);
        }
        if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) &&
                getTopLeftCorner().x() + getDimensions().x() < rightEdge) {
            movementDir = movementDir.add(Vector2.RIGHT);
        }
        setVelocity(movementDir.mult(MOVEMENT_SPEED));
    }

    /**
     * topLeftCorner getter
     * @return topLeftCorner of paddle
     */
    @Override
    public Vector2 getTopLeftCorner() {
        return super.getTopLeftCorner();
    }

    /**
     * onCollisionEnter Override
     * @param other The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (other.getTag().equals(BallParameters.BALL_TAG)) {
            collisionCounter++;
        }
    }

    /**
     * collisionCounter getter
     * @return collisionCounter
     */
    public int getCollisionCounter() {
        return collisionCounter;
    }
}
