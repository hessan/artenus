package com.annahid.libs.artenus.data;

/**
 * This class represents a 2-dimensional point and is used throughout this framework for positions,
 * scaling factors, impulses, and other properties of objects.
 * 
 * @author Hessan Feghhi
 *
 */
public final class Point2D {
	/**
	 * The x component of the point represented by this {@code Point2D} instance.
	 */
	public float x;

	/**
	 * The y component of the point represented by this {@code Point2D} instance.
	 */
	public float y;

	/**
	 * Constructs a {@code Point2D} with the given components.
	 *
	 * @param px The x component of the point.
	 * @param py The y component of the point.
	 */
	public Point2D(float px, float py) {
		x = px;
		y = py;
	}

	/**
	 * Sets both x and y components of this object to zero.
	 */
	public void setZero() {
		x = y = 0;
	}

	/**
	 * Multiplies a scalar value by both x and y components of this object and
	 * returns the result. The values in the original {@code Point2D} object are
	 * not modified and the result is a new {@code Point2D} instance.
	 *
	 * @param scalar The scalar to multiply.
	 * @return The resulting {@code Point2D} object which represents the new
	 * values.
	 */
	public Point2D multiply(float scalar) {
		return new Point2D(x * scalar, y * scalar);
	}
}
