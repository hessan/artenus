package com.annahid.libs.artenus.entities.physics;

/**
 * The interface for 2-dimensional physical shapes.
 *
 * @see com.annahid.libs.artenus.entities.physics.PhysicalBody
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public interface Shape {
	public static final int SHAPE_CIRCLE = 0;
	public static final int SHAPE_RECTANGLE = 1;
	public static final int SHAPE_POLYGON = 2;
	public static final int SHAPE_CHAIN = 3;

	/**
	 * Gets the type of this {@code Shape}. Possible shape types are {@code SHAPE_CIRCLE},
	 * {@code SHAPE_RECTANGLE}, {@code SHAPE_POLYGON} or {@code SHAPE_CHAIN}.
	 *
	 * @return The type of the shape.
	 */
	public int getType();

	/**
	 * Creates a Box2D representation of this {@code Shape}. This framework uses Box2D
	 * internally for physical simulation and this method maps this shape to its
	 * corresponding Box2D shape to be used in this physics engine.
	 *
	 * @return Box2D representation of this {@code Shape}.
	 */
	public org.jbox2d.collision.shapes.Shape getBox2DShape();
}
