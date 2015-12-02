/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

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
 * <p>Manages game input, using slide gestures as the direction knob and touch buttons as action
 * keys. Note that you still need to add the touch buttons to the scene, and assigning them to
 * action keys only manages event handling.</p>
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
     * Contains buttons currently assigned to different action keys.
     */
    private final Map<Integer, Button> buttons = new HashMap<>(5);

    /**
     * Holds the reference point for the direction knob, which is the point where the user first
     * touches.
     */
    private final Point2D reference = new Point2D(0, 0);

    /**
     * Holds the pointer identifier for the reference point.
     */
    private int startId = -1;

    /**
     * Holds the threshold below which the direction is not picked up by the knob.
     */
    private int threshold;

    /**
     * Holds the reference to the stage instance.
     */
    private Stage stage;

    /**
     * Holds the x coordinate of the top-left corner of the effective knob area.
     */
    private float x1;

    /**
     * Holds the y coordinate of the top-left corner of the effective knob area.
     */
    private float y1;

    /**
     * Holds the x coordinate of the bottom-right corner of the effective knob area.
     */
    private float x2;

    /**
     * Holds the y coordinate of the bottom-right corner of the effective knob area.
     */
    private float y2;

    /**
     * Assigns a touch button to an action key for this input manager.
     *
     * @param key    The action key to associate with
     * @param button The touch button to be added (or null to disassociate)
     *
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
     * Gets the x coordinate of the reference point, which is the first point the user touched to
     * trigger the direction knob. This point is maintained until the next gesture.
     *
     * @return The x component of the reference point.
     */
    public float getRefX() {
        return reference.x;
    }

    /**
     * Gets the y coordinate of the reference point, which is the first point the user touched to
     * trigger the direction knob. This point is maintained until the next gesture.
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
     * Handles touch events for this {@code SlideInput}.
     */
    @Override
    public boolean handleTouch(TouchEvent event) {
        if (stage.getScene().isHalted()) {
            return false;
        }
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
                float x = event.x - reference.x, y = event.y - reference.y;
                if (Math.abs(x) < threshold) {
                    x = 0;
                }
                if (Math.abs(y) < threshold) {
                    y = 0;
                }
                if (x != 0 || y != 0) {
                    final float size = (float) Math.sqrt(x * x + y * y);
                    x /= size;
                    y /= size;
                }
                direction.x = x;
                direction.y = y;
                releaseKeyMap();
            }
        }

        return false;
    }

    /**
     * Sets the rectangular region on the screen where the knob input is captured.
     *
     * @param x      x coordinate of the top-left corner of the region
     * @param y      y coordinate of the top-left corner of the region
     * @param width  Width of the region
     * @param height Height of the region
     */
    public void setEffectiveArea(float x, float y, float width, float height) {
        x1 = x;
        y1 = y;
        x2 = x + width;
        y2 = y + height;
    }

    /**
     * Called every time a touch gesture is lifted.
     *
     * @param pointerId Identifier of the pointer involved
     */
    private void checkRelease(int pointerId) {
        if (pointerId == startId) {
            startId = -1;
            holdKeyMap();
            direction.setZero();
            releaseKeyMap();
        }
    }
}
