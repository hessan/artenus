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

import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.Effect;

/**
 * Interface for all entities that support rendering of visual content.
 *
 * @author Hessan Feghhi
 */
public interface Renderable {
    /**
     * Rendering flag: ignore color filtering, if present.
     *
     * @see Renderable#setColorFilter
     */
    int FLAG_IGNORE_COLOR_FILTER = 1;

    /**
     * Rendering flag: ignore effects, if present.
     *
     * @see com.annahid.libs.artenus.graphics.Effect
     */
    int FLAG_IGNORE_EFFECTS = 2;

    /**
     * Sets the color filter for this entity. The original colors should be multiplied by this color
     * when rendering.
     *
     * @param r The red multiplier
     * @param g The green multiplier
     * @param b The blue multiplier
     */
    void setColorFilter(float r, float g, float b);

    /**
     * Sets the color filter for this entity. The original colors should be multiplied by this color
     * when rendering.
     *
     * @param rgb The color multipliers
     */
    void setColorFilter(RGB rgb);

    /**
     * Gets the color filter for this entity. Modifying the fields in the returned value affects
     * the color filter.
     *
     * @return The color filter
     */
    @SuppressWarnings("unused")
    RGB getColorFilter();

    /**
     * Gets the effect currently assigned to this entity. Multiple effects can be chained together
     * to create a single effect.
     *
     * @return The effect chain currently assigned to this entity
     * @see com.annahid.libs.artenus.graphics.Effect
     */
    Effect getEffect();

    /**
     * Assigns an effect to this entity. Each entity can only have one effect assigned to it.
     * Multiple effects can be chained together to create a single effect.
     *
     * @see com.annahid.libs.artenus.graphics.Effect
     */
    void setEffect(Effect effect);

    /**
     * Sets the transparency value for this renderable. An alpha value of 1 indicates a fully opaque
     * renderable and a value of 0 is an invisible sprite. Any value in between can be specified to
     * achieve transparency.
     *
     * @param alpha The alpha value for transparency
     */
    void setAlpha(float alpha);

    /**
     * Gets the transparency value for this renderable.
     *
     * @return The alpha value for transparency
     */
    float getAlpha();

    /**
     * Renders the visual content.
     *
     * @param flags Rendering flags
     */
    void render(RenderingContext rc, int flags);
}
