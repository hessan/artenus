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
 * Represents a circular {@link com.annahid.libs.artenus.physics.Shape} that can
 * be used for physical simulation.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class CircleShape implements Shape {
	/**
	 * Type value representing a circular shape.
	 *
	 * @see Shape#getType()
	 */
	public static final int TYPE = 0;

	/**
	 * Creates a {@code CircleShape} using the given radius.
	 *
	 * @param radius The radius of the circle
	 */
	public CircleShape(float radius) {
		r = radius;
	}

	/**
	 * Gets the radius associated to this {@code CircleShape}.
	 *
	 * @return The radius of the circle
	 */
	public float getRadius() {
		return r;
	}

	/**
	 * Gets the type of this {@link com.annahid.libs.artenus.physics.Shape}.
	 * @return {@link #TYPE}
	 */
	@Override
	public int getType() {
		return TYPE;
	}

	@Override
	public Object createInternal() {
		org.jbox2d.collision.shapes.Shape shape = new org.jbox2d.collision.shapes.CircleShape();
		shape.m_radius = r / PhysicsSimulator.pixelsPerMeter;
		return shape;
	}

	private float r;
}
