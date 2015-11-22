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

import com.annahid.libs.artenus.graphics.animation.AnimationHandler;

/**
 * Interface for all entities that can be animated. Sprites all implement this behavior.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.graphics.sprites.SpriteEntity
 */
public interface Animatable {
    /**
     * Gets the animation handler currently affecting this animatable.
     *
     * @return Animation handler
     */
    AnimationHandler getAnimation();

    /**
     * Assigns an animation handler to handle animations for this animatable.
     *
     * @param animation Animation handler, or {@code null} to remove the animation handler
     */
    void setAnimation(AnimationHandler animation);

    /**
     * Advances the animation for this animatable. This method is called once per animation frame.
     *
     * @param elapsedTime The amount of time since last call to this method
     */
    void advance(float elapsedTime);
}
