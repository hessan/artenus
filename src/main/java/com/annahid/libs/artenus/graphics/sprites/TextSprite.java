package com.annahid.libs.artenus.graphics.sprites;

import android.opengl.GLES10;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.graphics.Font;

/**
 * A subclass of {@link SpriteEntity} which represents a text. A text is a series of characters
 * displayed on the screen using a {@code Font}. This class provides all controls relating to size,
 * color and text manipulation to manage the piece of text that is added to the scene.
 *
 * @author Hessan Feghhi
 * @see SpriteEntity
 * @see Font
 */
@SuppressWarnings("UnusedDeclaration")
public final class TextSprite extends SpriteEntity {
	private Font myFont;
	private char[] ca;
	private boolean rtl = false;

	/**
	 * Constructs a {@code TextSprite} using the given font and font size.
	 *
	 * @param font     The font for this text
	 * @param fontSize The font size to use
	 */
	public TextSprite(Font font, int fontSize) {
		this(font, fontSize, "");
	}

	/**
	 * Constructs a {@code TextSprite} using the given font and font size. The text
	 * initially displayed for the sprite is also specified.
	 *
	 * @param font        The font for this text
	 * @param fontSize    The font size to use
	 * @param initialText The initial text of the sprite
	 */
	public TextSprite(Font font, int fontSize, String initialText) {
		super();
		myFont = font;
		setText(initialText);
		setScale(fontSize, fontSize);
	}

	/**
	 * Sets the right-to-left state of this {@code TextSprite}. If a text sprite is right-to-left,
	 * its characters will flow from right to left.
	 *
	 * @param isRtl {@code true} will make this sprite right-to-left and {@code false} will make
	 *              it left-to-right
	 */
	public void setRTL(boolean isRtl) {
		rtl = isRtl;
	}

	/**
	 * Sets the position of the text sprite so that it is centered at the given point.
	 *
	 * @param p The center point
	 */
	public void centerAt(Point2D p) {
		centerAt(p.x, p.y);
	}

	/**
	 * Sets the position of the text sprite so that it is centered at the given point.
	 *
	 * @param x The x coordinate of the center point
	 * @param y The y coordinate of the center point
	 */
	public void centerAt(float x, float y) {
		final float w = myFont.getTextWidth(ca, scale.x) / (rtl ? -2 : 2);
		final double rot = Math.toRadians(rotation);
		setPosition(x - w * (float) Math.cos(rot), y - w * (float) Math.sin(rot));
	}

	/**
	 * Gets the current font assigned to this {@code TextSprite}.
	 *
	 * @return The assigned font
	 */
	public final Font getFont() {
		return myFont;
	}

	/**
	 * Assigns a new font to this {@code TextSprite}.
	 *
	 * @param font The new font to be assigned
	 */
	public final void setFont(Font font) {
		myFont = font;
	}

	/**
	 * Sets the text on this {@code TextSprite}. Changes will take effect immediately.
	 *
	 * @param value The string representation of the new text
	 */
	public final void setText(String value) {
		if (value != null)
			ca = Font.processText(value).toCharArray();
		else ca = new char[0];
	}

	/**
	 * Determines whether this {@code TextSprite} represents an empty text. This is a faster
	 * method than to retrieve the text and examine it manually.
	 *
	 * @return {@code true} if the text is an empty string, and {@code false} otherwise
	 */
	public final boolean isTextEmpty() {
		return ca.length == 0;
	}

	/**
	 * Gets the string representation of the text currently displayed for this {@code TextSprite}.
	 * Keep in mind that this method returns a newly created String and can have memory overhead.
	 *
	 * @return The string representation of the current text
	 */
	public final String getText() {
		return new String(ca);
	}

	@Override
	public final void render(int flags) {
		if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0)
			effect.render(this, alpha);
		else if (alpha != 0) {

			if ((flags & FLAG_IGNORE_COLOR_FILTER) == 0)
				GLES10.glColor4f(alpha * cf.r, alpha * cf.g, alpha * cf.b, alpha);

			myFont.draw(ca, pos.x, pos.y, scale.x, rotation, rtl);
		}
	}
}
