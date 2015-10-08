package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.core.Stage;
import com.annahid.libs.artenus.graphics.rendering.Viewport;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * A post-processing filter that adds a blur effect to the rendered frame.
 *
 * @author Hessan Feghhi
 */
public class BlurFilter implements PostProcessingFilter {
    private static BlurShaderProgram program = new BlurShaderProgram();

    /**
     * Blur amount.
     */
    private float amount;

    /**
     * The index of the last pass required to apply the given amount of blur.
     */
    private int lastPass = 1;

    /**
     * Saved frame setup that carries information from {@link #setup(int, FilterPassSetup)} to
     * {@link #render(int, RenderingContext, RenderTarget)}.
     */
    private Viewport savedSetup;

    public static void init(Stage stage) {
        stage.registerShader(program);
    }

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
