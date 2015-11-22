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

package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.Viewport;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * Represents a post-processing filter that adds a ghosting effect to the rendered frame.
 *
 * @author Hessan Feghhi
 */
public class GhostingFilter implements PostProcessingFilter {
    /**
     * Holds the scale of the ghost image.
     */
    private float amount;

    /**
     * Gets the amount of ghosting, which corresponds to the scaling of the ghost image.
     *
     * @return Ghosting amount
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Sets the amount of ghosting, which corresponds to the scaling of the ghost image. For best
     * results use a value between 0 and 1.
     *
     * @param amount Ghosting amount
     */
    public void setAmount(float amount) {
        this.amount = amount;
    }

    /**
     * Returns {@code false}, as this filter only has one pass.
     *
     * @param pass  Current pass number (starting at 0)
     * @param setup Frame setup for the previous pass
     *
     * @return {@code false}
     */
    @Override
    public boolean setup(int pass, FilterPassSetup setup) {
        return false;
    }

    @Override
    public void render(int pass, RenderingContext context, RenderTarget renderedFrame) {
        Viewport fs = renderedFrame.getViewport();
        final TextureShaderProgram program =
                (TextureShaderProgram) TextureManager.getShaderProgram();
        final float w = context.getWidth(), h = context.getHeight();
        context.setShader(program);
        GLES20.glViewport(0, 0, fs.getWidth(), fs.getHeight());
        context.setColorFilter(1, 1, 1, 1);
        context.pushMatrix();
        context.identity();
        program.feed(renderedFrame.getTextureHandle());
        program.feedTexCoords(renderedFrame.getTextureCoords());
        context.translate(w / 2, h / 2);
        context.rotate(0);
        context.scale(w, -h);
        context.rect();
        if (amount > 0) {
            context.scale(1.0f + amount, 1.0f + amount);
            context.setColorFilter(0.5f, 0.5f, 0.5f, 0.5f);
            context.rect();
        }
        context.popMatrix();
    }
}
