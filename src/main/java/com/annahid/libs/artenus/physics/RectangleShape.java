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

package com.annahid.libs.artenus.physics;

/**
 * Represents a rectangular {@link com.annahid.libs.artenus.physics.Shape} that
 * can be used for physical simulation.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class RectangleShape implements Shape {
    /**
     * Type value representing a rectangular shape.
     *
     * @see Shape#getType()
     */
    public static final int TYPE = 1;

    /**
     * Holds rectangle width.
     */
    private float w;

    /**
     * Holds rectangle height.
     */
    private float h;

    /**
     * Creates a {@code RectangleShape} with given dimensions.
     *
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     */
    public RectangleShape(float width, float height) {
        w = width;
        h = height;
    }

    /**
     * Gets the type of this {@link com.annahid.libs.artenus.physics.Shape}.
     *
     * @return {@link #TYPE}
     */
    @Override
    public int getType() {
        return TYPE;
    }

    @Override
    public Object createInternal() {
        final org.jbox2d.collision.shapes.PolygonShape shape =
                new org.jbox2d.collision.shapes.PolygonShape();
        shape.setAsBox(w / PhysicsSimulator.pixelsPerMeter, h / PhysicsSimulator.pixelsPerMeter);
        return shape;
    }
}
