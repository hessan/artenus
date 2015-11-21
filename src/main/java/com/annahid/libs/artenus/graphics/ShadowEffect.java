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
 * Represents a shadow effect.
 *
 * @author Hessan Feghhi
 */
public final class ShadowEffect extends Effect {
    /**
     * Holds the horizontal distance of the shadow from the renderable entity.
     */
    private float dx;

    /**
     * Holds the vertical distance of the shadow from the renderable entity.
     */
    private float dy;

    /**
     * Holds the alpha transparency value of the shadow.
     */
    private float shadowAlpha;

    /**
     * Creates a new shadow effect with specified properties.
     *
     * @param dx          Horizontal distance of the shadow
     * @param dy          Vertical distance of the shadow
     * @param shadowAlpha The transparency value of the shadow
     */
    public ShadowEffect(float dx, float dy, float shadowAlpha) {
        this.dx = dx;
        this.dy = dy;
        this.shadowAlpha = shadowAlpha;
    }

    @Override
    public void render(RenderingContext context, Renderable renderable, float alpha) {
        context.pushMatrix();
        context.translate(dx, dy);
        context.setColorFilter(0, 0, 0, alpha * shadowAlpha);

        if (baseEffect == null) {
            renderable.render(
                    context,
                    Renderable.FLAG_IGNORE_COLOR_FILTER | Renderable.FLAG_IGNORE_EFFECTS
            );
        } else baseEffect.render(context, renderable, alpha);

        context.popMatrix();

        if (baseEffect == null)
            renderable.render(context, Renderable.FLAG_IGNORE_EFFECTS);
        else baseEffect.render(context, renderable, alpha);
    }
}
