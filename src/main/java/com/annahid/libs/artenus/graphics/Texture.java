package com.annahid.libs.artenus.graphics;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Picture;
import android.opengl.GLES10;
import android.opengl.GLUtils;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.ui.Stage;
import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Represents a texture. A texture is an image that can be used in
 * real-time graphics libraries. All images must be converted to instances of
 * this class before they can be used in this framework.
 *
 * @author Hessan Feghhi
 *
 */
public class Texture {
	/**
	 * Creates a new OpenGL texture identifier. This identifier is required to
	 * load the texture in OpenGL.
	 *
	 * @return The texture identifier
	 */
	private static int newTextureID() {
		int[] temp = new int[1];
		GLES10.glGenTextures(1, temp, 0);
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
	 * Prepares the OpenGL context for rendering this {@code Texture}. This method must be
	 * called before calling {@link #draw(float, float, float, float, float)} for
	 * correct rendering. However, it is recommended that you do not call any of these
	 * methods directly. The framework handles low-level rendering.
	 *
	 * @param textureBuffer The texture buffer
	 * @param wrap          OpenGL texture wrapping method
	 */
	public final void prepare(FloatBuffer textureBuffer, int wrap) {
		GLES10.glEnable(GL10.GL_TEXTURE_2D);
		GLES10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		GLES10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		GLES10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, wrap);
		GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, wrap);
	}

	/**
	 * Renders this {@code Texture} on the given OpenGL context in the given rectangular
	 * region. It is recommended that you do not call this method directly.
	 *
	 * @param x   The x coordinate of the region to draw the texture
	 * @param y   The y coordinate of the region to draw the texture
	 * @param w   The width of the region to draw the texture
	 * @param h   The height of the region to draw the texture
	 * @param rot The rotation angle of the rectangular region
	 */
	public final void draw(float x, float y, float w, float h, float rot) {
		GLES10.glPushMatrix();
		GLES10.glTranslatef(x, y, 0);
		GLES10.glRotatef(rot, 0, 0, 1);
		GLES10.glScalef(w, h, 0);
		GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		GLES10.glPopMatrix();
	}

	/**
	 * Indicates whether the texture is loaded. The texture can be displayed only when
	 * it is loaded. You normally don't need to check this directly and the framework
	 * takes all the required precautions not to allow you to use unloaded textures.
	 *
	 * @return    {@code true} if loaded, or {@code false} otherwise
	 */
	public final boolean isLoaded() {
		return textureId >= 0;
	}

	/**
	 * Gets the width of the {@code Texture}. This will be the processed width, so if
	 * you are using an SVG texture, it will indicate the power-of-two estimation of the
	 * width.
	 *
	 * @return The width of the texture
	 */
	public final int getWidth() {
		return width;
	}

	/**
	 * Gets the height of the {@code Texture}. This will be the processed height, so if
	 * you are using an SVG texture, it will indicate the power-of-two estimation of the
	 * height.
	 *
	 * @return The width of the texture
	 */
	public final int getHeight() {
		return height;
	}

	/**
	 * Unloads this {@code Texture}. It is highly recommended that you do not call this
	 * method directly, as it might cause problems. {@code TextureManager} takes the
	 * responsibility for loading and unloading textures whenever required.
	 *
	 * @see TextureManager
	 */
	public final void destroy() {
		GLES10.glDeleteTextures(1, new int[]{textureId}, 0);
		textureId = -1;
	}

	/**
	 * Gets the OpenGL texture identifier associated with this texture. Applications of
	 * this method are rare and it is recommended not to use this method.
	 *
	 * @return The OpenGL texture identifier
	 */
	public final int getInternalTextureId() {
		return textureId;
	}

	/**
	 * Constructs a new {@code Texture} with the given image.
	 *
	 * @param resourceId The resource identifier of the image
	 */
	Texture(int resourceId) {
		resId = resourceId;
		textureId = -1;
	}

	boolean loadEGL() {
		if (bmp == null || bmp.isRecycled())
			return false;

		textureId = newTextureID();

		try {
			GLES10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
			GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			GLES10.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
		} catch (Exception ex) {
			textureId = -1;
		}

		if (textureId >= 0)
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bmp, 0);

		bmp.recycle();
		bmp = null;

		if (GLES10.glGetError() != GLES10.GL_NO_ERROR)
			destroy();

		return true;
	}

	void loadImage() {
		loading = true;

		final Resources res = Artenus.getInstance().getResources();
		final boolean isSVG = res.getResourceTypeName(resId).equalsIgnoreCase("raw");
		final float texScale = Stage.getTextureScalingFactor();

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

			float scaledWidth = pic.getWidth() * texScale, scaledHeight = pic.getHeight() * texScale;
			final float frWidth = (float) Math.pow(2, Math.ceil(Math.log(scaledWidth) / Math.log(2))) / scaledWidth;
			final float frHeight = (float) Math.pow(2, Math.ceil(Math.log(scaledHeight) / Math.log(2))) / scaledHeight;

			scaledWidth *= frWidth;
			scaledHeight *= frHeight;
			width = Math.round(scaledWidth / texScale);
			height = Math.round(scaledHeight / texScale);

			final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
			final Bitmap tempBmp = Bitmap.createBitmap(Math.round(scaledWidth), Math.round(scaledHeight), conf);
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

	/**
	 * The texture identifier associated with this {@code Texture}.
	 */
	protected int textureId;

	/**
	 * The width of this {@code Texture} in pixels.
	 */
	protected int width;

	/**
	 * The height of this {@code Texture} in pixels.
	 */
	protected int height;

	/**
	 * The resource identifier of the image to be loaded into this {@code Texture}.
	 */
	int resId;

	/*
     * Boolean indicating whether the image is being read.
     */
	boolean loading;

	/**
	 * The temporary bitmap used to store the image before it is loaded into the EGL texture.
	 */
	private Bitmap bmp = null;
}
