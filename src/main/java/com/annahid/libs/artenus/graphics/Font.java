package com.annahid.libs.artenus.graphics;

import android.opengl.GLES10;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * A {@code Texture} that represents a font. A font is a special-purpose cut-out of
 * an image that divides that image into characters of different widths. Each font can represent
 * a limited set of characters out of the character space. You should be careful with the text
 * you handle with each font, as it might not support all characters included in the text.
 *
 * @author Hessan Feghhi
 *
 */
public final class Font extends Texture {
	private static final char accentedLetters[] = { 'à', 'è', 'ì', 'ò', 'ù', 'á', 'é', 'í', 'ó', 'ú', 'â', 'ê', 'î', 'ô', 'û' };
	private static final char basicLetters[] = {'a', 'e', 'i', 'o', 'u'};

	/**
	 * Processes a text and conforms it to easily support accent-based characters in the framework
	 * without the need to include accented letters in the actual font. You can use this method to
	 * get the framework representation of latin strings with accented letters.
	 *
	 * @param text The text to be processed
	 * @return The processed text
	 */
	public static String processText(String text) {
		String ret = (text == null) ? "" : text;

		for (int i = 0; i < 5; i++) {
			ret = ret.replace(String.valueOf(accentedLetters[i]), "`\r" + basicLetters[i]);
			ret = ret.replace(String.valueOf(accentedLetters[i + 5]), "'\r" + basicLetters[i]);
			ret = ret.replace(String.valueOf(accentedLetters[i + 10]), "^\r" + basicLetters[i]);
		}

		return ret;
	}

	/**
	 * Sets the horizontal and vertical letter spacing for this font. Zero letter spacing will
	 * usually give an odd look to the font. A negative horizontal spacing is recommended.
	 *
	 * @param hs Horizontal letter spacing
	 * @param vs Vertical letter spacing
	 */
	public final void setLetterSpacing(int hs, int vs) {
		horSpacing = hs;
		verSpacing = vs;
	}

	/**
	 * Calculates the width of a text if drawn with this {@code Font}, based of the font size
	 * specified.
	 *
	 * @param text The string representation of the text
	 * @param h    The font height
	 * @return The width of the text
	 */
	public final float getTextWidth(String text, float h) {
		return getTextWidth(text.toCharArray(), h);
	}

	/**
	 * Calculates the width of a text if drawn with this {@code Font}, based of the font size
	 * specified. This method is faster that {@link #getTextWidth(String, float)}.
	 *
	 * @param ca The character array representation of the text
	 * @param h  The font height
	 * @return The width of the text
	 */
	public final float getTextWidth(char[] ca, float h) {
		final float sz = h / charH;
		final float hSpacing = horSpacing * sz;
		float maxWidth = 0, currentX = 0, w;

		boolean firstLetter = true;

		for (int i = 0; i < ca.length; i++) {
			char c = ca[i];

			if (c == '\n') {
				maxWidth = Math.max(maxWidth, currentX);
				currentX = 0;
				firstLetter = true;
				continue;
			} else if (c == ' ') {
				currentX += h / 3;
				continue;
			}

			c -= firstChar;

			if (c >= 0) {
				w = (offsets[c * 2 + 1] - offsets[c * 2]) * sz;

				if (firstLetter) {
					firstLetter = false;
				}

				if (ca.length > i + 1)
					if (ca[i + 1] == '\r') {
						w = 0;
						i++;
					}

				currentX += w + hSpacing;
			}
		}

		return Math.max(maxWidth, currentX);
	}

	/**
	 * Draws a text on the OpenGL context using the information provided. This method is internally
	 * called by {@code TextSprite} to draw the text.
	 *
	 * @param ca  The character array representation of the text
	 * @param sx  Starting x coordinate
	 * @param sy  Starting y coordinate
	 * @param h   The desired height of the text. This controls font size
	 * @param rot Rotation angle of the text
	 * @param rtl {@code true} if the text should be drawn in right-to-left direction,
	 *            and {@code false} otherwise
	 */
	public void draw(char[] ca, float sx, float sy, float h, float rot, boolean rtl) {
		if (textureBuffers == null)
			buildTextureBuffers();

		float y = 0;
		float currentX = 0;

		GLES10.glEnable(GL10.GL_TEXTURE_2D);
		GLES10.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		GLES10.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

		final float sz = h / charH;
		final float hSpacing = horSpacing * sz, vSpacing = verSpacing * sz;
		boolean firstLetter = true;

		GLES10.glPushMatrix();
		GLES10.glTranslatef(sx, sy, 0);
		GLES10.glRotatef(rot, 0, 0, 1);

		for (int i = 0; i < ca.length; i++) {
			char c = ca[i];

			if (c == '\n') {
				currentX = 0;
				y += (h + vSpacing);
				firstLetter = true;
				continue;
			} else if (c == ' ') {
				currentX += h / (rtl ? -3 : 3);
				continue;
			}

			c -= firstChar;

			if (c >= 0) {
				float w = (offsets[c * 2 + 1] - offsets[c * 2]) * sz;

				if (firstLetter) {
					firstLetter = false;
				}

				GLES10.glPushMatrix();
				GLES10.glTranslatef(currentX + w / (rtl ? -2 : 2), y, 0);
				GLES10.glScalef(w, h, 0);
				GLES10.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffers[c]);
				GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
				GLES10.glPopMatrix();

				if (ca.length > i + 1)
					if (ca[i + 1] == '\r') {
						i++;
						continue;
					}

				currentX += rtl ? (-w - hSpacing) : (w + hSpacing);
			}
		}

		GLES10.glPopMatrix();
	}

	/**
	 * Constructs a font with the information provided.
	 *
	 * @param resourceId      The resource identifier of the image containing font graphics
	 * @param characterHeight Character height
	 * @param startChar       Starting character included in the font
	 * @param charOffsets     Width offset pairs identifying each character. See
	 *                        {@link com.annahid.libs.artenus.graphics.TextureManager.FontInfo} for
	 *                        more information about these offsets.
	 * @see TextureManager.FontInfo
	 */
	Font(int resourceId, int characterHeight, char startChar, int[] charOffsets) {
		super(resourceId);
		firstChar = startChar;
		charH = characterHeight;
		offsets = new float[charOffsets.length];

		for (int i = 0; i < charOffsets.length; i++)
			offsets[i] = charOffsets[i];
	}

	/**
	 * Builds the required OpenGL texture buffers for the characters.
	 */
	final void buildTextureBuffers() {
		float y1 = 0, y2 = charH;
		final float sw = width, sh = height;

		textureBuffers = new FloatBuffer[offsets.length / 2];

		for (int index = 0; index < textureBuffers.length; index++) {
			final float x1 = offsets[index * 2] / sw;
			final float x2 = (offsets[index * 2 + 1] - 1f) / sw;

			if (index > 0)
				if (offsets[index * 2] - (offsets[(index - 1) * 2]) < 0) {
					y1 += charH;
					y2 += charH;
				}

			final float texture[] = {
					x1, y1 / sh,
					x2, y1 / sh,
					x1, y2 / sh,
					x2, y2 / sh,
			};

			final ByteBuffer ibb = ByteBuffer.allocateDirect(texture.length * 4);
			ibb.order(ByteOrder.nativeOrder());
			textureBuffers[index] = ibb.asFloatBuffer();
			textureBuffers[index].put(texture);
			textureBuffers[index].position(0);
		}
	}

	private float[] offsets;
	private char firstChar;
	private int horSpacing = -10, verSpacing = 0;
	private FloatBuffer[] textureBuffers;
	private float charH;
}
