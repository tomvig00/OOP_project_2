package bricker.main;

import bricker.brick_startegies.BasicCollisionStrategy;
import bricker.brick_startegies.CollisionStrategy;
import bricker.brick_startegies.CollisionStrategyFactory;
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
import bricker.game_parameters.BallParameters;
import bricker.game_parameters.gameRules;


import java.awt.event.KeyEvent;
import java.util.Random;

public class BrickerGameManager extends GameManager {

    // Game Settings
    private static final String GAME_TITLE = "Bricker";
    private static final Vector2 WINDOW_SIZE = new Vector2(700, 500);

    // Bricks
    private static final int BRICKS_IN_ROW = 8;
    //    private static final int ROWS = 7;
    private static final int ROWS = 2;
    private static final int BRICK_HEIGHT = 15;
    private static final int BRICK_X_GAP = 2;
    private static final int BRICK_Y_GAP = 2;

    // Paddle
    private static final Vector2 PADDLE_SIZE = new Vector2(200, 20);
    private static final int PADDLE_Y_OFFSET = 30;


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
    private static final String BRICK_IMAGE_PATH = "assets/brick.png";
    private static final String PADDLE_IMAGE_PATH = "assets/paddle.png";

    // Messages
    private static final String MESSAGE_WIN = "You Win!";
    private static final String MESSAGE_LOSE = "You Lose!";
    private static final String MESSAGE_END_PROMPT = " Play again?";

    private final int bricksInRow;
    private final int rows;

    private Ball mainBall;
    private HeartBar heartBar;
    private Counter brickCounter;
    private Ball[] otherBalls;
    private Paddle mainPaddle;
    private Paddle extraPaddle;

    private Vector2 windowDimensions;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private WindowController windowController;
    private UserInputListener inputListener;

    private final Random rand;


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

        rand = new Random();

        otherBalls = new Ball[100];
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (checkWCondition()) return;
        checkBallOutOfBounds();
        checkOtherBallsOutOfBounds();
        checkExtraPaddleExpiration();
        checkGameEndConditions();
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
        createPaddle(imageReader, inputListener, false);
        extraPaddle = null;
        createWalls();
        createBackground(imageReader);
        createBrickGrid(imageReader);
        createHeartBar(imageReader);

