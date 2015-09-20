package com.annahid.libs.artenus.physics;

/**
 * Represents a circular {@link com.annahid.libs.artenus.physics.Shape} that can
 * be used for physical simulation.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class CircleShape implements Shape {
	/**
	 * Type value representing a circular shape.
	 *
	 * @see Shape#getType()
	 */
	public static final int TYPE = 0;

	/**
	 * Constructs a {@code CircleShape} using the given radius.
	 *
	 * @param radius The radius of the circle
	 */
	public CircleShape(float radius) {
		r = radius;
	}

	/**
	 * Gets the radius associated to this {@code CircleShape}.
	 *
	 * @return The radius of the circle
	 */
	public float getRadius() {
		return r;
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
		org.jbox2d.collision.shapes.Shape shape = new org.jbox2d.collision.shapes.CircleShape();
		shape.m_radius = r / PhysicsSimulator.pixelsPerMeter;
		return shape;
	}

	private float r;
}
