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

import com.annahid.libs.artenus.entities.behavior.Animatable;

/**
 * Interface for classes that handle animations. In order to make animations effective, you should
 * set their animation to an animation handler instance using
 * {@link com.annahid.libs.artenus.entities.behavior.Animatable#setAnimation(AnimationHandler)}.
 *
 * @author Hessan Feghhi
 */
public interface AnimationHandler {
    /**
     * Called whenever the animation should update the animatable based on elapsed time.
     *
     * @param animatable  The {@link Animatable} object to be updated
     * @param elapsedTime Elapsed time in seconds since the last frame
     * @see Entity
     */
    void advance(Animatable animatable, float elapsedTime);
}
