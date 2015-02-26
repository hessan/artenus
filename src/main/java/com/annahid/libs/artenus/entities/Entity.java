package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.graphics.Renderable;
import com.annahid.libs.artenus.ui.Scene;

/**
 * Defines an entity that can be added to a scene. Entities provide various functionality, including
 * graphical, physical, or user interaction. A {@link Scene} is made up of a list of entities, and
 * it uses them for processing graphics and animation.
 *
 * @see com.annahid.libs.artenus.ui.Scene
 * @author Hessan Feghhi
 */
public interface Entity extends Renderable {
	/**
	 * Assigns an animation handler to handle animations for this entity.
	 *
	 * @param animation Animation handler, or {@code null} to remove the animation handler
	 */
	public void setAnimation(AnimationHandler animation);

	/**
	 * Gets the animation handler currently affecting this entity.
	 *
	 * @return Animation handler
	 */
	public AnimationHandler getAnimation();

	/**
	 * Gets the current position of this entity.
	 *
	 * @return Current 2-dimensional position
	 */
	public Point2D getPosition();

	/**
	 * Sets the position of this entity.
	 *
	 * @param position The new position
	 */
	public void setPosition(Point2D position);

	/**
	 * Sets the position of this sprite.
	 *
	 * @param x The x coordinate of the new position
	 * @param y The y coordinate of the new position
	 */
	public void setPosition(float x, float y);

	/**
	 * Moves this entity the given distance. The translation will be relative to this entity's
	 * current position.
	 *
	 * @param amountX The horizontal translation
	 * @param amountY The vertical translation
	 */
	public void move(float amountX, float amountY);

	/**
	 * Gets the current rotational angle of this entity.
	 *
	 * @return Rotational angle in degrees
	 */
	public float getRotation();

	/**
	 * Sets the rotational angle of this entity.
	 *
	 * @param angle Rotational angle in degrees
	 */
	public void setRotation(float angle);

	/**
	 * Rotates this entity the given number of degrees. This rotation will be relative to this
	 * entity's current rotational angle.
	 *
	 * @param angle The angle in degrees to rotate
	 */
	public void rotate(float angle);

	/**
	 * Gets the 2-dimensional scaling factor for this entity.
	 *
	 * @return The scaling factor over horizontal and vertical axes
	 */
	public Point2D getScale();

	/**
	 * Sets the scaling factor for this entity. Horizontal and vertical scaling factors will be set
	 * to the same value.
	 *
	 * @param scaleValue Scaling factor
	 */
	public void setScale(float scaleValue);

	/**
	 * Sets the scaling factor for this entity, specifying different values horizontally and
	 * vertically.
	 *
	 * @param scaleX Horizontal scaling factor
	 * @param scaleY Vertical scaling factor
	 */
	public void setScale(float scaleX, float scaleY);

	/**
	 * Sets the color filter for this entity. The original colors should be multiplied by this color
	 * when rendering.
	 *
	 * @param r The red multiplier
	 * @param g The green multiplier
	 * @param b The blue multiplier
	 */
	public void setColorFilter(float r, float g, float b);

	/**
	 * Sets the color filter for this entity. The original colors should be multiplied by this color
	 * when rendering.
	 *
	 * @param rgb The color multipliers
	 */
	public void setColorFilter(RGB rgb);

	/**
	 * Gets the color filter for this entity. Modifying the fields in the returned value affects
	 * the color filter.
	 *
	 * @return The color filter
	 */
	public RGB getColorFilter();

	/**
	 * Called when this entity is associated with a scene.
	 *
	 * @param scene The scene that currently contains the entity.
	 */
	public void onAttach(Scene scene);

	/**
	 * Called when this entity is dissociated with a scene.
	 *
	 * @param scene The scene from which the entity is removed.
	 */
	public void onDetach(Scene scene);

	/**
	 * Gets the effect currently assigned to this entity. Multiple effects can be chained together
	 * to create a single effect.
	 *
	 * @return The effect chain currently assigned to this entity
	 * @see com.annahid.libs.artenus.graphics.Effect
	 */
	public Effect getEffect();

	/**
	 * Assigns an effect to this entity. Each entity can only have one effect assigned to it.
	 * Multiple effects can be chained together to create a single effect.
	 *
	 * @see com.annahid.libs.artenus.graphics.Effect
	 */
	public void setEffect(Effect effect);

	/**
	 * Advances the animation for this entity. This method is called once per animation frame.
	 *
	 * @param elapsedTime the amount of time since last call to this method
	 */
	public void advance(float elapsedTime);
}
