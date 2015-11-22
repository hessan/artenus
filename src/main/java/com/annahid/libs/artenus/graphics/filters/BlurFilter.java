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

import com.annahid.libs.artenus.core.Stage;
import com.annahid.libs.artenus.graphics.rendering.ShaderManager;
import com.annahid.libs.artenus.graphics.rendering.Viewport;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * Represents a post-processing filter that adds a blur effect to the rendered frame.
 *
 * @author Hessan Feghhi
 */
public class BlurFilter implements PostProcessingFilter {
    private static BlurShaderProgram program = new BlurShaderProgram();

    /**
     * Registers the blur shadedr program with the shader manager.
     */
    static {
        ShaderManager.register(program);
    }

    /**
     * Holds blur amount.
     */
    private float amount;

    /**
     * Holds the index of the last pass required to apply the given amount of blur.
     */
    private int lastPass = 1;

    /**
     * Holds saved frame setup that carries information from {@link #setup(int, FilterPassSetup)} to
     * {@link #render(int, RenderingContext, RenderTarget)}.
     */
    private Viewport savedSetup;

    /**
     * Gets the current blurring amount.
     *
     * @return Blurring amount
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Sets the blurring amount.
     *
     * @param amount Blurring amount
     */
    public void setAmount(float amount) {
        if (amount == 0)
            this.lastPass = 0;
        else this.lastPass = (int) amount / 5 * 2 + 1;
        this.amount = amount;
    }

    @Override
    public boolean setup(int pass, FilterPassSetup setup) {
        if (pass == 0) {
            if (amount > 5) {
                setup.setWidth(setup.getWidth() / 2);
                setup.setHeight(setup.getHeight() / 2);
            }
        }
        this.savedSetup = setup;
        return pass < lastPass;
    }

    @Override
    public void render(int pass, RenderingContext context, RenderTarget renderedFrame) {
        final float w = context.getWidth(), h = context.getHeight();
        float amount = (pass > lastPass - 2) ? this.amount % 5 : (pass < 2 ? 2.5f : 5);
        context.setShader(program);
        GLES20.glViewport(0, 0, savedSetup.getWidth(), savedSetup.getHeight());
        context.setColorFilter(1, 1, 1, 1);
        context.pushMatrix();
        context.identity();
        program.feed(renderedFrame.getTextureHandle());
        program.feedTexCoords(renderedFrame.getTextureCoords());
        program.feedAmount(amount);
        program.feed(w, h);
        program.feedPass(pass);
        context.translate(w / 2, h / 2);
        context.rotate(0);
        context.scale(w, -h);
        context.rect();
        context.popMatrix();
    }
}
