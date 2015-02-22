package com.annahid.libs.artenus.entities.sprites;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.AnimationHandler;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.graphics.Renderable;
import com.annahid.libs.artenus.ui.Scene;

/**
 * This class is the base class for all sprites. A sprite is a mobile piece of graphic
 * that can represent an object in the game. Sprites are the graphical entities that
 * are used in scenes. They can be moved, rotated, scaled and animated according to
 * their role in the game.
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.ui.Scene
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class SpriteEntity implements Entity, Renderable {
	protected final RGB cf = new RGB(1, 1, 1);
	protected final Point2D pos, scale;
	protected float rotation;
	protected AnimationHandler anim;
	protected float alpha;

	Effect effect;

	/**
	 * Constructs a generic sprite. This method is called by subclasses to initialize
	 * a {@code Sprite} instance.
	 */
	protected SpriteEntity() {
		pos = new Point2D(0.0f, 0.0f);
		scale = new Point2D(1.0f, 1.0f);
		rotation = 0.0f;
		alpha = 1.0f;
	}

	/**
	 * Sets the animation handler for this {@code Sprite}. Once set, the animation
	 * will be played on this {@code Sprite} automatically, until set to {@code null}.
	 *
	 * @param animation The animation handler, or {@code null} to remove animation.
	 */
	public final void setAnimation(AnimationHandler animation) {
		anim = animation;
	}


	/**
	 * Gets the current animation handler assigned to this {@code Sprite}.
	 *
	 * @return The animation handler, or {@code null} if it there is no animation
	 * assigned to this {@code Sprite}.
	 */
	public final AnimationHandler getAnimation() {
		return anim;
	}

	public final Point2D getPosition() {
		return pos;
	}

	public final float getRotation() {
		return rotation;
	}

	/**
	 * Gets the 2-dimensional scaling factor for this {@code Sprite}.
	 *
	 * @return The scaling factor on horizontal and vertical axes.
	 */
	public final Point2D getScale() {
		return scale;
	}

	public final void setRotation(float angle) {
		rotation = angle;
	}

	/**
	 * Sets the scaling factor for this {@code Sprite}. Horizontal
	 * and vertical scaling factors will be set to a single value.
	 *
	 * @param scaleValue The scaling factor.
	 */
	public final void setScale(float scaleValue) {
		scale.x = scale.y = scaleValue;
	}

	/**
	 * Sets the scaling factor for this {@code Sprite}, specifying different
	 * values horizontally and vertically.
	 *
	 * @param scaleX Horizontal scaling factor.
	 * @param scaleY Vertical scaling factor.
	 */
	public final void setScale(float scaleX, float scaleY) {
		scale.x = scaleX;
		scale.y = scaleY;
	}

	/**
	 * Rotates this {@code Sprite} the given number of degrees. This rotation
	 * will be relative to this {@code Sprite}'s current rotation angle.
	 *
	 * @param angle The angle in degrees to rotate.
	 */
	public final void rotate(float angle) {
		rotation += (angle % 360);
	}

	/**
	 * Moves this {@code Sprite} the given distance. The translation will be
	 * relative to this {@code Sprite}'s current position.
	 *
	 * @param amountX The horizontal translation.
	 * @param amountY The vertical translation.
	 */
	public final void move(float amountX, float amountY) {
		pos.x += amountX;
		pos.y += amountY;
	}

	/**
	 * Sets the position of this {@code Sprite}.
	 *
	 * @param position The new position.
	 */
	public final void setPosition(Point2D position) {
		if (position != null)
			setPosition(position.x, position.y);
	}

	public void setPosition(float x, float y) {
		pos.x = x;
		pos.y = y;
	}

	/**
	 * Sets the transparency value for this {@code Sprite}.
	 *
	 * @param alphaValue The alpha value for transparency. A value of
	 *                   1 indicates a fully opaque {@code Sprite} and a value of 0 is an
	 *                   invisible {@code Sprite}. Any value in between can be specified to
	 *                   achieve transparencies.
	 */
	public final void setAlpha(float alphaValue) {
		alpha = alphaValue;
	}

	/**
	 * Gets the transparency value for this {@code Sprite}.
	 *
	 * @return The alpha value for transparency. A value of
	 * 1 indicates a fully opaque {@code Sprite} and a value of 0 is an
	 * invisible {@code Sprite}.
	 */
	public final float getAlpha() {
		return alpha;
	}

	/**
	 * Sets the color filter for this  {@code Sprite}. The original colors
	 * will be multiplied by this color when rendering.
	 *
	 * @param r The red multiplier.
	 * @param g The green multiplier.
	 * @param b The blue multiplier.
	 */
	public final void setColorFilter(float r, float g, float b) {
		cf.r = r;
		cf.g = g;
		cf.b = b;
	}

	/**
	 * Sets the color filter for this  {@code Sprite}. The original colors
	 * will be multiplied by this color when rendering.
	 *
	 * @param rgb The color multipliers.
	 */
	public final void setColorFilter(RGB rgb) {
		cf.r = rgb.r;
		cf.g = rgb.g;
		cf.b = rgb.b;
	}

	/**
	 * Gets the color filter for this {@code Sprite}. Modifying the fields
	 * in the returned value affects the color filter.
	 *
	 * @return The color filter.
	 */
	public final RGB getColorFilter() {
		return cf;
	}

	@Override
	public void onAttach(Scene scene) {
	}

	@Override
	public void onDetach(Scene scene) {
	}

	@Override
	public Effect getEffect() {
		return effect;
	}

	@Override
	public void setEffect(Effect effect) {
		this.effect = effect;
	}

	/**
	 * Advances the animation for this sprite. This method is called
	 * by {@link com.annahid.libs.artenus.entities.EntityCollection} when the scene is being advanced.
	 *
	 * @param elapsedTime The elapsed time since the previous frame.
	 */
	@Override
	public final void advance(float elapsedTime) {
		if (anim == null)
			return;

		anim.advance(this, elapsedTime);
	}
}
