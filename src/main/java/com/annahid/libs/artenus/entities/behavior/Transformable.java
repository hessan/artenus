package com.annahid.libs.artenus.entities.behavior;

import com.annahid.libs.artenus.data.Point2D;

/**
 * Interface for all entities that can be moved, rotated, and scaled. Sprites are good examples of
 * entities that have this behavior.
 *
 * @see com.annahid.libs.artenus.graphics.sprites.SpriteEntity
 * @author Hessan Feghhi
 */
public interface Transformable {
    /**
     * Gets the current position of this entity.
     *
     * @return Current 2-dimensional position
     */
    Point2D getPosition();

    /**
     * Sets the position of this entity.
     *
     * @param position The new position
     */
    void setPosition(Point2D position);

    /**
     * Sets the position of this sprite.
     *
     * @param x The x coordinate of the new position
     * @param y The y coordinate of the new position
     */
    void setPosition(float x, float y);

    /**
     * Moves this entity the given distance. The translation will be relative to this entity's
     * current position.
     *
     * @param amountX The horizontal translation
     * @param amountY The vertical translation
     */
    void move(float amountX, float amountY);

    /**
     * Gets the current rotational angle of this entity.
     *
     * @return Rotational angle in degrees
     */
    float getRotation();

    /**
     * Sets the rotational angle of this entity.
     *
     * @param angle Rotational angle in degrees
     */
    void setRotation(float angle);

    /**
     * Rotates this entity the given number of degrees. This rotation will be relative to this
     * entity's current rotational angle.
     *
     * @param angle The angle in degrees to rotate
     */
    void rotate(float angle);

    /**
     * Gets the 2-dimensional scaling factor for this entity.
     *
     * @return The scaling factor over horizontal and vertical axes
     */
    Point2D getScale();

    /**
     * Sets the scaling factor for this entity. Horizontal and vertical scaling factors will be set
     * to the same value.
     *
     * @param scaleValue Scaling factor
     */
    void setScale(float scaleValue);

    /**
     * Sets the scaling factor for this entity, specifying different values horizontally and
     * vertically.
     *
     * @param scaleX Horizontal scaling factor
     * @param scaleY Vertical scaling factor
     */
    void setScale(float scaleX, float scaleY);
}
