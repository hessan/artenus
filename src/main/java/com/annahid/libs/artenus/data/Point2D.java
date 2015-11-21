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
 * Represents a 2-dimensional point and is used throughout this framework for positions, scaling
 * factors, impulses, and other properties of objects.
 *
 * @author Hessan Feghhi
 */
public final class Point2D {
    /**
     * The x component of the point represented by this {@code Point2D} instance.
     */
    public float x;

    /**
     * The y component of the point represented by this {@code Point2D} instance.
     */
    public float y;

    /**
     * Constructs a {@code Point2D} with the given components.
     *
     * @param px The x component of the point
     * @param py The y component of the point
     */
    public Point2D(float px, float py) {
        x = px;
        y = py;
    }

    /**
     * Sets both x and y components of this object to zero.
     */
    public void setZero() {
        x = y = 0;
    }

    /**
     * Multiplies a scalar value by both x and y components of this object and
     * returns the result. The values in the original {@code Point2D} object are
     * not modified and the result is a new {@code Point2D} instance.
     *
     * @param scalar The scalar to multiply
     * @return The resulting {@code Point2D} object which represents the new values
     */
    public Point2D multiply(float scalar) {
        return new Point2D(x * scalar, y * scalar);
    }
}
