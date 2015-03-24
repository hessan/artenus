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
	@SuppressWarnings("unused")
	public TapRegion(TapRegion.Shape type, float width, float height) {
		this(type, width, height, 0, 0);
	}

	/**
	 * Constructs a tap region with the given parameters.
	 *
	 * @param type   The region type identifier, which can be one of
	 *                {@link Shape#RECTANGLE} and {@link Shape#OVAL}
	 * @param width  The width of the tap region
	 * @param height The height of the tap region
	 * @param cx     The x component of the position
	 * @param cy     The y component of the position
	 */
	public TapRegion(TapRegion.Shape type, float width, float height, float cx, float cy) {
		pos = new Point2D(cx, cy);
		scale = new Point2D(1, 1);
		w = width;
		h = height;
		t = type;
		rot = 0;
	}

	public void setPosition(float cx, float cy) {
		pos.x = cx;
		pos.y = cy;
	}

	public void setRotation(float rotation) {
		rot = rotation;
	}

	public Point2D getPosition() {
		return pos;
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
	 * @param eventX The x component of the motion location
	 * @param eventY The y component of the motion location
	 * @return    {@code true} if the location falls into the region or {@code false} otherwise
	 */
	public boolean hitTest(float eventX, float eventY) {
		final float hw = w / 2 * scale.x, hh = h / 2 * scale.y;

		float x = eventX, y = eventY;

		if (rot != 0) {
			final float c = (float) Math.cos(Math.toRadians(-rot));
			final float s = (float) Math.sin(Math.toRadians(-rot));

			// UN-rotate the point depending on the rotation of the rectangle
			x = pos.x + c * (x - pos.x) - s * (y - pos.y);
			y = pos.y + s * (x - pos.x) + c * (y - pos.y);
		}

		if (t == Shape.OVAL)
			return Math.pow(x - pos.x, 2) / (hw * hw) + Math.pow(y - pos.y, 2) / (hh * hh) <= 1;
		else return x > pos.x - hw && x < pos.x + hw && y > pos.y - hh && y < pos.y + hh;
	}

	private Point2D pos, scale;
	private float rot, w, h;
	private Shape t;
}
