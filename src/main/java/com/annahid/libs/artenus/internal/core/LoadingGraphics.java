package com.annahid.libs.artenus.internal.core;

import android.opengl.GLES20;

import com.annahid.libs.artenus.core.RenderingContext;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.sprites.ImageSprite;
import com.annahid.libs.artenus.graphics.sprites.LineSprite;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;

class LoadingGraphics implements Renderable {
    private final RGB clearColor = new RGB(0, 0, 0);
    private LineSprite loadingBarSprite;
    private ImageSprite loadingSprite;

    /**
     * Renews loading screen assets after a system reset.
     */
    public void renew() {
        loadingSprite = null;
    }

    /**
     * Renders the "loading" screen on the given rendering context.
     */
    @Override
    public void render(RenderingContext ctx, int flags) {
        float vw = ctx.getWidth(), vh = ctx.getHeight();
        GLES20.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (!TextureManager.getLoadingTexture().isLoaded())
            TextureManager.getLoadingTexture().waitLoad();

        ctx.setShader(TextureManager.getShaderProgram());

        if (loadingSprite == null) {
            loadingSprite = new ImageSprite(-1, null);
            loadingSprite.setPosition(vw / 2, vh / 2);

            loadingBarSprite = new LineSprite(
                    new Point2D(0, vh),
                    new Point2D(
                            vw * TextureManager.getLoadedCount() / TextureManager.getTextureCount(),
                            vh
                    ), 3);
            loadingBarSprite.setAlpha(0.75f);
        }

        loadingSprite.render(ctx, 0);
        loadingBarSprite.setEndPoint(
                vw * TextureManager.getLoadedCount() / TextureManager.getTextureCount(), vh
        );
        loadingBarSprite.render(ctx, 0);
        TextureManager.getShaderProgram().cleanup();
    }

    /**
     * Gets the current background color of the loading screen.
     *
     * @return The background color
     */
    @Override
    public RGB getColorFilter() {
        return clearColor;
    }

    /**
     * Sets the background color of the loading screen.
     *
     * @param r The red component
     * @param g The green component
     * @param b The blue component
     */
    @Override
    public void setColorFilter(float r, float g, float b) {
        clearColor.r = r;
        clearColor.g = g;
        clearColor.b = b;
    }

    /**
     * Sets the background color of the loading screen.
     *
     * @param rgb The background color
     */
    @Override
    public void setColorFilter(RGB rgb) {
        setColorFilter(rgb.r, rgb.g, rgb.b);
    }

    @Override
    public Effect getEffect() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public void setEffect(Effect effect) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public void setAlpha(float alpha) {
        throw new UnsupportedOperationException("This method is not implemented.");
    }

    @Override
    public float getAlpha() {
        throw new UnsupportedOperationException("This method is not implemented.");
    }
}
