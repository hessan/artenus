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

package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.entities.behavior.Behaviors;

/**
 * Interface for all entities that can be added to a scene. Entities provide various functionality,
 * including graphical, physical, or user interaction. A {@link Scene} is made up of a list of
 * entities, and it uses them for processing graphics, animation, and user input.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.core.Scene
 */
public interface Entity {
    /**
     * Called when this entity is associated with a scene.
     *
     * @param scene The scene that currently contains the entity.
     */
    void onAttach(Scene scene);

    /**
     * Called when this entity is dissociated with a scene.
     *
     * @param scene The scene from which the entity is removed.
     */
    void onDetach(Scene scene);

    /**
     * Indicates whether this entity has the specified behavior. If it does, it can be cast to the
     * corresponding interface. Note that the return value can be transient and a fixed return value
     * for a behavior is not guaranteed by the framework.
     *
     * @param behavior Behavior to be checked
     * @return {@code true} if this entity has the behavior, {@code false} otherwise
     * @see Behaviors
     */
    boolean hasBehavior(Behaviors behavior);
}
