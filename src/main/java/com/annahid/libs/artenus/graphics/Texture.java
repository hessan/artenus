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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.nio.FloatBuffer;

/**
 * Represents a texture. A texture is an image that can be used in real-time graphics libraries. All
 * images must be converted to instances of this class before they can be used in this framework.
 *
 * @author Hessan Feghhi
 */
public class Texture {
    /**
     * Holds the texture identifier associated with this {@code Texture}.
     */
    protected int textureId;

    /**
     * Holds the width of this {@code Texture} in pixels.
     */
    protected int width;

    /**
     * Holds the height of this {@code Texture} in pixels.
     */
    protected int height;

    /**
     * Holds the resource identifier of the image to be loaded into this {@code Texture}.
     */
    int resId;

    /**
     * Indicates whether the image is being read.
     */
    boolean loading;

    /**
     * Holds a temporary bitmap used to store the image before it is loaded into the EGL texture.
     */
    private Bitmap bmp = null;

    /**
     * Creates a new {@code Texture} with the given image.
     *
     * @param resourceId The resource identifier of the image
     */
    Texture(int resourceId) {
        resId = resourceId;
        textureId = -1;
    }

    /**
     * Creates a new OpenGL texture identifier. This identifier is required to load the texture in
     * OpenGL ES.
     *
     * @return The texture identifier
     */
    private static int newTextureID() {
        int[] temp = new int[1];
        GLES20.glGenTextures(1, temp, 0);
        return temp[0];
    }

    /**
     * Loads the texture in the foreground. Use this method if you need a texture to load
     * immediately for a specific purpose. For most applications, Artenus pipeline would
     * automatically handle texture loading and life cycle.
     */
    public void waitLoad() {
        loadImage();
        loadEGL();
    }

    /**
     * Prepares the OpenGL context for rendering this {@code Texture}. This method must be called
     * before calling {@link #draw(RenderingContext, float, float, float, float, float)} for correct
     * rendering. However, it is recommended that you do not call any of these methods directly. The
     * framework handles low-level rendering.
     *
     * @param textureBuffer The texture buffer
     */
    public final void prepare(FloatBuffer textureBuffer) {
        final TextureShaderProgram program =
                (TextureShaderProgram) TextureManager.getShaderProgram();

        program.feed(textureId);
        program.feedTexCoords(textureBuffer);
    }

    /**
     * Renders this {@code Texture} on the given OpenGL context in the given rectangular region. It
     * is recommended that you do not call this method directly.
     *
     * @param x   x coordinate of the region to draw the texture
     * @param y   y coordinate of the region to draw the texture
     * @param w   Width of the region to draw the texture
     * @param h   Height of the region to draw the texture
     * @param rot Rotation angle of the rectangular region
     */
    public final void draw(RenderingContext ctx, float x, float y, float w, float h, float rot) {
        ctx.pushMatrix();
        ctx.translate(x, y);
        ctx.rotate(rot);
        ctx.scale(w, h);
        ctx.rect();
        ctx.popMatrix();
    }

    /**
     * Indicates whether the texture is loaded. The texture can be displayed only when it is loaded.
     * You normally don't need to check this directly and the framework takes all the required
     * precautions not to allow you to use unloaded textures.
     *
     * @return {@code true} if loaded, or {@code false} otherwise
     */
    public final boolean isLoaded() {
        return textureId >= 0;
    }

    /**
     * Gets the width of the {@code Texture}. This will be the processed width, so if you are using
     * an SVG texture, it will indicate the power-of-two estimation of the
     * width.
     *
     * @return The width of the texture
     */
    public final int getWidth() {
        return width;
    }

    /**
     * Gets the height of the {@code Texture}. This will be the processed height, so if you are
     * using an SVG texture, it will indicate the power-of-two estimation of the height.
     *
     * @return The width of the texture
     */
    public final int getHeight() {
        return height;
    }

