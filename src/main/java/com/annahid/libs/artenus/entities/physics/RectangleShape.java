package com.annahid.libs.artenus.entities.physics;

/**
 * This class represents a rectangular {@link com.annahid.libs.artenus.entities.physics.Shape} that
 * can be used for physical simulation.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class RectangleShape implements Shape {
	private float w, h;

	/**
	 * Constructs a {@code RectangleShape} with given dimensions.
	 *
	 * @param width  The width of the rectangle.
	 * @param height The height of the rectangle.
	 */
	public RectangleShape(float width, float height) {
		w = width;
		h = height;
	}

	@Override
	public org.jbox2d.collision.shapes.Shape getBox2DShape() {
		final org.jbox2d.collision.shapes.PolygonShape shape =
				new org.jbox2d.collision.shapes.PolygonShape();
		shape.setAsBox(w / PhysicsSimulator.pixelsPerMeter, h / PhysicsSimulator.pixelsPerMeter);
		return shape;
	}

	/**
	 * This method returns {@link com.annahid.libs.artenus.entities.physics.Shape#SHAPE_RECTANGLE}.
	 */
	@Override
	public int getType() {
		return SHAPE_RECTANGLE;
	}
}
