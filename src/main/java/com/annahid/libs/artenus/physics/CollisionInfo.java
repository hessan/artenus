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
 * Contains information about a collision. An instance of this class
 * is passed to the {@code onCollision} method of the {@link CollisionListener}
 * interface per each pair of colliding bodies.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class CollisionInfo {
	CollisionInfo() { }

	/**
	 * The first body involved in the collision.
	 */
	public PhysicalBody body1;

	/**
	 * The second body involved in the collision.
	 */
	public PhysicalBody body2;

	/**
	 * An array of normal impulses for the collision.
	 */
	public float[] normalImpulses;

	/**
	 * An array of tangent impulses for the collision.
	 */
	public float[] tangentImpulses;

	/**
	 * The number of impulses included in this collision.
	 */
	public int impulseCount;

	/**
	 * The tangent relative speed of colliding bodies.
	 */
	public float tangentSpeed;
}
