package com.annahid.libs.artenus.entities.physics;

/**
 * The interface for 2-dimensional physical shapes.
 *
 * @see com.annahid.libs.artenus.entities.physics.PhysicalBody
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public interface Shape {
	/**
	 * Type value representing a circular shape.
	 */
	public static final int SHAPE_CIRCLE = 0;

	/**
	 * Type value representing a rectangular shape.
	 */
	public static final int SHAPE_RECTANGLE = 1;

	/**
	 * Type value representing a polygonal shape.
	 */
	public static final int SHAPE_POLYGON = 2;

	/**
	 * Type value representing a chain shape.
	 */
	public static final int SHAPE_CHAIN = 3;

	/**
	 * Gets the type of this {@code Shape}. Possible shape types are {@code SHAPE_CIRCLE},
	 * {@code SHAPE_RECTANGLE}, {@code SHAPE_POLYGON} or {@code SHAPE_CHAIN}.
	 *
	 * @return The type of the shape
	 */
	public int getType();

	/**
	 * Creates an internal representation of this {@code Shape}. The exact type of the returned
	 * object depends on the physics simulation engine used internally.
	 *
	 * @return Box2D representation of this {@code Shape}
	 */
	public Object createInternal();
}
