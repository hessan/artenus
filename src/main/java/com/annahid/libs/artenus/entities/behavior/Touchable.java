package com.annahid.libs.artenus.entities.behavior;

/**
 * Interface for all entities that can receive touch events.
 */
public interface Touchable {
	/**
	 * This method is called whenever a touch event arrives.
	 *
	 * @param action Touch event action. See {@link com.annahid.libs.artenus.input.InputManager}
	 *               for possible values.
	 * @param pointerId Pointer index in a multi-touch environment
	 * @param x      The x position of the event on the stage
	 * @param y      The y position of the event on the stage
	 * @return    {@code true} if handled, {@code false} otherwise
	 */
	boolean handleTouch(int action, int pointerId, float x, float y);
}
