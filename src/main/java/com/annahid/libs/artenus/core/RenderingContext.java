package com.annahid.libs.artenus.core;

import com.annahid.libs.artenus.entities.behavior.Renderable;

/**
 * <p>Interface for a rendering context. It provides methods to control the state of the graphics
 * engine, and draw sprites. Methods in this interface are meant for a 2-dimensional environment.
 * </p>
 * <p>This interface is used internally by entities and textures to render graphics. Game developers
 * usually do not need to make use of this interface unless in rare cases. An example usa-case is
 * developing a new type of sprite which is not provided by the framework. In such a scenario it is
 * necessary to use this class instead of calling OpenGL ES functions directly. This ensures that
 * your sprite implementation is compatible with the rest of the framework.</p>
 */
public interface RenderingContext {
    /**
     * Pushes a matrix on top of the transformation matrix stack. Subsequent transformation calls
     * will only affect the top of the stack, leaving the rest of the matrices in the stack intact.
     */
    void pushMatrix();

    /**
     * <p>Pops the matrix at the top of the transformation matrix stack. The effective matrix will
     * be the one underneath. Calling this method when there is only one matrix in the stack has no
     * effect, and does not throw any exceptions.</p>
     * <p>You need to be careful to pop as many matrices as you push. The framework and all
     * renderables share the same rendering context. An imbalanced stack usage may cause
     * unexpected rendering results.</p>
     */
    void popMatrix();

    /**
     * Rotates the current matrix with the given angle.
     *
     * @param angle Rotation angle in degrees
     */
    void rotate(float angle);

    /**
     * Scales the current matrix with given factors.
     *
     * @param x Scaling factor along the x axis
     * @param y Scaling factor along the y axis
     */
    void scale(float x, float y);

    /**
     * Translates the current matrix the given distance.
     *
     * @param x The distance along the x axis
     * @param y The distance along the y axis
     */
    void translate(float x, float y);

    /**
     * Sets the current matrix to identity, canceling any previous transformation. Calling this
     * method is useful only in rare cases. It is normally enough to use the matrix stack to revert
     * transformations.
     */
    void identity();

    /**
     * Sets the color filter for the given context. The behavior of this method depends on the
     * shader program. But all shader programs in Artenus are required to accept color filters in
     * some way.
     *
     * @param r The red component of the color filter
     * @param g The green component of the color filter
     * @param b The blue component of the color filter
     * @param a The alpha component of the color filter, which determines the transparency
     */
    void setColorFilter(float r, float g, float b, float a);

    /**
     * Draws the default rectangular primitive with the given state of the rendering context. It is
     * up to the shader program to decide where and how this rectangle is drawn, given the state of
     * the rendering context.
     */
    void rect();

    /**
     * <p></p>Sets the shader program for the rendering context. All graphical method calls after
     * calling this method will work with the shader specified. Using this method correctly is
     * crucial to compatibility with the framework. The following are the rules that apply to all
     * renderables using this method:</p>
     * <ul>
     * <li>All renderables need to set the program shader before drawing anything.</li>
     * <li>Some aspects of the rendering context are reset after setting a shader, so it is
     * recommended to be the first call in {@link Renderable#render(RenderingContext, int)}.
     * </li>
     * <li>If the render method is called with {@link Renderable#FLAG_IGNORE_EFFECTS} filter,
     * no shader program should be applied. The system uses this flag to render objects in
     * plain graphics, for effects or touch maps. Setting a shader when this flag is on causes
     * instability in the system.
     * </li>
     * <li>As all renderables are required to set their own shader programs in the beginning of
     * the rendering process, there is no need to reset the program when rendering is
     * finished.
     * </li>
     * </ul>
     *
     * @param shader The shader program, or {@code null} to use the default program
     */
    void setShader(ShaderProgram shader);

    /**
     * Gets the shader program currently assigned to the rendering context.
     *
     * @return The active shader program
     */
    ShaderProgram getShader();

    /**
     * Gets the logical width of the context. Artenus unifies provides uniform graphics on various
     * screen sizes. The logical screen dimensions are device-independent.
     *
     * @return The width in texels
     */
    float getWidth();

    /**
     * Gets the logical height of the context. Artenus unifies provides uniform graphics on various
     * screen sizes. The logical screen dimensions are device-independent.
     *
     * @return The height in texels
     */
    float getHeight();

    /**
     * Gets the real and unscaled width of the rendering area, which is normally the screen width in
     * a full-screen application.
     *
     * @return The width of the rendering area
     */
    int getScreenWidth();

    /**
     * Gets the real and unscaled height of the rendering area, which is normally the screen height
     * in a full-screen application.
     *
     * @return The height of the rendering area
     */
    int getScreenHeight();

    /**
     * Gets the current transformation matrix that is used for drawing. This is a low-level method
     * and is usually not needed for a game.
     *
     * @return The transformation matrix
     */
    float[] getMatrix();

    /**
     * Pushes a pre-computed matrix on top of the transformation matrix stack. The argument can be
     * the return value of {@link RenderingContext#getMatrix()}.
     *
     * @param mat The new transformation matrix
     */
    void pushMatrix(float[] mat);
}
