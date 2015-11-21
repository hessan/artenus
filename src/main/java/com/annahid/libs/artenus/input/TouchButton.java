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

import com.annahid.libs.artenus.entities.behavior.Behaviors;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.FilteredEntity;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.entities.behavior.Transformable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles touch events for the underlying entity.
 *
 * @author Hessan Feghhi
 */
public class TouchButton extends FilteredEntity implements Button, Touchable {
    /**
     * Holds a counter used to generate button identifiers for the touch map.
     */
    private static AtomicInteger idStore = new AtomicInteger(100);

    /**
     * Holds button identifier assigned to this touch button.
     */
    int id;

    /**
     * Holds the state of the transformation matrix for the latest render call.
     */
    private float[] latestMatrix = null;

    /**
     * Indicates whether the button is currently in pressed state.
     */
    private boolean down = false;

    /**
     * Holds the identifier for the pointer currently pressing the button down.
     */
    private int downId = -1;

    /**
     * Holds the event handler currently listening to events from this touch button.
     */
    private ButtonListener listener = null;

    /**
     * Creates a new touch button that adds button behavior to the underlying entity.
     *
     * @param target The underlying entity
     */
    public TouchButton(Entity target) {
        super(target);
        id = (int) (byte) (idStore.incrementAndGet() % 256);

        if (id == 0)
            id++;

        if (!target.hasBehavior(Behaviors.TRANSFORMABLE)) {
            throw new IllegalArgumentException("Target entity is not transformable.");
        }
        if (!target.hasBehavior(Behaviors.RENDERABLE)) {
            throw new IllegalArgumentException(
                    "Target entity is not renderable. Artenus uses graphical" +
                            "representations of objects to track touch events."
            );
        }
        if (target instanceof TouchButton) {
            throw new IllegalArgumentException(
                    "Nested touch buttons are not supported by the framework."
            );
        }
    }

    @Override
    public void onAttach(Scene scene) {
        super.onAttach(scene);
        scene.getTouchMap().registerButton(this);
    }

    @Override
    public void onDetach(Scene scene) {
        super.onDetach(scene);
        scene.getTouchMap().unregisterButton(this);
    }

    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return behavior == Behaviors.TOUCHABLE || super.hasBehavior(behavior);
    }

    /**
     * Handles touch events sent through the touch map pipeline.
     *
     * @param action    The action
     * @param pointerId The pointer identifier associated with the action
     */
    void internalTouch(int action, int pointerId) {
        if (action == TouchEvent.EVENT_DOWN) {
            if (!down) {
                down = true;
                downId = pointerId;

                if (listener != null) {
                    listener.onPress(this);
                }
            }
        } else if (action != TouchEvent.EVENT_MOVE && down && pointerId == downId) {
            if (listener != null) {
                listener.onRelease(this, action == TouchEvent.EVENT_LEAVE);
            }
            down = false;
            downId = -1;
        }
    }

    /**
     * Always returns false. Touch buttons do not participate in the normal touch event pipeline.
     * Their touch event is handled through the touch map associated with the scene.
     *
     * @param event Event information
     *
     * @return {@code false}
     */
    @Override
    public final boolean handleTouch(TouchEvent event) {
        return false;
    }

    /**
     * Returns the state of the transformation matrix for the last render call, and removes it
     * from memory.
     *
     * @return Transformation matrix
     */
    float[] popLatestMatrix() {
        float[] ret = latestMatrix;
        latestMatrix = null;
        return ret;
    }

    @Override
    public void render(RenderingContext ctx, int flags) {
        latestMatrix = ctx.getMatrix();
        if (target.hasBehavior(Behaviors.RENDERABLE)) {
            ((Renderable) target).render(ctx, flags);
        }
    }

    /**
     * Returns a value indicating whether this touch button is currently in pressed state.
     *
     * @return {@code true} if pressed, {@code false} otherwise
     */
    @Override
    public boolean isPressed() {
        return down;
    }

    /**
     * Gets the button listener currently assigned to this touch button.
     *
     * @return The listener
     */
    @Override
    public ButtonListener getListener() {
        return listener;
    }

    /**
     * Sets the button listener for this touch button.
     *
     * @param listener The listener
     */
    @Override
    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }
}
