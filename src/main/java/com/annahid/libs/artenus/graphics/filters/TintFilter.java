package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.FrameSetup;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * A post-processing filter that overlays the rendered frame with a colored tint.
 *
 * @author Hessan Feghhi
 */
public class TintFilter implements PostProcessingFilter {
    /**
     * The red component of the color filter.
     */
    private float r = 0.0f;

    /**
     * The green component of the color filter.
     */
    private float g = 0.0f;

    /**
     * The blue component of the color filter.
     */
    private float b = 0.0f;

    /**
     * The alpha transparency component of the color filter.
     */
    private float a = 0.0f;

    /**
     * Sets the current tint.
     *
     * @param r The red component of the tint
     * @param g The green component of the tint
     * @param b The blue component of the tint
     * @param a The alpha transparency of the tint
     */
    public void setTint(float r, float g, float b, float a) {
        this.r = r * a;
        this.g = g * a;
        this.b = b * a;
        this.a = a;
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
        context.setShader(null);
        context.setColorFilter(r, g, b, a);
        context.rect();
        context.popMatrix();
    }
}