    /**
     * Unloads this {@code Texture}. It is highly recommended that you do not call this method
     * directly, as it might cause problems. {@code TextureManager} takes the responsibility for
     * loading and unloading textures whenever required.
     *
     * @see TextureManager
     */
    public final void destroy() {
        GLES20.glDeleteTextures(1, new int[] { textureId }, 0);
        textureId = -1;
    }

    /**
     * Gets the OpenGL ES texture identifier associated with this texture. Applications of this
     * method are rare and it is recommended not to use this method.
     *
     * @return The OpenGL ES texture identifier
     */
    public final int getTextureHandle() {
        return textureId;
    }

    /**
     * Performs the loading process on the OpenGL ES side.
     *
     * @return {@code true} if processing is possible, {@code false} otherwise
     */
    boolean loadEGL() {
        if (bmp == null || bmp.isRecycled())
            return false;

        textureId = newTextureID();

        try {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
            GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MIN_FILTER,
                    GLES20.GL_LINEAR
            );
            GLES20.glTexParameterf(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_MAG_FILTER,
                    GLES20.GL_LINEAR
            );
            GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_S,
                    GLES20.GL_CLAMP_TO_EDGE
            );
            GLES20.glTexParameteri(
                    GLES20.GL_TEXTURE_2D,
                    GLES20.GL_TEXTURE_WRAP_T,
                    GLES20.GL_CLAMP_TO_EDGE
            );
        } catch (Exception ex) {
            textureId = -1;
        }

        if (textureId >= 0)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

        bmp.recycle();
        bmp = null;

        if (GLES20.glGetError() != GLES20.GL_NO_ERROR)
            destroy();

        return true;
    }

    /**
     * Loads the image resource and prepares the bitmap for {@link #loadEGL()}.
     */
    void loadImage() {
        loading = true;

        final Resources res = Artenus.getInstance().getResources();
        final boolean isSVG = res.getResourceTypeName(resId).equalsIgnoreCase("raw");

        final float texScale = TextureManager.getTextureScalingFactor();

        if (isSVG) {
            // Load the SVG file from the given resource.
            final SVG svg = SVGParser.getSVGFromResource(res, resId);
            final Picture pic = svg.getPicture();

			/*
             * If it is the loading texture, keep the original width and height of the texture.
			 * These values are needed to display the loading texture correctly on the screen,
			 * and we need to save them now because they will change later on in the method to
			 * be powers of two.
			 */
            if (this == TextureManager.getLoadingTexture()) {
                TextureManager.loadingTexW = pic.getWidth();
                TextureManager.loadingTexH = pic.getHeight();
            }

            float scaledWidth =
                    pic.getWidth() * texScale, scaledHeight = pic.getHeight() * texScale;
            final float frWidth = (float)
                    Math.pow(2, Math.ceil(Math.log(scaledWidth) / Math.log(2))) / scaledWidth;
            final float frHeight = (float)
                    Math.pow(2, Math.ceil(Math.log(scaledHeight) / Math.log(2))) / scaledHeight;

            scaledWidth *= frWidth;
            scaledHeight *= frHeight;
            width = Math.round(scaledWidth / texScale);
            height = Math.round(scaledHeight / texScale);

            final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            final Bitmap tempBmp =
                    Bitmap.createBitmap(Math.round(scaledWidth), Math.round(scaledHeight), conf);
            final Canvas canvas = new Canvas(tempBmp);
            canvas.scale(texScale, texScale);
            canvas.drawPicture(pic);
            bmp = tempBmp;
        } else {
            final BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inScaled = false;
            final Bitmap tempBmp = BitmapFactory.decodeResource(res, resId, opts);

            if (tempBmp != null) {
                width = tempBmp.getWidth();
                height = tempBmp.getHeight();

                if (this == TextureManager.getLoadingTexture()) {
                    TextureManager.loadingTexW = width;
                    TextureManager.loadingTexH = height;
                }
            }

            bmp = tempBmp;
        }

        loading = false;
    }
}
