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

package com.annahid.libs.artenus.graphics.animation;

import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Renderable;

/**
 * Performs a fading effect animation for sprites. You can specify whether you want the sprite to
 * appear or disappear.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class FadeAnimation implements AnimationHandler {
    /**
     * Indicates whether this is a fade-in (and not a fade-out) animation.
     */
    private boolean fin;

    /**
     * Holds animation speed.
     */
    private float s;

    /**
     * Creates a {@code FadeAnimation} with the specified behavior.
     *
     * @param fadeIn Whether the handled sprite should appear (or fade in)
     * @param speed  The speed of fading in. Setting this value to 1 causes a fully hidden object
     *               to appear completely in one second. Higher values make this transition faster
     *               and lower values make it slower.
     */
    public FadeAnimation(boolean fadeIn, float speed) {
        fin = fadeIn;
        s = speed;
    }

    @Override
    public void advance(Animatable entity, float elapsedTime) {

        if (!(entity instanceof Renderable))
            return;

        final Renderable sprite = (Renderable) entity;
        final float alpha = sprite.getAlpha();

        if (fin) {
            if (alpha < 1)
                sprite.setAlpha(Math.min(1, alpha + elapsedTime * s));
            else entity.setAnimation(null);
        } else {
            if (alpha > 0)
                sprite.setAlpha(Math.max(0, alpha - elapsedTime * s));
            else entity.setAnimation(null);
        }
    }
}
