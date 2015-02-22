package com.annahid.libs.artenus.entities.sprites;

import android.opengl.GLES10;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.graphics.Texture;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.ui.Scene;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * <p>This class is an implementation of {@link SpriteEntity} that provides a grid of texture blocks.
 * It is intended for platform or map based games. You can create a large grid and add it to
 * the scene as a normal sprite. Only the portion of the grid that is visible will be rendered.
 * Use this sprite for level maps.</p>
 * <p>Please note that this class is subject to revision or removal in the future. It is NOT
 * yet deprecated. Support for platform-based games is currently being revised and a faster and
 * more robust framework will be introduced in future versions.</p>
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("unused")
public final class ImageGridSprite extends SpriteEntity {
	private Texture frames;
	private int[][] blocks;
	private float[][] alphas;
	private float blkW, blkH, w, h, m;
	private Scene myScene;
	private FloatBuffer[] textureBuffers = null;

	/**
	 * Constructs a {@code ImageGridSprite} using a image resource id and block parameters.
	 * Like {@code Sprite}, this class supports SVG images with {@code raw} resource type
	 * as well as images normally supported by the android platform. It is recommended to
	 * provide SVG images for better scaling in different screen resolutions. The image
	 * is cut out into frames using the given block width and height.
	 *
	 * @param scene       The scene this {@code ImageGridSprite} belongs to.
	 * @param resourceId  The resource identifier for the image.
	 * @param frameWidth  The block width.
	 * @param frameHeight The block height.
	 * @param margin      The margin for each image block. It is recommended to have a
	 *                    transparent margin for each block to avoid color mixing. This parameter specifies
	 *                    the size of this margin. Grid blocks are squeezed together with this parameter to
	 *                    diminish the transparent margin.
	 * @param cols        The number of columns for this grid.
	 * @param rows        The number of rows for this grid.
	 */
	public ImageGridSprite(
			Scene scene, int resourceId,
			float frameWidth, float frameHeight,
			float margin, int cols, int rows) {
		blkW = frameWidth;
		blkH = frameHeight;
		frames = TextureManager.getTexture(resourceId);
		blocks = new int[cols][rows];
		alphas = new float[cols][rows];
		myScene = scene;
		w = 200;
		h = 200;
		m = margin;
	}

	/**
	 * Gets the transparency value for the given grid block.
	 *
	 * @param col The column number of the block.
	 * @param row The row number of the block.
	 * @return The transparency (alpha) value.
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
	 * @param col   The column number of the block.
	 * @param row   The row number of the block.
	 * @param alpha The new transparency (alpha) value.
	 */
	public void setAlpha(int col, int row, float alpha) {
		try {
			alphas[col][row] = alpha;
		} catch (Exception ex) {
			// Do nothing
		}
	}

	/**
	 * Adjusts the width and height limits for this grid. This creates an imaginary
	 * window cornered at the top-right corner of the screen. Blocks outside this
	 * window will not be rendered.
	 *
	 * @param width  The width of the imaginary window.
	 * @param height The height of the imaginary window.
	 */
	public void setLimits(float width, float height) {
		w = width;
		h = height;
	}

	/**
	 * Gets the current frame (image block index) of the given grid block. To
	 * understand image frames, see the documentation for {@code Sprite}.
	 *
	 * @param col The column number of the block.
	 * @param row The row number of the block.
	 * @return The current frame.
	 * @see SpriteEntity
	 */
	public int getFrame(int col, int row) {
		return blocks[col][row];
	}

	/**
	 * Sets the current frame of the given grid block. To understand image frames,
	 * see the documentation for {@code Sprite}.
	 *
	 * @param col   The column number of the block.
	 * @param row   The row number of the block.
	 * @param frame The new frame number.
	 * @see SpriteEntity
	 */
	public void setFrame(int col, int row, int frame) {
		blocks[col][row] = frame;
	}

	/**
	 * Gets the number of columns for this grid.
	 *
	 * @return The number of columns.
	 */
	public int getColumns() {
		return blocks.length;
	}

	/**
	 * Gets the number of rows for this grid.
	 *
	 * @return The number of rows.
	 */
	public int getRows() {
		return blocks[0].length;
	}

	/**
	 * Gets the assigned width for this grid.
	 *
	 * @return The width of the grid in pixels.
	 */
	public float getWidth() {
		return blocks.length * blkW;
	}

	/**
	 * Gets the assigned height for this grid.
	 *
	 * @return The height of the grid in pixels.
	 */
	public float getHeight() {
		return blocks[0].length * blkH;
	}

	@Override
	public void render(int flags) {
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

		if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0) {
			effect.render(this, alpha);
			return;
		}

		final Point2D trans = myScene.translationVector();
		final int startX = Math.max(0, Math.min(toGridX(-trans.x), blocks.length));
		final int startY = Math.max(0, Math.min(toGridY(-trans.y), blocks[0].length));
		final float bw = blkW - m * 2, bh = blkH - m * 2;
		final int endX = Math.min(blocks.length, startX + (int) (w / bw) + 2);
		final int endY = Math.min(blocks[0].length, startY + (int) (h / bh) + 2);
		final int[][] blocks = this.blocks;

		GLES10.glEnable(GL10.GL_TEXTURE_2D);
		GLES10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glBindTexture(GL10.GL_TEXTURE_2D, frames.getInternalTextureId());
		GLES10.glPushMatrix();
		GLES10.glRotatef(rotation, 0, 0, 1);

		for (int i = startX; i < endX; i++)
			for (int j = startY; j < endY; j++) {
				final float a = alpha * alphas[i][j];
				GLES10.glColor4f(a, a, a, a);
				GLES10.glPushMatrix();
				GLES10.glTranslatef(((float) i + 0.5f) * bw, ((float) j + 0.5f) * bh, 0);
				GLES10.glScalef(blkW, blkH, 0);
				GLES10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffers[blocks[i][j]]);
				GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				GLES10.glPopMatrix();
			}

		GLES10.glPopMatrix();
	}

	/**
	 * Converts a pixel coordination into the corresponding column number on the grid.
	 *
	 * @param x The x coordination value.
	 * @return The resulting column number.
	 */
	private int toGridX(float x) {
		return (int) ((x - blkW / 2) / blkW + 0.5f);
	}

	/**
	 * Converts a pixel coordination into the corresponding row number on the grid.
	 *
	 * @param y The y coordination value.
	 * @return The resulting row number.
	 */
	private int toGridY(float y) {
		return (int) ((y - blkH / 2) / blkH + 0.5f);
	}
}
