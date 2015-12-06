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
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * <p>Provides a grid of texture blocks. It is intended for platform or map based games. You can
 * create a large grid and add it to the scene as a normal sprite. Only the portion of the grid that
 * is visible will be rendered. Use this sprite for level maps.</p>
 * <p>Note that this class is subject to revision or removal in the future. It is NOT yet
 * deprecated. Support for platform-based games is currently being revised and a faster and more
 * robust framework will be introduced in future versions.</p>
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("unused")
public final class GridSprite extends SpriteEntity {
    /**
     * Holds texture atlas.
     */
    private Texture frames;

    /**
     * Contains atlas image indices for the grid blocks.
     */
    private int[][] blocks;

    /**
     * Contains alpha transparency values for the blocks.
     */
    private float[][] alphas;

    /**
     * Contains texture buffers for atlas images.
     */
    private FloatBuffer[] textureBuffers = null;

    /**
     * Holds block width.
     */
    private float blkW;

    /**
     * Holds block height.
     */
    private float blkH;

    /**
     * Holds the thickness of the overlapping area around blocks, in pixels.
     */
    private float m;

    /**
     * Holds column index of the top-left block in the visible region.
     */
    private int vc1;

    /**
     * Holds row index of the top-left block in the visible region.
     */
    private int vr1;

    /**
     * Holds column index of the bottom-right block in the visible region.
     */
    private int vc2;

    /**
     * Holds the row index of the bottom-right block in the visible region.
     */
    private int vr2;

    /**
     * Creates a {@code GridSprite} using a image resource id and block parameters.
     * Like {@code Sprite}, this class supports SVG images with {@code raw} resource type
     * as well as images normally supported by the android platform. It is recommended to
     * provide SVG images for better scaling in different screen resolutions. The image
     * is cut out into frames using the given block width and height.
     *
     * @param resourceId  The resource identifier for the image
     * @param frameWidth  The block width
     * @param frameHeight The block height
     * @param margin      The margin for each image block. It is recommended to have a transparent
     *                    margin for each block to avoid color mixing. This parameter specifies the
     *                    size of this margin. Grid blocks are squeezed together with this parameter
     *                    to diminish the transparent margin.
     * @param cols        The number of columns for this grid
     * @param rows        The number of rows for this grid
     */
    public GridSprite(
            int resourceId,
            float frameWidth,
            float frameHeight,
            float margin,
            int cols,
            int rows
    ) {
        blkW = frameWidth;
        blkH = frameHeight;
        frames = TextureManager.getTexture(resourceId);
        blocks = new int[cols][rows];
        alphas = new float[cols][rows];
        m = margin;
    }

    /**
     * Gets the transparency value for the given grid block.
     *
     * @param col The column number of the block
     * @param row The row number of the block
     *
     * @return The transparency (alpha) value
     */
    public float getAlpha(int col, int row) {
        try {
            return alphas[col][row];
        } catch (Exception ex) {
            return 1;
        }
    }

    /**
     * Sets the transparency value of the given grid block.
     *
     * @param col   The column number of the block
     * @param row   The row number of the block
     * @param alpha The new transparency (alpha) value
     */
    public void setAlpha(int col, int row, float alpha) {
        try {
            alphas[col][row] = alpha;
        } catch (Exception ex) {
            // Do nothing
        }
    }

    /**
     * Gets the current frame (image block index) of the given grid block. To
     * understand image frames, see the documentation for {@code Sprite}.
     *
     * @param col The column number of the block
     * @param row The row number of the block
     *
     * @return The current frame
     *
     * @see SpriteEntity
     */
    public int getFrame(int col, int row) {
        return blocks[col][row];
    }

    /**
     * Sets the current frame of the given grid block. To understand image frames,
     * see the documentation for {@code Sprite}.
     *
     * @param col   The column number of the block
     * @param row   The row number of the block
     * @param frame The new frame number
     *
     * @see SpriteEntity
     */
    public void setFrame(int col, int row, int frame) {
        blocks[col][row] = frame;
    }

