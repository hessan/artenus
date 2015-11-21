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

package com.annahid.libs.artenus.graphics.rendering;

/**
 * Represents a viewport for a render target. A viewport is an area on the frame that is used for
 * rendering. The rest of the available area on the target will not be touched.
 */
public class Viewport {
    /**
     * Holds viewport width.
     */
    protected int width;

    /**
     * Holds viewport height.
     */
    protected int height;

    /**
     * Creates a new viewport with given width and height.
     *
     * @param width  Viewport width
     * @param height Viewport height
     */
    public Viewport(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the frame width in this viewport.
     *
     * @return Frame width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the frame height for this viewport.
     *
     * @return Frame height
     */
    public int getHeight() {
        return this.height;
    }
}
