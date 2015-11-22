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

package com.annahid.libs.artenus.graphics;

import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * Superclass for all visual effects that can be added to a sprite. Currently there is only one
 * effect available, but there will be more added in the future. Each entity can only have a single
 * effect assigned to it through the {@link Renderable#setEffect(Effect)} method. Multiple effects
 * must be added as a chain, using the {@link com.annahid.libs.artenus.graphics.Effect#over} method.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.entities.Entity
 */
public abstract class Effect {
    /**
     * Holds the base effect that should be applied before this effect.
     */
    protected Effect baseEffect;

    /**
     * Chains another effect with this effect. The renderer will process this effect after it is
     * done processing the chained effect.
     *
     * @param effect The effect to be chained
     *
     * @return The chained effect, which is normally the same as the argument
     */
    @SuppressWarnings("unused")
    public Effect over(Effect effect) {
        baseEffect = effect;
        return effect;
    }

    /**
     * Renders the renderable, applying the effect, and taking the alpha value the renderable
     * prefers to be rendered with.
     *
     * @param renderable renderable to be drawn using this effect
     * @param alpha      preferred alpha value
     */
    public abstract void render(RenderingContext context, Renderable renderable, float alpha);
}
