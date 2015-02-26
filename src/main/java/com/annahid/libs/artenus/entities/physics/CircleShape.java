package com.annahid.libs.artenus.entities.physics;

/**
 * This class represents a circular {@link com.annahid.libs.artenus.entities.physics.Shape} that can
 * be used for physical simulation.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class CircleShape implements Shape {
	private float r;

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
	 * Gets the type of this {@link com.annahid.libs.artenus.entities.physics.Shape}.
	 * @return {@link com.annahid.libs.artenus.entities.physics.Shape#SHAPE_CIRCLE}
	 */
	@Override
	public int getType() {
		return SHAPE_CIRCLE;
	}

	@Override
	public Object createInternal() {
		org.jbox2d.collision.shapes.Shape shape = new org.jbox2d.collision.shapes.CircleShape();
		shape.m_radius = r / PhysicsSimulator.pixelsPerMeter;
		return shape;
	}
}
