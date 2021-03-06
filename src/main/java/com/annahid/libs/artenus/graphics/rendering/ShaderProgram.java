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

import java.nio.FloatBuffer;

/**
 * Interface for all shader programs. A shader program is a rendering component that instructs the
 * graphics hardware on how to draw elements on the screen.
 *
 * @author Hessan Feghhi
 */
public interface ShaderProgram {
    /**
     * Compiles this shader program.
     */
    void compile();

    /**
     * Feeds a transformation matrix to this shader program. This matrix is also multiplied by the
     * projection matrix and can be directly used to draw elements.
     *
     * @param mat The matrix
     */
    void feed(float[] mat);

    /**
     * Feeds a vertex strip for the element to be drawn on the screen.
     *
     * @param buffer The vertex buffer for the element
     */
    void feed(FloatBuffer buffer);

    /**
     * Feeds a color component to this shader program. All shader programs in this framework are
     * expected to accept one color component. But how they use this component is not defined.
     *
     * @param r The red component of the color
     * @param g The green component of the color
     * @param b The blue component of the color
     * @param a The alpha component of the color
     */
    void feed(float r, float g, float b, float a);

    /**
     * Activates the shader program. This method is normally called for each renderable, or when
     * the shader program is switched to.
     *
     * @see com.annahid.libs.artenus.entities.behavior.Renderable
     */
    void activate();

    /**
     * Removes any unused state created by this shader program from the rendering context. This
     * method is normally called when the shader program is no longer needed for the current frame.
     * Note that the program may be needed for the next frame, so it should not deallocate its
     * global resources.
     */
    void cleanup();

    /**
     * Destroys the shader program and frees all allocated resources. The shader will not be used
     * after this method, unless the {@link #compile()} method is called again.
     */
    void destroy();
}
