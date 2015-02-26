package com.annahid.libs.artenus.entities;

/**
 * An interface for classes that handle animations for {@link Entity} objects. In order to make
 * animations effective, you should set their animation to an animation handler instance using
 * {@link Entity#setAnimation(AnimationHandler)}.
 *
 * @author Hessan Feghhi
 *
 */
public interface AnimationHandler {
	/**
	 * Called whenever the animation should update the entity based on elapsed time.
	 *
	 * @param sprite      The {@link Entity} object to be updated
	 * @param elapsedTime Elapsed time in seconds since the last frame
	 * @see Entity
	 */
	public void advance(Entity sprite, float elapsedTime);
}