        for (int i = 0; i < otherBalls.length; i++) {
            otherBalls[i] = null;
        }
    }

    /**
     * removes a game object from the game
     *
     * @param obj     a game object to remove
     * @param layerID the layerID to remove object from
     * @return true if found object and removed it.
     */
    public boolean removeGameObject(GameObject obj, int layerID) {
        return gameObjects().removeGameObject(obj, layerID);
    }

    /**
     * adds a GameObject to the gameObjects of the game.
     *
     * @param obj object to be added.
     */
    public void addGameObject(GameObject obj) {
        gameObjects().addGameObject(obj);
        if (obj instanceof Ball) {
            addOtherBall((Ball) obj);
        }
    }

    public void createAdditionalPaddle() {
        createPaddle(imageReader, inputListener, true);
    }

    private void addOtherBall(Ball ball) {
        for (int i = 0; i < otherBalls.length; i++) {
            if (otherBalls[i] == null) {
                otherBalls[i] = ball;
                System.out.println(String.format("placed otehr ball at %d", i));
                break;
            }
        }
    }

    /**
     * image reader getter.
     *
     * @return image getter
     */
    public ImageReader getImageReader() {
        return imageReader;
    }

    /**
     * image reader getter.
     *
     * @return image getter
     */
    public SoundReader getSoundReader() {
        return soundReader;
    }

    private boolean checkWCondition() {
        if (inputListener.isKeyPressed(KeyEvent.VK_W)) {
            handleEndGame(MESSAGE_WIN);
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(Ball ball) {
        return ball.getTopLeftCorner().y() > windowDimensions.y();
    }

    private void checkBallOutOfBounds() {
        if (isOutOfBounds(mainBall)) {
            heartBar.removeHeart();
            gameObjects().removeGameObject(mainBall);
            createBall(imageReader, soundReader);
        }
    }

    private void checkOtherBallsOutOfBounds() {
        for (int i = 0; i < otherBalls.length; i++) {
            if (otherBalls[i] == null) {
                continue;
            }
            Ball currentBall = otherBalls[i];
            if (isOutOfBounds(currentBall)) {
                otherBalls[i] = null;
                gameObjects().removeGameObject(currentBall);
            }
        }
    }

    private void checkExtraPaddleExpiration() {
        if (extraPaddle == null) {
            return;
        }

        if (extraPaddle.getCollisionCounter() >= gameRules.MAX_EXTRA_PADDLE_HITS) {
            gameObjects().removeGameObject(extraPaddle);
            extraPaddle = null;
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
        brickCounter = new Counter(0);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < bricksInRow; col++) {
                float x = WALL_WIDTH + BRICK_X_GAP + col * (brickWidth + BRICK_X_GAP);
                float y = WALL_HEIGHT + BRICK_Y_GAP + row * (BRICK_HEIGHT + BRICK_Y_GAP);
                CollisionStrategy strategy = getRandomStrategy();
                createBrick(imageReader, new Vector2(x, y), brickSize,
                        strategy);
            }
        }
    }

    private CollisionStrategy getRandomStrategy() {
        CollisionStrategy baseStrategy = new BasicCollisionStrategy(this, brickCounter);
//        return baseStrategy;
        return CollisionStrategyFactory.generateRandomStrategy(baseStrategy, this);
    }

    private void createBrick(ImageReader imageReader, Vector2 topLeftPosition, Vector2 dimensions,
                             CollisionStrategy collisionStrategy) {
        Renderable brickImage = imageReader.readImage(BRICK_IMAGE_PATH, false);
        Brick brick = new Brick(topLeftPosition, dimensions, brickImage, collisionStrategy);
        gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
        brickCounter.increment();
    }

    private Paddle createPaddleAtHeight(ImageReader imageReader, UserInputListener inputListener, float yCoordinate) {
        float leftBorder = WALL_WIDTH;
        float rightBorder = windowDimensions.x() - WALL_WIDTH;
        Renderable paddleImage = imageReader.readImage(PADDLE_IMAGE_PATH, true);
        Paddle paddle = new Paddle(Vector2.ZERO, PADDLE_SIZE, paddleImage, inputListener,
                leftBorder, rightBorder);
        paddle.setCenter(new Vector2(windowDimensions.x() / 2,
                yCoordinate - PADDLE_Y_OFFSET));
        gameObjects().addGameObject(paddle);
        return paddle;
    }

    private void createPaddle(ImageReader imageReader, UserInputListener inputListener, boolean isAdditional) {
        // extra already exists
        if (isAdditional && extraPaddle != null) {
            return;
        }

        float yCoordinate = windowDimensions.y();
        if (isAdditional) {
            yCoordinate /= 2;
        }
        Paddle paddle = createPaddleAtHeight(imageReader, inputListener, yCoordinate);
        if (isAdditional) {
            extraPaddle = paddle;
        } else {
            mainPaddle = paddle;
        }

    }

    private void createBall(ImageReader imageReader, SoundReader soundReader) {
        Renderable ballImage = imageReader.readImage(BallParameters.BALL_IMAGE_PATH, true);
        Sound collisionSound = soundReader.readSound(BallParameters.BALL_SOUND_PATH);
        mainBall = new Ball(Vector2.ZERO, BallParameters.BALL_SIZE, ballImage, collisionSound);
        setRandomBallSpeed(mainBall);
        gameObjects().addGameObject(mainBall);
    }

    private void setRandomBallSpeed(Ball ball) {
        float ballVelX = BallParameters.BALL_SPEED;
        float ballVelY = BallParameters.BALL_SPEED;
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

        for (Vector2[] wallLocation : wallLocations) {
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