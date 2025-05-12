package bricker.main;

import bricker.brick_startegies.BasicCollisionStrategy;
import bricker.brick_startegies.CollisionStrategy;
import bricker.brick_startegies.CollisionStrategyEnum;
import bricker.brick_startegies.CollisionStrategyFactory;
import bricker.game_parameters.*;
import bricker.game_objects.Brick;
import bricker.game_objects.HeartBar;
import bricker.game_objects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;
import bricker.game_objects.Ball;

import java.awt.event.KeyEvent;
import java.util.Random;
import java.util.Vector;

public class BrickerGameManager extends GameManager {

    // Game Settings
    private static final String GAME_TITLE = "Bricker";
    private static final Vector2 WINDOW_SIZE = new Vector2(700, 500);
    private static final String BACKGROUND_IMAGE_PATH = "assets/DARK_BG2_small.jpeg";

    private Renderable turboImage;
    private Renderable ballImage;
    private Sound collisionSound;
    private Renderable paddleImage;

    // Messages
    private static final String MESSAGE_WIN = "You Win!";
    private static final String MESSAGE_LOSE = "You Lose!";
    private static final String MESSAGE_END_PROMPT = " Play again?";

    private final int bricksInRow;
    private final int rows;

    private Ball mainBall;
    private HeartBar heartBar;
    private Counter brickCounter;
    //    private Ball[] otherBalls;
    private final Vector<GameObject> movingObjects;
    private Paddle mainPaddle;
    private Paddle extraPaddle;

    private boolean isInTurbo = false;
    private int counterWhenturboStarted = 0;

    private Vector2 windowDimensions;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private WindowController windowController;
    private UserInputListener inputListener;

    private final Random rand;


