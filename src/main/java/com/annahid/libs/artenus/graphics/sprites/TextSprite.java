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

import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.graphics.Font;

/**
 * Represents a text block. A text is a series of characters displayed on the screen using a
 * {@link Font}. This class provides all controls relating to size, color and text manipulation to
 * manage the piece of text that is added to the scene.
 *
 * @author Hessan Feghhi
 * @see SpriteEntity
 * @see Font
 */
@SuppressWarnings("UnusedDeclaration")
public final class TextSprite extends SpriteEntity {
    /**
     * Holds the current font used to draw the text.
     */
    private Font myFont;

    /**
     * Contains characters to be drawn.
     */
    private char[] ca;

    /**
     * Indicates whether the text should be rendered in right-to-left order.
     */
    private boolean rtl = false;

    /**
     * Creates a {@code TextSprite} using the given font and font size.
     *
     * @param font     The font for this text
     * @param fontSize The font size to use
     */
    public TextSprite(Font font, int fontSize) {
        this(font, fontSize, "");
    }

    /**
     * Creates a {@code TextSprite} using the given font and font size. The text
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

    @Override
    public final void render(RenderingContext ctx, int flags) {
        TextureShaderProgram program = (TextureShaderProgram) TextureManager.getShaderProgram();
        if ((flags & FLAG_IGNORE_EFFECTS) != 0) {
            if (ctx.getShader() instanceof TextureShaderProgram) {
                program = (TextureShaderProgram) ctx.getShader();
            }
        }
        ctx.setShader(program);

        if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0)
            effect.render(ctx, this, alpha);
        else if (alpha != 0) {

            if ((flags & FLAG_IGNORE_COLOR_FILTER) == 0)
                ctx.setColorFilter(alpha * cf.r, alpha * cf.g, alpha * cf.b, alpha);

            myFont.draw(ctx, program, ca, pos.x, pos.y, scale.x, rotation, rtl);
        }
    }
}
