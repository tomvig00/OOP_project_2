package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * game object of a falling heart.
 *
 * @author tal.ronen1, tomvig00
 */
public class FallingHeart extends GameObject {

    private final BrickerGameManager manager;

    /**
     * constuctor
     *
     * @param topLeftCorner - top left corner of the heart
     * @param dimensions    - dimensions of the image.
     * @param renderable    - renderable picture of the heart.
     * @param manager       - current game manager.
     */
    public FallingHeart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                        BrickerGameManager manager) {
        super(topLeftCorner, dimensions, renderable);
        this.manager = manager;
    }

    /**
     * shouldCollideWith override.
     *
     * @param other The other GameObject.
     * @return - true if should colide with other object.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (!manager.isMainPaddle(other)) {
            return false;
        }
        return super.shouldCollideWith(other);
    }

    /**
     * onCollisionEnter override
     *
     * @param obj       The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject obj, Collision collision) {
        manager.addLife();
        manager.removeGameObject(this, Layer.DEFAULT);
    }
}