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

package com.annahid.libs.artenus.core;

/**
 * Events that can happen during the stage's lifecycle. The stage notifies its manager whenever any
 * of these events occur.
 *
 * @author Hessan Feghhi
 * @see StageManager
 */
public enum StageEvents {
    /**
     * Indicates that the stage has gone into a paused state. This event is triggered when the
     * Artenus activity goes in the background.
     */
    PAUSE(1),
    /**
     * Indicates that the stage has resumed from a paused state. This event is triggered when the
     * Artenus activity resumes from a background state.
     */
    RESUME(2),
    /**
     * Indicates that the stage has just allocated resources to display content. This event is
     * triggered when the OpenGL view's {@code onSurfaceCreated} method is invoked.
     */
    DISPLAY(3);

    /**
     * The current value of this enumeration.
     */
    private final int value;

    /**
     * Creates a new stage event.
     *
     * @param value Event
     */
    StageEvents(int value) {
        this.value = value;
    }

    /**
     * Gets the current value of this enumeration.
     *
     * @return Current value
     */
    public int getValue() {
        return value;
    }
}
