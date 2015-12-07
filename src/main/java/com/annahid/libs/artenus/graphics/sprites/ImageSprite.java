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

package com.annahid.libs.artenus.graphics.sprites;

import com.annahid.libs.artenus.graphics.Texture;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Displays an image block on the screen. It provides tools for retrieving portions of a atlas
 * texture and handling frames for animations.
 *
 * @author Hessan Feghhi
 * @see SpriteEntity
 */
@SuppressWarnings("UnusedDeclaration")
public class ImageSprite extends SpriteEntity {
    /**
     * Holds current atlas frame being displayed by the image sprite.
     */
    private int currentFrame;

    /**
     * Holds the resource identifier of the atlas texture.
     */
    private int resId = -1;

    /**
     * Holds the atlas texture.
     */
    private Texture frames = null;

    /**
     * Holds the cutout.
     */
    private Cutout cutout = null;

    /**
     * Creates an {@code ImageSprite} with the given texture and {@code Cutout}. When
     * you create an {@code ImageSprite}, make sure that the corresponding texture exists
     * and is loaded beforehand. See {@link TextureManager} for more details.
     *
     * @param resourceId The resource identifier for the texture. This can be for an
     *                   ordinary image (png, jpeg, etc.) or an SVG file.
     * @param co         The cutout instructor to generate frames
     *
     * @see TextureManager
     */
    public ImageSprite(int resourceId, Cutout co) {
        super();
        cutout = co;
        currentFrame = 0;
        resId = resourceId;
    }

    /**
     * Gets the texture associated with this {@code ImageSprite}.
     *
     * @return The associated {@code Texture}
     */
    public Texture getTexture() {
        return frames;
    }

    /**
     * Sets the current frame for this {@code ImageSprite}. Frames are determined by
     * the associated {@code ImageSprite.Cutout}.
     *
     * @param index The frame index to change to
     */
    public void gotoFrame(int index) {
        currentFrame = index;
    }

    /**
     * Gets the current frame for this {@code ImageSprite}. Frames are determined by
     * the associated {@code ImageSprite.Cutout}.
     *
     * @return The current frame
     */
    public int getCurrentFrame() {
        return currentFrame;
    }

    @Override
    public void render(RenderingContext context, int flags) {
        if (frames == null) {
            if (cutout == null) {
                frames = TextureManager.getLoadingTexture();
                cutout = new Cutout(
                        TextureManager.getLoadingTextureWidth(),
                        TextureManager.getLoadingTextureHeight(),
                        1
                );
                if (!frames.isLoaded()) {
                    frames.waitLoad();
                }
            } else {
                frames = TextureManager.getTexture(resId);
            }
            return;
        }

        if (alpha != 0) {
            TextureShaderProgram program = (TextureShaderProgram) TextureManager.getShaderProgram();

            if ((flags & FLAG_PRESERVE_SHADER_PROGRAM) != 0) {
                if (context.getShader() instanceof TextureShaderProgram) {
                    program = (TextureShaderProgram) context.getShader();
                }
            }

            context.setShader(program);

            if (!cutout.isGenerated()) {
                cutout.generate(frames.getWidth(), frames.getHeight());
            }
            final float width = scale.x * cutout.fw, height = scale.y * cutout.fh;
            frames.prepare(program, cutout.textureBuffers[currentFrame]);
            context.setColorFilter(alpha * cf.r, alpha * cf.g, alpha * cf.b, alpha);
            frames.draw(context, pos.x, pos.y, width, height, rotation);
        }
    }

    /**
     * Describes how a texture is divided into image blocks for use in an {@code ImageSprite}. By
     * introducing a {@code ImageSprite.Cutout} object to an {@code ImageSprite}, you instruct it
     * how to build its frames based on the texture you provide.
     *
     * @author Hessan Feghhi
     */
    public static final class Cutout {
        /**
         * Contains generated texture coordinate buffers.
         */
        private FloatBuffer[] textureBuffers;

        /**
         * Holds frame (or block) width.
         */
        private float fw;

