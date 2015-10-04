package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.FrameSetup;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * A post-processing filter that adds a blur effect to the rendered frame.
 *
 * @author Hessan Feghhi
 */
public class BlurFilter implements PostProcessingFilter {
    /**
     * Blur amount.
     */
    private float amount;

    /**
     * Saved frame setup that carries information from {@link #setup(int, FrameSetup)} to
     * {@link #render(int, RenderingContext, RenderTarget)}.
     */
    private FrameSetup savedSetup;

    /**
     * Gets the current blurring amount.
     *
     * @return Blurring amount
     */
    public float getAmount() {
        return amount;
    }

    /**
     * Sets the blurring amount. The minimum blurring amount is 1, which corresponds to a normal
     * image. Any value below this will be replaced by 1.
     *
     * @param amount Blurring amount
     */
    public void setAmount(float amount) {
        this.amount = Math.max(1, amount);
    }

    @Override
    public boolean setup(int pass, FrameSetup setup) {
        setup.setWidth((int) Math.max(1.0f, (float) setup.getWidth() / amount));
        setup.setHeight((int) Math.max(1.0f, (float) setup.getHeight() / amount));
        this.savedSetup = setup;
        return false;
    }

    @Override
    public void render(int pass, RenderingContext context, RenderTarget renderedFrame) {
        final float w = context.getWidth(), h = context.getHeight();
        final TextureShaderProgram program =
                (TextureShaderProgram) TextureManager.getShaderProgram();
        context.setShader(program);
        GLES20.glViewport(0, 0, savedSetup.getWidth(), savedSetup.getHeight());
        context.setColorFilter(1, 1, 1, 1);
        context.pushMatrix();
        context.identity();
        program.feed(renderedFrame.getTextureHandle());
        program.feedTexCoords(renderedFrame.getTextureCoords());
        context.translate(w / 2, h / 2);
        context.rotate(0);
        context.scale(w, -h);
        context.rect();
        context.popMatrix();
    }
}
