package com.annahid.libs.artenus.physics;

/**
 * Represents a rectangular {@link com.annahid.libs.artenus.physics.Shape} that
 * can be used for physical simulation.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class RectangleShape implements Shape {
	/**
	 * Type value representing a rectangular shape.
	 *
	 * @see Shape#getType()
	 */
	public static final int TYPE = 1;

	/**
	 * Constructs a {@code RectangleShape} with given dimensions.
	 *
	 * @param width  The width of the rectangle
	 * @param height The height of the rectangle
	 */
	public RectangleShape(float width, float height) {
		w = width;
		h = height;
	}

	/**
	 * Gets the type of this {@link com.annahid.libs.artenus.physics.Shape}.
	 * @return {@link #TYPE}
	 */
	@Override
	public int getType() {
		return TYPE;
	}

	@Override
	public Object createInternal() {
		final org.jbox2d.collision.shapes.PolygonShape shape =
				new org.jbox2d.collision.shapes.PolygonShape();
		shape.setAsBox(w / PhysicsSimulator.pixelsPerMeter, h / PhysicsSimulator.pixelsPerMeter);
		return shape;
	}

	private float w, h;
}
