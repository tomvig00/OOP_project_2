package bricker.main;

import bricker.brick_startegies.BasicCollisionStrategy;
import bricker.brick_startegies.CollisionStrategy;
import bricker.gameobjects.Brick;
import bricker.gameobjects.HeartBar;
import bricker.gameobjects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import bricker.gameobjects.Ball;

import java.awt.event.KeyEvent;
import java.util.Random;

public class BrickerGameManager extends GameManager {

    // Game Settings
    private static final String GAME_TITLE = "Bricker";
    private static final Vector2 WINDOW_SIZE = new Vector2(700, 500);

    // Bricks
    private static final int BRICKS_IN_ROW = 8;
    private static final int ROWS = 7;
    private static final int BRICK_HEIGHT = 15;
    private static final int BRICK_X_GAP = 2;
    private static final int BRICK_Y_GAP = 2;

    // Paddle
    private static final Vector2 PADDLE_SIZE = new Vector2(200, 20);
    private static final int PADDLE_Y_OFFSET = 30;

    // Ball
    private static final float BALL_SPEED = 250;
    private static final Vector2 BALL_SIZE = new Vector2(50, 50);

    // Walls
    private static final int WALL_WIDTH = 10;
    private static final int WALL_HEIGHT = 10;

    // Hearts
    private static final int INITIAL_HEARTS = 3;
    private static final int MAX_HEARTS = 4;
    private static final Vector2 HEART_SIZE = new Vector2(20, 20);
    private static final Vector2 HEART_BAR_POSITION_OFFSET = new Vector2(WALL_WIDTH, 0);

    // Assets
    private static final String BACKGROUND_IMAGE_PATH = "assets/DARK_BG2_small.jpeg";
    private static final String HEART_IMAGE_PATH = "assets/heart.png";
    private static final String BALL_IMAGE_PATH = "assets/ball.png";
    private static final String BRICK_IMAGE_PATH = "assets/brick.png";
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";
    private static final String BALL_SOUND_PATH = "assets/blop.wav";

    // Messages
    private static final String MESSAGE_WIN = "You Win!";
    private static final String MESSAGE_LOSE = "You Lose!";
    private static final String MESSAGE_END_PROMPT = " Play again?";

    private final int bricksInRow;
    private final int rows;

    private Ball ball;
    private HeartBar heartBar;
    private Counter brickCounter;

    private Vector2 windowDimensions;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private WindowController windowController;
    private UserInputListener inputListener;

    public static void main(String[] args) {
        int bricksInRow = BRICKS_IN_ROW;
        int rows = ROWS;
        if (args.length == 2) {
            bricksInRow = Integer.parseInt(args[0]);
            rows = Integer.parseInt(args[1]);
        }
        BrickerGameManager gameManager = new BrickerGameManager(
                GAME_TITLE, WINDOW_SIZE, bricksInRow, rows);
        gameManager.run();
    }

    public BrickerGameManager(String windowTitle, Vector2 windowDimensions, int bricksInRow, int rows) {
        super(windowTitle, windowDimensions);
        this.bricksInRow = bricksInRow;
        this.rows = rows;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (checkWCondition()) return;
        checkBallOutOfBounds();
        checkGameEndConditions();
    }

    private boolean checkWCondition() {
        if (inputListener.isKeyPressed(KeyEvent.VK_W)) {
            handleEndGame(MESSAGE_WIN);
            return true;
        }
        return false;
    }

    private void checkBallOutOfBounds() {
        if (ball.getTopLeftCorner().y() > windowDimensions.y()) {
            heartBar.removeHeart();
            gameObjects().removeGameObject(ball);
            createBall(imageReader, soundReader);
        }
    }

    private void checkGameEndConditions() {
        if (heartBar.getCurrentHearts() <= 0) {
            handleEndGame(MESSAGE_LOSE);
        } else if (brickCounter.value() <= 0) {
            handleEndGame(MESSAGE_WIN);
        }
    }

    private void handleEndGame(String message) {
        message += MESSAGE_END_PROMPT;
        if (windowController.openYesNoDialog(message)) {
            windowController.resetGame();
        } else {
            windowController.closeWindow();
        }
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        this.inputListener = inputListener;
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.windowController = windowController;
        this.windowDimensions = windowController.getWindowDimensions();

        createBall(imageReader, soundReader);
        createPaddle(imageReader, inputListener);
        createWalls();
        createBackground(imageReader);
        createBrickGrid(imageReader);
        createHeartBar(imageReader);
    }

