package com.annahid.libs.artenus.input;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.behavior.Behaviors;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.core.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>A subclass of {@link GameInput} that uses slide motions as the direction knob and touch
 * buttons as action keys. Note that you still need to add the touch buttons to the scene, and
 * assigning them to action keys only manages event handling.</p>
 * <p>The way the directional knob works in {@code SlideInput} is that whenever the user touches a
 * point in the screen where there is no action button, that point becomes the center of the knob,
 * or the reference point. Now sliding away from the reference point indicates the direction of
 * choice. If the user slides their finger to the left side of the reference point (the first point
 * they touched), the knob will report a left direction and same holds for every other direction.
 * The knob goes back to neutral as soon as the gesture is finished.</p>
 *
 * @author Hessan Feghhi
 * @see TouchButton
 */
@SuppressWarnings("unused")
public final class SlideInput extends GameInput implements Touchable {
    /**
     * Buttons currently assigned to different action keys.
     */
    private final Map<Integer, Button> buttons = new HashMap<>(5);
    private final Point2D reference = new Point2D(0, 0);
    private int startId = -1;
    private int threshold;
    private Stage stage;
    private float x1, y1, x2, y2;

    /**
     * Assigns a touch button to an action key for this input manager.
     *
     * @param key    The action key to associate with
     * @param button The touch button to be added (or null to disassociate)
     * @see com.annahid.libs.artenus.core.Stage
     */
    public void setButton(final int key, Button button) {
        if (button == null) {
            button = buttons.get(key);

            if (button != null) {
                button.setListener(null);
                buttons.remove(key);
            }

            return;
        }
        button.setListener(new ButtonListener() {
            @Override
            public void onPress(Button button) {
                holdKeyMap();
                pressKeys(key);
                releaseKeyMap();
            }

            @Override
            public void onRelease(Button button, boolean cancel) {
                holdKeyMap();
                releaseKeys(key);
                releaseKeyMap();
            }
        });
        buttons.put(key, button);
    }

    public Button getButton(int key) {
        return buttons.get(key);
    }

    /**
     * Gets the x coordinate of the reference point, which is the first
     * point the user touched to trigger the direction knob. This point
     * is maintained until the next gesture.
     *
     * @return The x component of the reference point.
     */
    public float getRefX() {
        return reference.x;
    }

    /**
     * Gets the y coordinate of the reference point, which is the first
     * point the user touched to trigger the direction knob. This point
     * is maintained until the next gesture.
     *
     * @return The y component of the reference point.
     */
    public float getRefY() {
        return reference.y;
    }

    /**
     * Registers this {@code SlideInput} manager to the given context.
     */
    @Override
    public void onAttach(Scene scene) {
        stage = scene.getStage();
        threshold = (int) (10
                * Artenus.getInstance().getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    public void onDetach(Scene scene) {
        stage = null;
    }

    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return behavior == Behaviors.TOUCHABLE;
    }

    /**
     * Handles touch events for {@code SlideInput}.
     */
    @Override
    public boolean handleTouch(TouchEvent event) {
        if (stage.getScene().isHalted())
            return false;

        if (event.action == TouchEvent.EVENT_DOWN) {
            if (startId < 0 && event.x > x1 && event.x < x2 && event.y > y1 && event.y < y2) {
                startId = event.pointerId;
                reference.x = event.x;
                reference.y = event.y;
            }
        } else if (event.action == TouchEvent.EVENT_UP) {
            checkRelease(event.pointerId);
        } else {
            if (startId >= 0) {
                holdKeyMap();
                direction.x = event.x - reference.x;
                direction.y = event.y - reference.y;
                if (Math.abs(direction.x) < threshold) direction.x = 0;
                if (Math.abs(direction.y) < threshold) direction.y = 0;

                if (direction.x != 0 || direction.y != 0) {
                    float size = (float)
                            Math.sqrt(direction.x * direction.x + direction.y * direction.y);
                    direction.x /= size;
                    direction.y /= size;
                }
                releaseKeyMap();
            }
        }

        return false;
    }

    public void setEffectiveArea(float x, float y, float width, float height) {
        x1 = x;
        y1 = y;
        x2 = x + width;
        y2 = y + height;
    }

    private void checkRelease(int pointerId) {
        if (pointerId == startId) {
            startId = -1;
            holdKeyMap();
            direction.setZero();
            releaseKeyMap();
        }
    }
}
