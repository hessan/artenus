/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.graphics.sprites;

import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.data.Point2D;

/**
 * Represents a line, and provides methods to control the appearance of the line.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class LineSprite extends SpriteEntity {
    /**
     * Holds the first point.
     */
    private Point2D startPoint;

    /**
     * Holds the second point.
     */
    private Point2D endPoint;

    /**
     * Holds line thickness in pixels.
     */
    private float thickness;

    /**
     * Creates a {@code LineSprite} connecting two given points with the
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
    public final void render(RenderingContext context, int flags) {
        if ((flags & FLAG_IGNORE_EFFECTS) == 0)
            context.setShader(null);
        else context.setShader(context.getShader());

        if (effect != null && (flags & FLAG_IGNORE_EFFECTS) == 0) {
            effect.render(context, this, alpha);
            return;
        }

        context.pushMatrix();
        context.translate(pos.x, pos.y);
        context.rotate(rotation);
        context.scale(scale.x, scale.y);

        if ((flags & FLAG_IGNORE_COLOR_FILTER) == 0) {
            context.setColorFilter(cf.r * alpha, cf.g * alpha, cf.b * alpha, alpha);
        }
        context.rect();
        context.popMatrix();
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
