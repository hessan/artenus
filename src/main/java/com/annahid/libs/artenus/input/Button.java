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

/**
 * Represents a button. This can be a visual touch button on the screen, a physical button on a
 * game-pad, or anything else that can produce or simulate press and release events.
 *
 * @author Hessan Feghhi
 */
public interface Button {
    /**
     * Gets the current listener responsible for this button's push and release events.
     */
    ButtonListener getListener();

    /**
     * Assigns a listener to this button to handle its push and release events.
     *
     * @param listener The listener
     */
    void setListener(ButtonListener listener);

    /**
     * Returns a value indicating whether the button is currently in pressed state.
     *
     * @return {@code true} if the button is pressed, {@code false} otherwise
     */
    boolean isPressed();
}
