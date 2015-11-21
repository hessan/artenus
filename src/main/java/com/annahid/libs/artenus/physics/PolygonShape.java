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

import com.annahid.libs.artenus.data.Point2D;

import org.jbox2d.common.Vec2;

/**
 * Represents a {@link com.annahid.libs.artenus.physics.Shape} consisting of a convex
 * polygon.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class PolygonShape implements Shape {

    /**
     * Type value representing a polygonal shape.
     *
     * @see Shape#getType()
     */
    public static final int TYPE = 2;

    /**
     * Contains corners of the polygon.
     */
    private Point2D[] pts;

    /**
     * Constructs a {@code PolygonShape} using the convex hull of the given points.
     *
     * @param points The array of points making the desired polygon
     */
    public PolygonShape(Point2D[] points) {
        pts = points;
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
        final Vec2[] points = new Vec2[pts.length];

        for (int i = 0; i < pts.length; i++)
            points[i] = new Vec2(
                    pts[i].x / PhysicsSimulator.pixelsPerMeter,
                    pts[i].y / PhysicsSimulator.pixelsPerMeter);

        shape.set(points, pts.length);
        return shape;
    }
}
