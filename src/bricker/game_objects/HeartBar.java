package bricker.game_objects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * HeartBar represents a bar of hearts displayed at the bottom-left of the screen.
 * It displays a number in the first slot and the hearts to its right.
 */
public class HeartBar extends GameObject {
    // Layout
    private static final float HEART_SPACING = 5;

    // Colors
    private static final Color COLOR_FULL = Color.GREEN;
    private static final Color COLOR_MEDIUM = Color.WHITE;
    private static final Color COLOR_LOW = Color.RED;

    // Thresholds
    private static final int HEART_THRESHOLD_FULL = 3;
    private static final int HEART_THRESHOLD_MEDIUM = 2;

    private final List<GameObject> hearts;
    private final Renderable heartImage;
    private final Vector2 heartSize;
    private final int maxHearts;
    private final GameObjectCollection gameObjects;

    private final GameObject numberDisplay;
    private final TextRenderable numberText;

    public HeartBar(Vector2 topLeftCorner,
                    Vector2 heartSize,
                    Renderable heartImage,
                    int maxHearts,
                    int initialHearts,
                    GameObjectCollection gameObjects) {
        super(topLeftCorner, calculateBarDimensions(heartSize, maxHearts), null);
        this.gameObjects = gameObjects;
        this.hearts = new ArrayList<>();
        this.heartImage = heartImage;
        this.heartSize = heartSize;
        this.maxHearts = maxHearts;

        // Create numeric display in the first slot
        numberText = new TextRenderable(String.valueOf(initialHearts));
        numberText.setColor(COLOR_FULL);
        Vector2 numberPosition = topLeftCorner.getImmutableCopy();
        numberDisplay = new GameObject(numberPosition, heartSize, numberText);
        gameObjects.addGameObject(numberDisplay, Layer.UI);

        // Add initial hearts
        for (int i = 0; i < Math.min(initialHearts, maxHearts); i++) {
            addHeart();
        }

        updateNumberText();
    }

    public void addHeart() {
        if (hearts.size() < maxHearts) {
            Vector2 position = getTopLeftCorner().add(new Vector2(
                    (heartSize.x() + HEART_SPACING) * (hearts.size() + 1), 0)); // +1 to skip number
            GameObject heart = new GameObject(position, heartSize, heartImage);
            hearts.add(heart);
            gameObjects.addGameObject(heart, Layer.UI);
            updateNumberText();
        }
    }

    public void removeHeart() {
        if (!hearts.isEmpty()) {
            GameObject heart = hearts.remove(hearts.size() - 1);
            gameObjects.removeGameObject(heart, Layer.UI);
            updateNumberText();
        }
    }

    public int getCurrentHearts() {
        return hearts.size();
    }

    private static Vector2 calculateBarDimensions(Vector2 heartSize, int maxHearts) {
        int totalSlots = maxHearts + 1; // +1 for the number
        float width = totalSlots * heartSize.x() + (totalSlots - 1) * HEART_SPACING;
        float height = heartSize.y();
        return new Vector2(width, height);
    }

    private void updateNumberText() {
        int current = getCurrentHearts();
        numberText.setString(String.valueOf(current));

        if (current >= HEART_THRESHOLD_FULL) {
            numberText.setColor(COLOR_FULL);
        } else if (current == HEART_THRESHOLD_MEDIUM) {
            numberText.setColor(COLOR_MEDIUM);
        } else {
            numberText.setColor(COLOR_LOW);
        }

        numberDisplay.renderer().setRenderable(numberText);
    }
}
