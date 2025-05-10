package bricker.brick_startegies;

import bricker.game_parameters.BallParameters;
import bricker.gameobjects.Ball;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.gui.*;

import java.util.Random;

public class AddPuckStrategy implements CollisionStrategyDecorator{
    private final int BALL_AMOUNT = 2;
    private final static float SIZE_FACTOR = 0.75f;
    private final String BALL_IMAGE_PATH = "assets/mockBall.png";
    private final String BALL_SOUND_PATH = "assets/blop.wav";

    private final CollisionStrategy baseStrategy;
    private final BrickerGameManager manager;

    private static Random rand;


    public AddPuckStrategy(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        this.baseStrategy = baseStrategy;
        this.manager = manager;
        rand = new Random();
    }

    @Override
    public void onCollision(GameObject obj1, GameObject obj2) {
        baseStrategy.onCollision(obj1, obj2);
        addMoreBalls(obj1.getCenter());
    }

    private void addMoreBalls(Vector2 spawnAt) {
        for(int i=0; i < BALL_AMOUNT; i++)
        {
            addPuckBall(spawnAt);
        }
    }

    private void addPuckBall(Vector2 spawnAt) {
        Renderable ballImage = manager.getImageReader().readImage(BALL_IMAGE_PATH, true);
        Sound collisionSound = manager.getSoundReader().readSound(BALL_SOUND_PATH);
        Ball ball = new Ball(Vector2.ZERO, BallParameters.BALL_SIZE.mult(SIZE_FACTOR), ballImage, collisionSound);
        setRandomBallSpeed(ball);
        ball.setCenter(spawnAt);
        manager.addGameObject(ball);
    }

    private void setRandomBallSpeed(Ball ball ) {
        double angle = rand.nextDouble() * Math.PI;
        float velocityX = (float) Math.cos(angle) * BallParameters.BALL_SPEED;
        float velocityY = (float) Math.sin(angle) * BallParameters.BALL_SPEED;
        ball.setVelocity(new Vector2(velocityX, velocityY));
    }
}
