package com.annahid.libs.artenus.input;

import com.annahid.libs.artenus.data.Point2D;

/**
 * An imaginary region used by some input managers to receive touch input.
 */
public final class TapRegion {
	/**
	 * The Shape specifies the shape of the tap region.
	 */
	public enum Shape {
		/**
		 * A rectangular tap region
		 */
		RECTANGLE,

		/**
		 * An oval tap region
		 */
		OVAL
	}

	/**
	 * Constructs a tap region with the given parameters.
	 *
	 * @param type   The region type identifier, which can be one of
	 *                {@link Shape#RECTANGLE} and {@link Shape#OVAL}
	 * @param width  The width of the tap region
	 * @param height The height of the tap region
	 */
	public TapRegion(TapRegion.Shape type, float width, float height) {
		scale = new Point2D(1, 1);
		w = width;
		h = height;
		t = type;
		rot = 0;
	}

	public void setRotation(float rotation) {
		rot = rotation;
	}

	public float getRotation() {
		return rot;
	}

	/**
	 * Gets the width of this {@code TapRegion}.
	 *
	 * @return The width of this region
	 */
	public float getWidth() {
		return w;
	}

	/**
	 * Gets the height of this {@code TapRegion}.
	 *
	 * @return The height of this region
	 */
	public float getHeight() {
		return h;
	}

	/**
	 * Checks whether the motion information provided falls into this {@code TapRegion}.
	 *
	 * @param posx The x component of the object location
	 * @param posy The y component of the object location
	 * @param x The x component of the motion location
	 * @param y The y component of the motion location
	 * @return    {@code true} if the location falls into the region or {@code false} otherwise
	 */
	boolean hitTest(float posx, float posy, float x, float y) {
		final float hw = w / 2 * scale.x, hh = h / 2 * scale.y;

		if (rot != 0) {
			final float c = (float) Math.cos(Math.toRadians(-rot));
			final float s = (float) Math.sin(Math.toRadians(-rot));

			// UN-rotate the point depending on the rotation of the rectangle
			x = posx + c * (x - posx) - s * (y - posy);
			y = posy + s * (x - posx) + c * (y - posy);
		}

		if (t == Shape.OVAL)
			return Math.pow(x - posx, 2) / (hw * hw) + Math.pow(y - posy, 2) / (hh * hh) <= 1;
		else return x > posx - hw && x < posx + hw && y > posy - hh && y < posy + hh;
	}

	private Point2D scale;
	private float rot, w, h;
	private Shape t;
}
