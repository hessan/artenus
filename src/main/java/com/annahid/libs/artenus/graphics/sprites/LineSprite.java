package com.annahid.libs.artenus.graphics.sprites;

import android.opengl.GLES10;

import com.annahid.libs.artenus.data.Point2D;

import javax.microedition.khronos.opengles.GL10;

/**
 * A subclass of {@link SpriteEntity} that represents a line. It provides methods to control the
 * appearance of the line.
 *
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class LineSprite extends SpriteEntity {
	private Point2D startPoint, endPoint;
	private float thickness;

	/**
	 * Constructs a {@code LineSprite} connecting two given points with the
	 * line width that is specified.
	 *
	 * @param startPoint The first point of the line
	 * @param endPoint   The second point of the line
	 * @param width      The line width
	 */
	public LineSprite(Point2D startPoint, Point2D endPoint, float width) {
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.thickness = width;
		updateLine();
	}

	/**
	 * Gets the first or starting point of the line.
	 *
	 * @return The starting point
	 */
	public final Point2D getStartPoint() {
		return new Point2D(startPoint.x, startPoint.y);
	}

	/**
	 * Gets the second or end point of the line.
	 *
	 * @return The end point
	 */
	public final Point2D getEndPoint() {
		return new Point2D(endPoint.x, endPoint.y);
	}

	/**
	 * Moves the first or starting point of the line to the given coordinates.
	 * This triggers internal transform updates.
	 *
	 * @param x The x coordinate of the new point
	 * @param y The y coordinate of the new point
	 */
	public final void setStartPoint(float x, float y) {
		startPoint.x = x;
		startPoint.y = y;
		updateLine();
	}

	/**
	 * Moves the second or end point of the line to the given coordinates.
	 * This triggers internal transform updates.
	 *
	 * @param x The x coordinate of the new point
	 * @param y The y coordinate of the new point
	 */
	public final void setEndPoint(float x, float y) {
		endPoint.x = x;
		endPoint.y = y;
		updateLine();
	}

	/**
	 * Moves both points of the line to the given coordinates.
	 * This triggers internal transform updates.
	 *
	 * @param s The new starting point
	 * @param e The new end point
	 */
	public final void setPoints(Point2D s, Point2D e) {
		startPoint.x = s.x;
		startPoint.y = s.y;
		endPoint.x = e.x;
		endPoint.y = e.y;
		updateLine();
	}

	/**
	 * Modifies line width. This triggers internal transform updates.
	 *
	 * @param w The new line width
	 */
	public final void setLineWidth(float w) {
		thickness = w;
		updateLine();
	}

	@Override
	public final void render(int flags) {
		if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0) {
			effect.render(this, alpha);
			return;
		}

		GLES10.glDisable(GL10.GL_TEXTURE_2D);
		GLES10.glPushMatrix();
		GLES10.glTranslatef(pos.x, pos.y, 0);
		GLES10.glRotatef(rotation, 0, 0, 1);
		GLES10.glScalef(scale.x, scale.y, 0);

		if ((flags & FLAG_IGNORE_COLOR_FILTER) == 0)
			GLES10.glColor4f(cf.r * alpha, cf.g * alpha, cf.b * alpha, alpha);

		GLES10.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
		GLES10.glPopMatrix();
	}

	/**
	 * Updates the {@code LineSprite} after changes are made to the properties.
	 */
	private void updateLine() {
		final float dx = endPoint.x - startPoint.x;
		final float dy = endPoint.y - startPoint.y;

		pos.x = (startPoint.x + endPoint.x) / 2;
		pos.y = (startPoint.y + endPoint.y) / 2;
		setRotation((float) Math.toDegrees(Math.atan2(dy, dx)));
		setScale((float) Math.sqrt(dx * dx + dy * dy), thickness);
	}
}
