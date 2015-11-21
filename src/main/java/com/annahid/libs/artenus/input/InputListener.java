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
 * An interface for classes that response to input status changes.
 *
 * @author Hessan Feghhi
 */
public interface InputListener {
    /**
     * Signals this {@link InputListener} that a change has occurred in the input status. This can
     * be a slight change in the direction of the knob or the pressing or releasing of some action
     * buttons.
     *
     * @param input The calling input manager. Use this to retrieve information about the event
     */
    void inputStatusChanged(GameInput input);
}
