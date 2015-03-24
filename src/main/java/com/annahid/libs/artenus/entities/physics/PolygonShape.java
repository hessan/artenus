package com.annahid.libs.artenus.entities.physics;

import com.annahid.libs.artenus.data.Point2D;

import org.jbox2d.common.Vec2;

/**
 * Represents a {@link com.annahid.libs.artenus.entities.physics.Shape} consisting of a convex
 * polygon.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class PolygonShape implements Shape {
	/**
	 * Type value representing a polygonal shape.
	 *
	 * @see Shape#getType()
	 */
	public static final int TYPE = 2;

	/**
	 * Constructs a {@code PolygonShape} using the convex hull of the given points.
	 *
	 * @param points The array of points making the desired polygon
	 */
	public PolygonShape(Point2D[] points) {
		pts = points;
	}

	/**
	 * Gets the type of this {@link com.annahid.libs.artenus.entities.physics.Shape}.
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
		final Vec2[] points = new Vec2[pts.length];

		for (int i = 0; i < pts.length; i++)
			points[i] = new Vec2(
					pts[i].x / PhysicsSimulator.pixelsPerMeter,
					pts[i].y / PhysicsSimulator.pixelsPerMeter);

		shape.set(points, pts.length);
		return shape;
	}

	private Point2D[] pts;
}
