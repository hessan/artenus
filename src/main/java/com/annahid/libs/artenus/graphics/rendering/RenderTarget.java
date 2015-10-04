package com.annahid.libs.artenus.graphics.rendering;

import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * An graphics output object used for rendering. Artenus does not render frames directly onto the
 * screen. Instead, it first draws them into intermediate objects, which are then used for
 * post-processing. The final render target is always the screen, which is handled internally.
 *
 * @author Hessan Feghhi
 */
public class RenderTarget {
    private int renderBufferHandle;
    private int frameBufferHandle;
    private int textureHandle;
    private int fboWidth;
    private int fboHeight;
    private FrameSetup setup;
    private FloatBuffer frameTexCoords;

    /**
     * Creates a new render target with given width and height. Once created, these dimensions
     * cannot change. But the rendering and filtering procedures can use a portion of the image.
     *
     * @param width  Image width
     * @param height Image height
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
     * Creates a new instance of {@code RenderTarget}.
     *
     * @param fboWidth  Frame buffer width
     * @param fboHeight Frame buffer height
     */
    private RenderTarget(int fboWidth, int fboHeight) {
        this.fboWidth = fboWidth;
        this.fboHeight = fboHeight;
        this.setup = new FrameSetup();
        reset();
    }

    /**
     * Resets the rendering setup associated with this render target.
     */
    public void reset() {
        this.setup.setWidth(fboWidth);
        this.setup.setHeight(fboHeight);
    }

    /**
     * Binds this target to the rendering context. All drawing will then be done on this target.
     */
    public void begin() {
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                textureHandle,
                0
        );
        GLES20.glViewport(0, 0, setup.getWidth(), setup.getHeight());
    }

    /**
     * Unbinds this target from the rendering context.
     */
    public void end() {
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
    }

    /**
     * Gets the texture handle to the rendering result. This handle can then be fed to a shader
     * program to draw the result on another render target.
     *
     * @return Texture image
     */
    public int getTextureHandle() {
        return textureHandle;
    }

    /**
     * Gets texture coordinates which define the sub-image containing render results. This should be
     * fed to the shader program along with the texture handle.
     *
     * @return Texture coordinates
     * @see #getTextureHandle()
     */
    public FloatBuffer getTextureCoords() {
        return frameTexCoords;
    }

    /**
     * Gets the frame setup associated with this render target. You should not modify the contents
     * of this setup manually. The framework provides the frame setup directly wherever it can be
     * modified.
     *
     * @return The frame setup
     */
    public FrameSetup getFrameSetup() {
        return setup;
    }

    /**
     * Sets the frame setup for this render target. The information in the argument will be copied
     * locally, and future modifications to the argument instance will not affect the setup for this
     * render target.
     *
     * @param setup Frame setup
     */
    public void setFrameSetup(FrameSetup setup) {
        if (setup != null) {
            this.setup.setWidth(setup.getWidth());
            this.setup.setHeight(setup.getHeight());
            generateTextureCoords();
        }
    }

    /**
     * Gets the maximum width of the render target, which is the width it has been created with.
     *
     * @return Maximum width in pixels
     */
    public int getMaxWidth() {
        return fboWidth;
    }

    /**
     * Gets the maximum height of the render target, which is the height it has been created with.
     *
     * @return Maximum height in pixels
     */
    public int getMaxHeight() {
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
        final float x2 = (float) setup.getWidth() / (float) fboWidth;
        final float y2 = (float) setup.getHeight() / (float) fboHeight;
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
