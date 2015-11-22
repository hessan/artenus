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

package com.annahid.libs.artenus.graphics.rendering;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Represents a graphics output object used for rendering. Artenus does not render frames directly
 * onto the screen. Instead, it first draws them into intermediate objects, which are then used for
 * post-processing. The final render target is always the screen, which is handled internally.
 *
 * @author Hessan Feghhi
 */
public class RenderTarget {
    /**
     * Holds OpenGL ES render buffer handle.
     */
    private int renderBufferHandle;

    /**
     * Holds OpenGL ES frame buffer handle.
     */
    private int frameBufferHandle;

    /**
     * Holds OpenGL ES texture handle.
     */
    private int textureHandle;

    /**
     * Holds frame buffer width.
     */
    private int fboWidth;

    /**
     * Holds frame buffer height.
     */
    private int fboHeight;

    /**
     * Holds the viewport corresponding to this render target.
     */
    private Viewport viewport;

    /**
     * Holds the texture coordinate buffer for the working area of this render target.
     */
    private FloatBuffer frameTexCoords;

    /**
     * Creates a new instance of {@code RenderTarget}.
     *
     * @param fboWidth  Frame buffer width
     * @param fboHeight Frame buffer height
     */
    private RenderTarget(int fboWidth, int fboHeight) {
        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;
        this.viewport = new Viewport(fboWidth, fboHeight);
    }

    /**
     * Creates a new render target with given width and height. Once created, these dimensions
     * cannot change. But the rendering and filtering procedures can use a portion of the image.
     *
     * @param width  Image width
     * @param height Image height
     *
     * @return The rendering target, or {@code null} in case of an error
     */
    public static RenderTarget create(int width, int height) {
        RenderTarget ret = new RenderTarget(width, height);

        final int[] temp = new int[1];
        GLES20.glGenFramebuffers(1, temp, 0);
        ret.frameBufferHandle = temp[0];

        GLES20.glGenTextures(1, temp, 0);
        ret.textureHandle = temp[0];

        GLES20.glGenRenderbuffers(1, temp, 0);
        ret.renderBufferHandle = temp[0];

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, ret.frameBufferHandle);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ret.textureHandle);
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        );
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                width,
                height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                null
        );
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, ret.renderBufferHandle);
        GLES20.glFramebufferRenderbuffer(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER,
                ret.renderBufferHandle
        );
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        ret.generateTextureCoords();
        return ret;
    }

    /**
     * Gets the frame buffer handle. This handle is used internally to bind the target to the
     * rendering context.
     *
     * @return Frame buffer handle
     */
    public int getFrameBufferHandle() {
        return frameBufferHandle;
    }

    /**
     * Gets the texture handle to the rendering result. This handle can then be fed to a shader
     * program to draw the result on another render target.
     *
     * @return Texture image handle
     */
    public int getTextureHandle() {
        return textureHandle;
    }

    /**
     * Gets texture coordinates which define the sub-image containing render results. This should be
     * fed to the shader program along with the texture handle.
     *
     * @return Texture coordinates
     *
     * @see #getTextureHandle()
     */
    public FloatBuffer getTextureCoords() {
        return frameTexCoords;
    }

    /**
     * Gets the viewport associated with this render target.
     *
     * @return The viewport
     */
    public Viewport getViewport() {
        return viewport;
    }

    /**
     * Sets the viewport for this render target.
     *
     * @param viewport Frame setup
     */
    public void setViewport(Viewport viewport) {
        if (viewport != null) {
            this.viewport = viewport;
            generateTextureCoords();
        }
    }

    /**
     * Gets the maximum width of the render target, which is the width it has been created with.
     *
     * @return Maximum width in pixels
     */
    public int getWidth() {
        return fboWidth;
    }

    /**
     * Gets the maximum height of the render target, which is the height it has been created with.
     *
     * @return Maximum height in pixels
     */
    public int getHeight() {
        return fboHeight;
    }

    /**
     * Frees all resources created with this render target. This method must be called whenever the
     * rendering context is lost or reset.
     */
    public void dispose() {
        final int[] temp = new int[1];
        temp[0] = renderBufferHandle;
        GLES20.glDeleteRenderbuffers(1, temp, 0);
        temp[0] = frameBufferHandle;
        GLES20.glDeleteFramebuffers(1, temp, 0);
        temp[0] = textureHandle;
        GLES20.glDeleteTextures(1, temp, 0);
    }

    /**
     * Generates texture coordinates to represent the active sub-image.
     */
    private void generateTextureCoords() {
        final float x2 = viewport.getWidth() / (float) fboWidth;
        final float y2 = viewport.getHeight() / (float) fboHeight;
        final float texture[] = {
                0, 0,
                x2, 0,
                0, y2,
                x2, y2,
        };
        final ByteBuffer ibb = ByteBuffer.allocateDirect(texture.length * 4);
        ibb.order(ByteOrder.nativeOrder());
        final FloatBuffer textureBuffer = ibb.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.position(0);
        frameTexCoords = textureBuffer;
    }
}
