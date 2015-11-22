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

package com.annahid.libs.artenus.internal.core;

import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.sprites.ImageSprite;
import com.annahid.libs.artenus.graphics.sprites.LineSprite;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;

/**
 * Renders the loading interface.
 *
 * @author Hessan Feghhi
 */
final class LoadingGraphics {
    /**
     * Holds the background color of the loading graphics.
     */
    private final RGB clearColor = new RGB(0, 0, 0);

    /**
     * Holds the loading progress bar sprite.
     */
    private LineSprite loadingBarSprite;

    /**
     * Holds the loading display sprite.
     */
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
