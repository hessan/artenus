package com.annahid.libs.artenus.internal.core;

import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.sprites.ImageSprite;
import com.annahid.libs.artenus.graphics.sprites.LineSprite;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;

/**
 * This class renders the loading interface.
 *
 * @author Hessan Feghhi
 */
final class LoadingGraphics {
    private final RGB clearColor = new RGB(0, 0, 0);
    private LineSprite loadingBarSprite;
    private ImageSprite loadingSprite;

    /**
     * Renews loading screen assets after a system reset.
     */
    void renew() {
        loadingSprite = null;
    }

    /**
     * Renders the "loading" screen on the given rendering context.
     */
    void render(RenderingContext ctx) {
        float vw = ctx.getWidth(), vh = ctx.getHeight();
        ctx.clear(clearColor.r, clearColor.g, clearColor.b);

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
     * Sets the background color of the loading screen.
     *
     * @param color The color
     */
    void setBackColor(RGB color) {
        clearColor.r = color.r;
        clearColor.g = color.g;
        clearColor.b = color.b;
    }
}