        /**
         * Holds frame (or block) height.
         */
        private float fh;

        /**
         * Holds the number of frames that will be cut out of the atlas horizontally.
         */
        private int fc;

        /**
         * Holds the number of frames that will be cut out of the atlas vertically.
         */
        private int fch;

        /**
         * Holds the x coordinate of the top-left corner of the first frame.
         */
        private int sx;

        /**
         * Holds the y coordinate of the top-left corner of the first frame.
         */
        private int sy;

        /**
         * Creates a cutout that divides the texture into blocks of the given dimensions and takes
         * out the given number of blocks horizontally from the texture. The remainder of the
         * texture will remain unused.
         *
         * @param frameWidth  The width of each block
         * @param frameHeight The height of each block
         * @param frameCount  The number of blocks to cut out of the image
         */
        public Cutout(float frameWidth, float frameHeight, int frameCount) {
            this(frameWidth, frameHeight, frameCount, 1, 0, 0);
        }

        /**
         * Creates a cutout that divides the texture into blocks of the given dimensions. You can
         * specify the number of columns and rows and it will cut out a grid of blocks from the
         * texture with the given information. The remainder of the texture will remain unused.
         *
         * @param frameWidth  Width of each block
         * @param frameHeight Height of each block
         * @param frameCountW Number of horizontal blocks
         * @param frameCountH Number of vertical blocks
         */
        public Cutout(float frameWidth, float frameHeight, int frameCountW, int frameCountH) {
            this(frameWidth, frameHeight, frameCountW, frameCountH, 0, 0);
        }

        /**
         * Creates a cutout with the information given. This constructor is an extension to the
         * {@code Cutout(float, float, int, int)} constructor that gives you the option to start at
         * a given point in the texture. This can be useful if you are using large atlas textures.
         *
         * @param frameWidth  Width of each block
         * @param frameHeight Height of each block
         * @param frameCountW Number of horizontal blocks
         * @param frameCountH Number of vertical blocks
         * @param startX      x coordinate of the starting pixel
         * @param startY      y coordinate of the starting pixel
         */
        public Cutout(float frameWidth, float frameHeight, int frameCountW, int frameCountH, int startX, int startY) {
            fw = frameWidth;
            fh = frameHeight;
            fc = frameCountW;
            fch = frameCountH;
            sx = startX;
            sy = startY;
        }

        /**
         * Gets the frame (block) width associated with this cutout.
         *
         * @return The frame width
         */
        public float getFrameWidth() {
            return fw;
        }

        /**
         * Gets the frame (block) height associated with this cutout.
         *
         * @return The frame height
         */
        public float getFrameHeight() {
            return fh;
        }

        /**
         * Determines whether the texture buffers for this cutout have already
         * been generated.
         *
         * @return {@code true} if buffers are generated or {@code false} otherwise
         */
        boolean isGenerated() {
            return textureBuffers != null;
        }

        /**
         * Generates texture coordinate buffers.
         *
         * @param w Perceived texture width
         * @param h Perceived texture height
         */
        void generate(int w, int h) {
            textureBuffers = new FloatBuffer[fc * fch];

            for (int indexh = 0; indexh < fch; indexh++) {
                for (int index = 0; index < fc; index++) {
                    final float x1 = (sx + fw * (float) index) / (float) w;
                    final float x2 = (sx + fw * (float) (index + 1)) / (float) w;
                    final float y1 = (sy + fh * (float) indexh) / (float) h;
                    final float y2 = (sy + fh * (float) (indexh + 1)) / (float) h;

                    final float texture[] = {
                            x1, y1,
                            x2, y1,
                            x1, y2,
                            x2, y2,
                    };

                    final ByteBuffer ibb = ByteBuffer.allocateDirect(texture.length * 4);
                    ibb.order(ByteOrder.nativeOrder());
                    final FloatBuffer textureBuffer = ibb.asFloatBuffer();
                    textureBuffer.put(texture);
                    textureBuffer.position(0);
                    textureBuffers[indexh * fc + index] = textureBuffer;
                }
            }
        }
    }
}
