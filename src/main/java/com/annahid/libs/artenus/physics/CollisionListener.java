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
 * Interface for classes that process physical collision events. An instance of this interface is
 * assigned to a {@link com.annahid.libs.artenus.physics.PhysicsSimulator} using the
 * {@code setCollisionListener} method and on each collision, the {@code onCollision} method is
 * called on the listener with information relating to the collision.
 *
 * @author Hessan Feghhi
 */
public interface CollisionListener {
    /**
     * Called whenever a collision happens in the physical world.
     *
     * @param info Information about the collision
     */
    void onCollision(CollisionInfo info);
}
