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
import android.opengl.GLES20;
import android.util.Pair;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Represents a font. A font is a special-purpose cut-out of an image that divides that image into
 * characters of different widths. Each font can represent a limited set of characters out of the
 * character space. Artenus fonts are normal SVG graphics with a comment block describing extra
 * information. Below is an example of the comment block:
 * <p/>
 * <pre>
 * &lt;!--
 * ARTENUS_FONT 80,-5,0
 * {@literal}A 0,  67
 * {@literal}B 67, 124
 * {@literal}C 124,198
 * {@literal}D 0,  80
 * --&gt;
 * </pre>
 * The above block introduces a font height of 80 pixels, horizontal spacing of -5, and vertical
 * spacing of 0. There are four letters defined in the font:
 * <p/>
 * <ul>
 * <li>A starts from x=0 to x=67 in the first line of the graphics (y=0 to y=79).</li>
 * <li>B starts from x=0 to x=67 in the first line of the graphics (y=0 to y=79).</li>
 * <li>C starts from x=0 to x=67 in the first line of the graphics (y=0 to y=79).</li>
 * <li>D starts from x=0 to x=67 in the second line of the graphics (y=80 to y=159). Whenever
 * the first x value is less than the previous letter's <em>second</em> x, the interpreter
 * jumps to the next line. Thus you don't need to specify y values at all.</li>
 * </ul>
 * <p/>
 * This comment block can be placed anywhere within the SVG file, but for best performance it is
 * recommended to appear as high as possible.
 *
 * @author Hessan Feghhi
 */
public final class Font extends Texture {
    private static final char accentedLetters[] =
            { 'à', 'è', 'ì', 'ò', 'ù', 'á', 'é', 'í', 'ó', 'ú', 'â', 'ê', 'î', 'ô', 'û' };

    private static final char basicLetters[] = { 'a', 'e', 'i', 'o', 'u' };

    /**
     * Contains x coordinate offset values in the order defined in the SVG font file.
     */
    private float[] offsets;

    /**
     * Holds the first character defined in the font file.
     */
    private char firstChar;

    /**
     * Holds horizontal spacing in pixels.
     */
    private int horSpacing = -10;

    /**
     * Holds vertical spacing in pixels.
     */
    private int verSpacing = 0;

    /**
     * Contains texture buffers for characters.
     */
    private FloatBuffer[] textureBuffers;

    /**
     * Holds character height.
     */
    private float charH;

    /**
     * Creates a font with the information provided.
     *
     * @param resourceId Resource identifier of the SVG file containing font information
     */
    Font(int resourceId) {
        super(resourceId);
        final Resources res = Artenus.getInstance().getResources();

        if (!res.getResourceTypeName(resId).equalsIgnoreCase("raw"))
            throw new IllegalStateException("Not a valid font resource");

        // Load commented font information from the SVG file.

        final Map<Character, Pair<Float, Float>> map = new HashMap<>(32);
        String line;
        boolean isFont = false;
        char first = Character.MAX_VALUE, last = Character.MIN_VALUE;
        final BufferedReader reader =
                new BufferedReader(new InputStreamReader(res.openRawResource(resId)));
        try {
            final Pattern pattern = Pattern.compile("\\s*,\\s*");

            while ((line = reader.readLine()) != null) {
                int index = line.indexOf("ARTENUS_FONT");

                if (index >= 0) {
                    String[] params = pattern.split(line.substring(index + 12).trim());

                    isFont = true;
                    charH = Integer.parseInt(params[0]);

                    if (params.length > 1) {
                        int hs = Integer.parseInt(params[1]), vs = 0;

                        if (params.length > 2)
                            vs = Integer.parseInt(params[2]);

                        horSpacing = hs;
                        verSpacing = vs;
                    }

                    while ((line = reader.readLine()) != null) {
                        line = line.trim();

                        if (line.startsWith("@")) {
                            String[] coords = pattern.split(line.substring(2).trim());

                            if (coords.length > 1) {
                                char c = line.charAt(1);

                                if (c > last)
                                    last = c;

                                if (c < first)
                                    first = c;

                                map.put(c, new Pair<>(
                                        Float.parseFloat(coords[0]), Float.parseFloat(coords[1])
                                ));
                            }
                        }
                    }

                    break;
                }
            }
        } catch (IOException ex) {
            isFont = false;
        }

        try {
            reader.close();
        } catch (IOException ex) {
            // Do nothing
        }

        if (!isFont)
            throw new IllegalStateException("Error reading font resource");

        offsets = new float[(last - first + 1) << 1];

        for (char c = first; c <= last; c++) {
            Pair<Float, Float> result = map.get(c);

            if (result != null) {
                int index = (c - first) << 1;
                offsets[index] = result.first;
                offsets[index + 1] = result.second;
            }
        }

        firstChar = first;
    }

    /**
     * Processes a text and conforms it to easily support accent-based characters in the framework
     * without the need to include accented letters in the actual font. You can use this method to
     * get the framework representation of latin strings with accented letters.
     *
     * @param text The text to be processed
     *
     * @return The processed text
     *
     * @deprecated Fonts can now be localized, which defeats the purpose of this method. It is still
     * recommended to include definitions for `, ', and ^ characters in any font file to avoid
     * compatibility issues. But this method will be removed from the framework soon.
     */
    @Deprecated
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
     * Calculates the width of a text if drawn with this {@code Font}, based of the font size
     * specified.
     *
     * @param text The string representation of the text
     * @param h    The font height
     *
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
     *
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
    public void draw(
            RenderingContext context,
            char[] ca, float sx, float sy, float h, float rot, boolean rtl) {
        if (textureBuffers == null)
            buildTextureBuffers();

        final TextureShaderProgram program =
                (TextureShaderProgram) TextureManager.getShaderProgram();
        context.setShader(program);

        float y = 0;
        float currentX = 0;

        GLES20.glEnable(GLES20.GL_TEXTURE_2D);
        program.feed(textureId);

        final float sz = h / charH;
        final float hSpacing = horSpacing * sz, vSpacing = verSpacing * sz;
        boolean firstLetter = true;

        context.pushMatrix();
        context.translate(sx, sy);
        context.rotate(rot);

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

            float w = (offsets[c * 2 + 1] - offsets[c * 2]) * sz;

            if (firstLetter) {
                firstLetter = false;
            }

            context.pushMatrix();
            context.translate(currentX + w / (rtl ? -2 : 2), y);
            context.scale(w, h);
            program.feedTexCoords(textureBuffers[c]);
            context.rect();
            context.popMatrix();

            if (ca.length > i + 1)
                if (ca[i + 1] == '\r') {
                    i++;
                    continue;
                }

            currentX += rtl ? (-w - hSpacing) : (w + hSpacing);
        }

        context.popMatrix();
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
}
