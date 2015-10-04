package com.annahid.libs.artenus.physics;

/**
 * The interface for 2-dimensional physical shapes.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.physics.PhysicalBody
 */
public interface Shape {
    /**
     * Gets the type of this {@code Shape}. Each shape defines its own type value, which is stored
     * as the constant {@code TYPE} within the corresponding class.
     *
     * @return The type of the shape
     */
    int getType();

    /**
     * Creates an internal representation of this {@code Shape}. The exact type of the returned
     * object depends on the physics simulation engine used internally.
     *
     * @return Box2D representation of this {@code Shape}
     */
    Object createInternal();
}
