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

package com.annahid.libs.artenus.graphics.filters;

import com.annahid.libs.artenus.graphics.rendering.Viewport;

/**
 * Holds information about the pass that is going to be rendered. Post-processing filters can
 * modify the frame size or other information about the frame for each pass. This class is used to
 * keep track of these changes, and pass it along the subsequent passes and filters.
 *
 * @author Hessan Feghhi
 */
public class FilterPassSetup extends Viewport {
    /**
     * Indicates whether the next pass is in-place.
     */
    private boolean inPlace = false;

    /**
     * Creates a new instance of {@code FilterPassSetup} based on a previous setup.
     *
     * @param viewport Viewport to copy dimensions from
     */
    public FilterPassSetup(Viewport viewport) {
        super(viewport.getWidth(), viewport.getHeight());
    }

    /**
     * Sets the frame width for this setup.
     *
     * @param width Frame width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Sets the frame height for this setup.
     *
     * @param height Frame height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Instructs the renderer that the next pass is going to be in-place. An in-place pass does not
     * redraw the whole frame, but rather overlays something on top of it. The normal rendering
     * behavior for post-processors is that the result of each pass is given to the next pass, and
     * the new pass draws on a fresh render target (at least theoretically). By calling this method,
     * both input and output targets are carried forward from the previous pass, and the output
     * target will still contain the previous results. In-place filters can greatly improve their
     * performance by notifying the renderer using this method.
     */
    public void setInPlace() {
        this.inPlace = true;
    }

    /**
     * Indicates whether the next pass is in-place.
     *
     * @return {@code true} if in-place, {@code false} otherwise
     */
    public boolean isInPlace() {
        return inPlace;
    }
}
