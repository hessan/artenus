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
 * Interface for 2-dimensional physical shapes.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.physics.PhysicalBody
 */
public interface Shape {
    /**
     * Gets the type of this {@code Shape}. Each shape defines its own type value, which is stored
     * as the constant {@code TYPE} within the corresponding class.
     *
     * @return The type of the shape
     */
    int getType();

    /**
     * Creates an internal representation of this {@code Shape}. The exact type of the returned
     * object depends on the physics simulation engine used internally.
     *
     * @return Box2D representation of this {@code Shape}
     */
    Object createInternal();
}
