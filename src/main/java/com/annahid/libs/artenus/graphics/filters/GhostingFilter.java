package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.FrameSetup;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * A post-processing filter that adds a ghosting effect to the rendered frame.
 *
 * @author Hessan Feghhi
 */
public class GhostingFilter implements PostProcessingFilter {
    /**
     * Scale of the ghost image.
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

    @Override
    public boolean setup(int pass, FrameSetup setup) {
        return false;
    }

    @Override
    public void render(int pass, RenderingContext context, RenderTarget renderedFrame) {
        FrameSetup fs = renderedFrame.getFrameSetup();
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
