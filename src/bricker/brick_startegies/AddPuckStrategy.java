package bricker.brick_startegies;

import bricker.game_parameters.BallParameters;
import bricker.game_objects.Ball;
import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.Random;

public class AddPuckStrategy extends CollisionStrategyDecorator {
    private static final int BALL_AMOUNT = 2;
    private static final float SIZE_FACTOR = 0.75f;
    private static final String BALL_IMAGE_PATH = "assets/mockBall.png";
    private static final String BALL_SOUND_PATH = "assets/blop.wav";

    private static Renderable ballImage = null;
    private static Sound ballSound = null;

    private static Random rand;


    public AddPuckStrategy(CollisionStrategy baseStrategy, BrickerGameManager manager) {
        super(baseStrategy, manager);
        rand = new Random();

        if (ballImage == null) {
            ballImage = manager.getImageReader().readImage(BALL_IMAGE_PATH, true);
        }

        if (ballSound == null) {
            ballSound = manager.getSoundReader().readSound(BALL_SOUND_PATH);
        }
    }

    @Override
    public void strategyOnCollision(GameObject obj1, GameObject obj2) {
        addMoreBalls(obj1.getCenter());
    }

    private void addMoreBalls(Vector2 spawnAt) {
        for (int i = 0; i < BALL_AMOUNT; i++) {
            addPuckBall(spawnAt);
        }
    }

    private void addPuckBall(Vector2 spawnAt) {
        Ball ball = new Ball(Vector2.ZERO, BallParameters.BALL_SIZE.mult(SIZE_FACTOR), ballImage, ballSound);
        setRandomBallSpeed(ball);
        ball.setCenter(spawnAt);
        ball.setTag(BallParameters.BALL_TAG);
        manager.addGameObject(ball, true);
    }

    private void setRandomBallSpeed(Ball ball) {
        double angle = rand.nextDouble() * Math.PI;
        float velocityX = (float) Math.cos(angle) * BallParameters.BALL_SPEED;
        float velocityY = (float) Math.sin(angle) * BallParameters.BALL_SPEED;
        ball.setVelocity(new Vector2(velocityX, velocityY));
    }
}
