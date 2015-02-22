package com.annahid.libs.artenus.data;

/**
 * This class represents a color with red, green, and blue components. This class does not
 * provide transparency since it is not to be used for that purpose.
 *
 * @author Hessan Feghhi
 *
 */
public final class RGB {
	/**
	 * The red component of the color represented by this {@code RGB} instance.
	 */
	public float r;

	/**
	 * The red component of the color represented by this {@code RGB} instance.
	 */
	public float g;

	/**
	 * The red component of the color represented by this {@code RGB} instance.
	 */
	public float b;

	/**
	 * Constructs a new {@code RGB} using the give red, green and blue components.
	 *
	 * @param cr The red component of the color.
	 * @param cg The green component of the color.
	 * @param cb The blue component of the color.
	 */
	public RGB(float cr, float cg, float cb) {
		r = cr;
		g = cg;
		b = cb;
	}

	/**
	 * Constructs a new {@code RGB} using the color's string representation.
	 *
	 * @param rgb A hexadecimal string of the form rrggbb or RRGGBB.
	 */
	public RGB(String rgb) {
		r = Integer.parseInt(rgb.substring(0, 2), 16) / 256.0f;
		g = Integer.parseInt(rgb.substring(2, 4), 16) / 256.0f;
		b = Integer.parseInt(rgb.substring(4), 16) / 256.0f;
	}
}
