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

package com.annahid.libs.artenus.entities.behavior;

/**
 * Entity behaviors. Each entity can support one or more of these behaviors.
 */
public enum Behaviors {
    /**
     * Behavior value for animatable entities. An entity that declares this behavior must also
     * implement {@link Animatable}.
     */
    ANIMATABLE,

    /**
     * Behavior value for renderable entities. An entity that declares this behavior must also
     * implement {@link Renderable}.
     */
    RENDERABLE,

    /**
     * Behavior value for entities that accept touch events. An entity that declares this behavior
     * must also implement {@link Touchable}.
     */
    TOUCHABLE,

    /**
     * Behavior value for entities that can be moved, rotated, or scaled. An entity that declares
     * this behavior must also implement {@link Transformable}.
     */
    TRANSFORMABLE
}
