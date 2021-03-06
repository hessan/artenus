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

/**
 * Interface for all entities that support rendering of visual content.
 *
 * @author Hessan Feghhi
 */
public interface Renderable {

    /**
     * Rendering flag: ignore effects, if present.
     */
    int FLAG_PRESERVE_SHADER_PROGRAM = 2;

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
     * Gets the color filter for this entity. Modifying the fields in the returned value affects
     * the color filter.
     *
     * @return The color filter
     */
    @SuppressWarnings("unused")
    RGB getColorFilter();

    /**
     * Sets the color filter for this entity. The original colors should be multiplied by this color
     * when rendering.
     *
     * @param rgb The color multipliers
     */
    void setColorFilter(RGB rgb);

    /**
     * Gets the transparency value for this renderable.
     *
     * @return The alpha value for transparency
     */
    float getAlpha();

    /**
     * Sets the transparency value for this renderable. An alpha value of 1 indicates a fully opaque
     * renderable and a value of 0 is an invisible sprite. Any value in between can be specified to
     * achieve transparency.
     *
     * @param alpha The alpha value for transparency
     */
    void setAlpha(float alpha);

    /**
     * Renders the visual content.
     *
     * @param flags Rendering flags
     */
    void render(RenderingContext rc, int flags);
}
