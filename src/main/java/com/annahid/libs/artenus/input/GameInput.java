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

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.entities.behavior.Behaviors;

/**
 * The base class for all game input controllers. A game input manager maps inputs from a specific
 * source to the unified Artenus game controller pattern. A virtual controller in this framework has
 * a single direction knob and four action keys. The direction knob can indicate any possible
 * direction in a circle centered at the knob.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class GameInput implements Entity {
    /**
     * Key identifier for the first action key.
     */
    public static final int KEY_ACTION1 = 16;

    /**
     * Key identifier for the second action key.
     */
    public static final int KEY_ACTION2 = 32;

    /**
     * Key identifier for the third action key.
     */
    public static final int KEY_ACTION3 = 64;

    /**
     * Key identifier for the fourth action key.
     */
    public static final int KEY_ACTION4 = 128;

    /**
     * Holds the current direction of the knob. Subclasses are allowed to adjust the coordinates of
     * this field according to user input. However, this variable should never be set to
     * {@code null} or it will cause application crash. To keep up with the standard operation of
     * the framework's input system subclasses should normalize the direction (so the length of the
     * direction vector is always 1 or zero).
     */
    protected Point2D direction;

    private Point2D savedDirection;

    private int keyMap, savedKeyMap;

    private InputListener l = null;

    /**
     * Creates an {@code GameInput} with all buttons unpressed and the directional knob at its rest
     * (0).
     */
    protected GameInput() {
        keyMap = 0;
        direction = new Point2D(0, 0);
        savedDirection = new Point2D(0, 0);
    }

    /**
     * Appoints an {@link InputListener} to respond to input status changes of this
     * {@code GameInput}. Each time the key map or the direction changes, the
     * listener is signaled to process the event.
     *
     * @param listener The new listener to be appointed, or {@code null} to remove the listener
     *
     * @see InputListener
     */
    public final void setListener(InputListener listener) {
        l = listener;
    }

    /**
     * Holds the key map to process new input. Subclasses must call this method before processing
     * input. Holding the key map helps determine whether the associated {@link InputListener}
     * should be called back. This method should be followed by {@link #releaseKeyMap()} in order to
     * complete the processing of input.
     *
     * @see InputListener
     */
    protected void holdKeyMap() {
        savedKeyMap = keyMap;
        savedDirection.x = direction.x;
        savedDirection.y = direction.y;
    }

    /**
     * Releases the previously held key map. Once this method is called, the new key map and knob
     * direction are compared to their corresponding values before holding. In case of any change,
     * the associated {@link InputListener} will be called back to respond to the change.
     */
    protected void releaseKeyMap() {
        if (l != null && (keyMap != savedKeyMap
                || direction.x != savedDirection.x || direction.y != savedDirection.y)) {
            l.inputStatusChanged(this);
        }
    }

    /**
     * Changes the status of a key or a combination of keys to pressed. Subclasses
     * should use this method to alter the key map. This method must be called
     * after a {@link #holdKeyMap()} call and before a {@link #releaseKeyMap()}
     * call. Otherwise the changes will not be reported
     *
     * @param keyCode The key identifier(s) to be pressed
     */
    protected void pressKeys(int keyCode) {
        keyMap |= keyCode;
    }

    /**
     * Changes the status of a key or a combination of keys to released. Subclasses
     * should use this method to alter the key map. This method must be called
     * after a {@link #holdKeyMap()} call and before a {@link #releaseKeyMap()}
     * call. Otherwise the changes will not be reported.
     *
     * @param keyCode The key identifier(s) to be released
     */
    protected void releaseKeys(int keyCode) {
        keyMap &= ~keyCode;
    }

    /**
     * Determines whether the key (or any of the multiple keys) specified are
     * currently in the pressed state.
     *
     * @param keyCode The key identifier(s) to check
     *
     * @return {@code true} if the key (or any of the keys} are pressed or {@code false} otherwise
     */
    public boolean isKeyPressed(int keyCode) {
        return (keyMap & keyCode) != 0;
    }

    /**
     * Gets the complete key map. The key map is a bit-set containing information
     * about the pressed state of the keys. If you {@code and} the return value with
     * any of the key identifiers, the result will be zero if the corresponding key
     * is not pressed, and non-zero otherwise.
     *
     * @return The key map bitwise integer
     */
    public int getKeyMap() {
        return keyMap;
    }

    /**
     * Gets the status of the direction knob.
     *
     * @return The direction vector. The length of the vector is normally either 1 or zero.
     */
    public final Point2D getDirection() {
        return direction;
    }

    /**
     * Registers this {@code GameInput} with the given scene. Some input
     * managers might need some initialization that needs context. This method is
     * where they should do those procedures. All input managers must implement
     * this method. This method is called internally and you must not invoke it
     * manually.
     *
     * @param scene The scene that will use this input manager to handle inputs
     */
    @Override
    public abstract void onAttach(Scene scene);

    /**
     * Unregisters this {@code GameInput} and releases resources associated to
     * it. This method is called internally and you must not invoke it manually.
     *
     * @param scene The scene that will use this input manager to handle inputs
     */
    @Override
    public abstract void onDetach(Scene scene);

    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return false;
    }
}