    private void createHeartBar(ImageReader imageReader) {
        Renderable heartImage = imageReader.readImage(HEART_IMAGE_PATH, true);
        Vector2 heartBarPosition = new Vector2(
                HEART_BAR_POSITION_OFFSET.x(),
                windowDimensions.y() - HEART_SIZE.y()
        );
        heartBar = new HeartBar(heartBarPosition, HEART_SIZE, heartImage, MAX_HEARTS, INITIAL_HEARTS, gameObjects());
    }

    private void createBrickGrid(ImageReader imageReader) {
        float availableWidth = windowDimensions.x() - 2 * WALL_WIDTH;
        float totalGapWidth = (bricksInRow + 1) * BRICK_X_GAP;
        float brickWidth = (availableWidth - totalGapWidth) / bricksInRow;
        Vector2 brickSize = new Vector2(brickWidth, BRICK_HEIGHT);
        brickCounter = new Counter(bricksInRow * rows);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < bricksInRow; col++) {
                float x = WALL_WIDTH + BRICK_X_GAP + col * (brickWidth + BRICK_X_GAP);
                float y = WALL_HEIGHT + BRICK_Y_GAP + row * (BRICK_HEIGHT + BRICK_Y_GAP);
                createBrick(imageReader, new Vector2(x, y), brickSize,
                        new BasicCollisionStrategy(gameObjects(), brickCounter));
            }
        }
    }

    private void createBrick(ImageReader imageReader, Vector2 topLeftPosition, Vector2 dimensions,
                             CollisionStrategy collisionStrategy) {
        Renderable brickImage = imageReader.readImage(BRICK_IMAGE_PATH, false);
        Brick brick = new Brick(topLeftPosition, dimensions, brickImage, collisionStrategy);
        gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
    }

    private void createPaddle(ImageReader imageReader, UserInputListener inputListener) {
        float leftBorder = WALL_WIDTH;
        float rightBorder = windowDimensions.x() - WALL_WIDTH;
        Renderable paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH, true);
        Paddle paddle = new Paddle(Vector2.ZERO, PADDLE_SIZE, paddleImage, inputListener,
                leftBorder, rightBorder);
        paddle.setCenter(new Vector2(windowDimensions.x() / 2,
                windowDimensions.y() - PADDLE_Y_OFFSET));
        gameObjects().addGameObject(paddle);
    }

    private void createBall(ImageReader imageReader, SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage(BALL_IMAGE_PATH, true);
        Sound collisionSound = soundReader.readSound(BALL_SOUND_PATH);
        ball = new Ball(Vector2.ZERO, BALL_SIZE, ballImage, collisionSound);
        setRandomBallSpeed(ball);
        gameObjects().addGameObject(ball);
    }

    private void setRandomBallSpeed(Ball ball) {
        float ballVelX = BALL_SPEED;
        float ballVelY = BALL_SPEED;
        Random rand = new Random();
        if (rand.nextBoolean()) ballVelX *= -1;
        if (rand.nextBoolean()) ballVelY *= -1;
        ball.setVelocity(new Vector2(ballVelX, ballVelY));
        ball.setCenter(windowDimensions.mult(0.5f));
    }

    private void createWalls() {
        Vector2[][] wallLocations = new Vector2[3][2];

        Vector2 yVec = new Vector2(WALL_WIDTH, windowDimensions.y());

        // left
        wallLocations[0][0] = Vector2.ZERO;
        wallLocations[0][1] = yVec;

        //right
        wallLocations[1][0] = new Vector2(windowDimensions.x() - WALL_WIDTH, 0);
        wallLocations[1][1] = yVec;

        //top
        wallLocations[2][0] = Vector2.ZERO;
        wallLocations[2][1] = new Vector2(windowDimensions.x(), WALL_HEIGHT);

        for(Vector2[] wallLocation: wallLocations)
        {
            GameObject wall = new GameObject(wallLocation[0], wallLocation[1], null);
            gameObjects().addGameObject(wall, Layer.STATIC_OBJECTS);
        }
    }

    private void createBackground(ImageReader imageReader) {
        Renderable backgroundImage = imageReader.readImage(BACKGROUND_IMAGE_PATH, false);
        GameObject background = new GameObject(Vector2.ZERO, windowDimensions, backgroundImage);
        background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        gameObjects().addGameObject(background, Layer.BACKGROUND);
    }
}