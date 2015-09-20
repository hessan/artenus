package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.entities.behavior.Animatable;

/**
 * Interface for classes that handle animations. In order to make animations effective, you should
 * set their animation to an animation handler instance using
 * {@link com.annahid.libs.artenus.entities.behavior.Animatable#setAnimation(AnimationHandler)}.
 *
 * @author Hessan Feghhi
 *
 */
public interface AnimationHandler {
	/**
	 * Called whenever the animation should update the animatable based on elapsed time.
	 *
	 * @param animatable   The {@link Animatable} object to be updated
	 * @param elapsedTime  Elapsed time in seconds since the last frame
	 * @see Entity
	 */
	void advance(Animatable animatable, float elapsedTime);
}
