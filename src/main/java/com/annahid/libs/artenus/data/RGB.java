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

package com.annahid.libs.artenus.data;

/**
 * Represents a color with red, green, and blue components. This class does not
 * provide transparency since it is not to be used for that purpose.
 *
 * @author Hessan Feghhi
 */
public final class RGB {
    /**
     * The red component of the color represented by this {@code RGB} instance.
     */
    public float r;

    /**
     * The red component of the color represented by this {@code RGB} instance.
     */
    public float g;

    /**
     * The red component of the color represented by this {@code RGB} instance.
     */
    public float b;

    /**
     * Constructs a new {@code RGB} using the give red, green and blue components.
     *
     * @param cr The red component of the color
     * @param cg The green component of the color
     * @param cb The blue component of the color
     */
    public RGB(float cr, float cg, float cb) {
        r = cr;
        g = cg;
        b = cb;
    }

    /**
     * Constructs a new {@code RGB} using the color's string representation.
     *
     * @param rgb A hexadecimal string of the form rrggbb or RRGGBB
     */
    public RGB(String rgb) {
        r = Integer.parseInt(rgb.substring(0, 2), 16) / 256.0f;
        g = Integer.parseInt(rgb.substring(2, 4), 16) / 256.0f;
        b = Integer.parseInt(rgb.substring(4), 16) / 256.0f;
    }
}