    public static void main(String[] args) {
        int bricksInRow = GameRules.BRICKS_IN_ROW;
        int rows = GameRules.ROWS;
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

//        otherBalls = new Ball[100];
        movingObjects = new Vector<>();
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        if (checkWCondition()) return;
        checkBallOutOfBounds();
        checkMovingObjectsOutOfBounds();
        checkExtraPaddleExpiration();
        checkTurboExpiration();
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

        // loading assets
        ballImage = imageReader.readImage(BallParameters.BALL_IMAGE_PATH, true);
        turboImage = imageReader.readImage(TurboParameters.TURBO_IMAGE_PATH, true);
        collisionSound = soundReader.readSound(BallParameters.BALL_SOUND_PATH);
        paddleImage = imageReader.readImage(PaddleParameters.PADDLE_IMAGE_PATH, true);

        createMainBall();
        createPaddle(inputListener, false);
        extraPaddle = null;
        createWalls();
        createBackground(imageReader);
        createBrickGrid(imageReader);
        createHeartBar(imageReader);
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
     * @param obj              object to be added.
     * @param checkOutOfBounds - if true, remove object when it's out of screen.
     */
    public void addGameObject(GameObject obj, boolean checkOutOfBounds) {
        gameObjects().addGameObject(obj);
        if (checkOutOfBounds) {
            movingObjects.add(obj);
        }
    }

    public void createAdditionalPaddle() {
        createPaddle(inputListener, true);
    }

    public void enterTurboMode() {
        if (isInTurbo) {
            return;
        }
        isInTurbo = true;
        mainBall.renderer().setRenderable(turboImage);
        mainBall.setVelocity(mainBall.getVelocity().mult(TurboParameters.TURBO_FACTOR));

        counterWhenturboStarted = mainBall.getCollisionCounter();
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

    public boolean isMainBall(GameObject obj) {
        return obj == mainBall;
    }

    public boolean isMainPaddle(GameObject obj) {
        return obj == mainPaddle;
    }

    public void addLife() {
        heartBar.addHeart();
    }


    private boolean checkWCondition() {
        if (inputListener.isKeyPressed(KeyEvent.VK_W)) {
            handleEndGame(MESSAGE_WIN);
            return true;
        }
        return false;
    }

    private boolean isOutOfBounds(GameObject obj) {
        return obj.getTopLeftCorner().y() > windowDimensions.y();
    }

    private void checkBallOutOfBounds() {
        if (isOutOfBounds(mainBall)) {
            heartBar.removeHeart();
            gameObjects().removeGameObject(mainBall);
            createMainBall();
        }
    }

    private void checkMovingObjectsOutOfBounds() {
        Vector<GameObject> toBeRemoved = new Vector<>();
        for (GameObject obj : movingObjects) {
            if (isOutOfBounds(obj)) {
                toBeRemoved.add(obj);
            }
        }

        for (GameObject obj : toBeRemoved) {
            movingObjects.remove(obj);
            gameObjects().removeGameObject(obj);
        }
    }

    private void checkExtraPaddleExpiration() {
        if (extraPaddle == null) {
            return;
        }

        if (extraPaddle.getCollisionCounter() >= GameRules.MAX_EXTRA_PADDLE_HITS) {
            gameObjects().removeGameObject(extraPaddle);
            extraPaddle = null;
        }
    }

    private void checkTurboExpiration() {
        if (!isInTurbo) {
            return;
        }
        if (mainBall.getCollisionCounter() >= GameRules.MAX_TURBO_COLLISIONS + counterWhenturboStarted) {
            exitTurboMode();
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
        Renderable heartImage = imageReader.readImage(HeartParameters.HEART_IMAGE_PATH, true);
        Vector2 heartBarPosition = new Vector2(
                HeartParameters.HEART_BAR_POSITION_OFFSET.x(),
                windowDimensions.y() - HeartParameters.HEART_SIZE.y()
        );
        heartBar = new HeartBar(heartBarPosition, HeartParameters.HEART_SIZE, heartImage, GameRules.MAX_HEARTS,
                GameRules.INITIAL_HEARTS, gameObjects());
    }

    private void createBrickGrid(ImageReader imageReader) {
        float availableWidth = windowDimensions.x() - 2 * WallParameters.WALL_WIDTH;
        float totalGapWidth = (bricksInRow + 1) * BrickParameters.BRICK_X_GAP;
        float brickWidth = (availableWidth - totalGapWidth) / bricksInRow;
        Vector2 brickSize = new Vector2(brickWidth, BrickParameters.BRICK_HEIGHT);
        brickCounter = new Counter(0);

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < bricksInRow; col++) {
                float x = WallParameters.WALL_WIDTH + BrickParameters.BRICK_X_GAP + col * (brickWidth + BrickParameters.BRICK_X_GAP);
                float y = WallParameters.WALL_HEIGHT + BrickParameters.BRICK_Y_GAP + row * (BrickParameters.BRICK_HEIGHT + BrickParameters.BRICK_Y_GAP);
                CollisionStrategy strategy = getRandomStrategy();
                createBrick(imageReader, new Vector2(x, y), brickSize,
                        strategy);
            }
        }
    }

    private CollisionStrategy getRandomStrategy() {
        CollisionStrategy baseStrategy = new BasicCollisionStrategy(this, brickCounter);
        return expandStrategyRandomly(baseStrategy);
    }

    private void createBrick(ImageReader imageReader, Vector2 topLeftPosition, Vector2 dimensions,
                             CollisionStrategy collisionStrategy) {
        Renderable brickImage = imageReader.readImage(BrickParameters.BRICK_IMAGE_PATH, false);
        Brick brick = new Brick(topLeftPosition, dimensions, brickImage, collisionStrategy);
        gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
        brickCounter.increment();
    }

    private Paddle createPaddleAtHeight(UserInputListener inputListener, float yCoordinate) {
        float leftBorder = WallParameters.WALL_WIDTH;
        float rightBorder = windowDimensions.x() - WallParameters.WALL_WIDTH;
        Paddle paddle = new Paddle(Vector2.ZERO, PaddleParameters.PADDLE_SIZE, paddleImage, inputListener,
                leftBorder, rightBorder);
        paddle.setCenter(new Vector2(windowDimensions.x() / 2,
                yCoordinate - PaddleParameters.PADDLE_Y_OFFSET));
        gameObjects().addGameObject(paddle);
        return paddle;
    }

    private void createPaddle(UserInputListener inputListener, boolean isAdditional) {
        // extra already exists
        if (isAdditional && extraPaddle != null) {
            return;
        }

        float yCoordinate = windowDimensions.y();
        if (isAdditional) {
            yCoordinate /= 2;
        }
        Paddle paddle = createPaddleAtHeight(inputListener, yCoordinate);
        if (isAdditional) {
            extraPaddle = paddle;
        } else {
            mainPaddle = paddle;
        }

    }

    private void createMainBall() {
        mainBall = new Ball(Vector2.ZERO, BallParameters.BALL_SIZE, ballImage, collisionSound);
        setRandomBallSpeed(mainBall);
        gameObjects().addGameObject(mainBall);
        mainBall.setTag(BallParameters.BALL_TAG);
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

        Vector2 yVec = new Vector2(WallParameters.WALL_WIDTH, windowDimensions.y());

        // left
        wallLocations[0][0] = Vector2.ZERO;
        wallLocations[0][1] = yVec;

        //right
        wallLocations[1][0] = new Vector2(windowDimensions.x() - WallParameters.WALL_WIDTH, 0);
        wallLocations[1][1] = yVec;

        //top
        wallLocations[2][0] = Vector2.ZERO;
        wallLocations[2][1] = new Vector2(windowDimensions.x(), WallParameters.WALL_HEIGHT);

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

    private void exitTurboMode() {
        if (!isInTurbo) {
            return;
        }
        isInTurbo = false;
        mainBall.renderer().setRenderable(ballImage);
        mainBall.setVelocity(mainBall.getVelocity().mult(1.0f / TurboParameters.TURBO_FACTOR));
    }


    private CollisionStrategy expandStrategyRandomly(CollisionStrategy baseStrategy) {
        if (rand.nextBoolean()) {
            return baseStrategy;
        }

        int strategiesToAdd = 1;
        int strategiesAdded = 0;
        CollisionStrategy currentStrategy = baseStrategy;
        while (strategiesAdded < strategiesToAdd && strategiesAdded <= GameRules.MAX_STRATEGY_PER_BRICK) {
            CollisionStrategyEnum newStrategy = CollisionStrategyEnum.values()[rand.nextInt(CollisionStrategyEnum.values().length)];
            if (newStrategy == CollisionStrategyEnum.DOUBLE_STRATEGY) {
                strategiesToAdd++;
            } else {
                currentStrategy = CollisionStrategyFactory.getCollisionStrategyDecorator(newStrategy, currentStrategy, this);
                strategiesAdded++;

            }
        }
        return currentStrategy;
    }
}