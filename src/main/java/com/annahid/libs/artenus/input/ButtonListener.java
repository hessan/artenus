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
 * Interface for all classes that handle push button events.
 *
 * @author Hessan Feghhi
 */
public interface ButtonListener {
    /**
     * Called when a button is pressed.
     *
     * @param button The button that is pressed
     */
    void onPress(Button button);

    /**
     * Called when a button is released, regardless of whether or not the user meant to activate
     * the action associated with the button. This is usually the case for virtual buttons, where
     * the user has a choice to either complete the click, or lift their finger or the mouse
     * outside the button's hot region to cancel the event. If the button has an action associated
     * only with its release event and the interval between the press and the release does not have
     * any effect, it is recommended NOT to take that action if the {@code cancel} argument is true.
     *
     * @param button The button that is released
     * @param cancel true if the event is cancelled, false otherwise
     */
    void onRelease(Button button, boolean cancel);
}
