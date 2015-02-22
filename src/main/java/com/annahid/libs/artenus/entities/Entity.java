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
	public void setAnimation(AnimationHandler animation);

	public AnimationHandler getAnimation();

	public Point2D getPosition();

	public void setPosition(Point2D position);

	public void setPosition(float x, float y);

	public void move(float amountX, float amountY);

	public float getRotation();

	public void setRotation(float angle);

	public void rotate(float angle);

	public Point2D getScale();

	public void setScale(float scaleValue);

	public void setScale(float scaleX, float scaleY);

	public void setColorFilter(float r, float g, float b);

	public void setColorFilter(RGB rgb);

	public RGB getColorFilter();

	/**
	 * Called when this entity is associated with a scene.
	 *
	 * @param scene	The scene that currently contains the entity.
	 */
	public void onAttach(Scene scene);

	/**
	 * Called when this entity is dissociated with a scene.
	 *
	 * @param scene	The scene from which the entity is removed.
	 */
	public void onDetach(Scene scene);

	public Effect getEffect();

	public void setEffect(Effect effect);

	/**
	 * Advances the animation for this entity. This method is called once per animation frame.
	 *
	 * @param elapsedTime	the amount of time since last call to this method.
	 */
	public void advance(float elapsedTime);
}
