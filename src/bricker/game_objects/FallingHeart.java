package bricker.game_objects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class FallingHeart extends GameObject {

    BrickerGameManager manager;

    public FallingHeart(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
                        BrickerGameManager manager) {
        super(topLeftCorner, dimensions, renderable);
        this.manager = manager;
    }

    @Override
    public boolean shouldCollideWith(GameObject other) {
        if(!manager.isMainPaddle(other))
        {
            return false;
        }
        return super.shouldCollideWith(other);
    }

    @Override
    public void onCollisionEnter(GameObject obj, Collision collision) {
        manager.addLife();
        manager.removeGameObject(this, Layer.DEFAULT);
    }
}