    /**
     * Gets the number of columns for this grid.
     *
     * @return The number of columns
     */
    public int getColumns() {
        return blocks.length;
    }

    /**
     * Gets the number of rows for this grid.
     *
     * @return The number of rows
     */
    public int getRows() {
        return blocks[0].length;
    }

    /**
     * Gets the assigned width for this grid.
     *
     * @return The width of the grid in pixels
     */
    public float getWidth() {
        return blocks.length * blkW;
    }

    /**
     * Gets the assigned height for this grid.
     *
     * @return The height of the grid in pixels
     */
    public float getHeight() {
        return blocks[0].length * blkH;
    }

    /**
     * Sets the visible area within the grid block. The renderer will not render anything that lies
     * outside the rectangular are specified. Use this method to achieve better performance. Note
     * that there is no guarantee that the contents are clipped by the given area.
     *
     * @param startX The x coordinate of the region top-left corner
     * @param startY The y coordinate of the region top-left corner
     * @param endX   The x coordinate of the region bottom-right corner
     * @param endY   The y coordinate of the region bottom-right corner
     */
    public void setVisibleRegion(float startX, float startY, float endX, float endY) {
        this.vc1 = Math.min(blocks.length, Math.max(toGridX(startX) - 1, 0));
        this.vr1 = Math.min(blocks[0].length, Math.max(toGridY(startY) - 1, 0));
        this.vc2 = Math.min(blocks.length, Math.max(toGridX(endX) + 1, 0));
        this.vr2 = Math.min(blocks[0].length, Math.max(toGridY(endY) + 1, 0));
    }

    @Override
    public void render(RenderingContext context, int flags) {
        if (textureBuffers == null) {
            textureBuffers = new FloatBuffer[(int) (frames.getWidth() / blkW)];

            for (int i = 0; i < textureBuffers.length; i++) {
                final float x1 = blkW * (float) i / (float) frames.getWidth();
                final float x2 = (blkW * (float) (i + 1) - 1) / (float) frames.getWidth();
                final float y2 = blkH / (float) frames.getHeight();
                final float texture[] = {
                        x1, 0,
                        x2, 0,
                        x1, y2,
                        x2, y2
                };
                final ByteBuffer ibb = ByteBuffer.allocateDirect(texture.length * 4);
                ibb.order(ByteOrder.nativeOrder());
                textureBuffers[i] = ibb.asFloatBuffer();
                textureBuffers[i].put(texture);
                textureBuffers[i].position(0);
            }
        }

        final float bw = blkW - m * 2, bh = blkH - m * 2;
        final int[][] blocks = this.blocks;

        if ((flags & FLAG_PRESERVE_SHADER_PROGRAM) == 0)
            context.setShader(TextureManager.getShaderProgram());
        final TextureShaderProgram program =
                (TextureShaderProgram) TextureManager.getShaderProgram();

        program.feed(frames.getTextureHandle());
        context.pushMatrix();
        context.rotate(rotation);

        for (int i = vc1; i < vc2; i++) {
            for (int j = vr1; j < vr2; j++) {
                final float a = alpha * alphas[i][j];
                context.setColorFilter(a, a, a, a);
                context.pushMatrix();
                context.translate(((float) i + 0.5f) * bw, ((float) j + 0.5f) * bh);
                context.scale(blkW, blkH);
                program.feedTexCoords(textureBuffers[blocks[i][j]]);
                context.rect();
                context.popMatrix();
            }
        }
        context.popMatrix();
    }

    /**
     * Converts a pixel coordinate into the corresponding column number on the grid.
     *
     * @param x The x coordinate
     *
     * @return The resulting column number
     */
    private int toGridX(float x) {
        return (int) Math.ceil(x / (blkW - m * 2) - 0.5);
    }

    /**
     * Converts a pixel coordinate into the corresponding row number on the grid.
     *
     * @param y The y coordinate
     *
     * @return The resulting row number
     */
    private int toGridY(float y) {
        return (int) Math.ceil(y / (blkH - m * 2) - 0.5);
    }
}
