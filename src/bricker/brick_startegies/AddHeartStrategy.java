package bricker.brick_startegies;

import bricker.game_objects.FallingHeart;
import bricker.game_parameters.HeartParameters;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class AddHeartStrategy extends CollisionStrategyDecorator{

    private static Renderable heartImage = null;

    public AddHeartStrategy (CollisionStrategy baseStrategy, BrickerGameManager manager) {
        super(baseStrategy, manager);

        if(heartImage == null) {
            heartImage = manager.getImageReader().readImage(HeartParameters.HEART_IMAGE_PATH, true);
        }

    }

    @Override
    public void strategyOnCollision(GameObject obj1, GameObject obj2) {
        Vector2 position = obj1.getCenter();
        FallingHeart heart = new FallingHeart(position, HeartParameters.HEART_SIZE, heartImage, manager);
        heart.setVelocity(HeartParameters.FALL_VELOCITY);
        heart.setCenter(obj1.getCenter());
        manager.addGameObject(heart, true);
    }

}
