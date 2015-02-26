package com.annahid.libs.artenus.input;

/**
 * Interface for classes that support touch input.
 */
public interface Touchable {
	/**
	 * This method is called whenever a touch event arrives.
	 *
	 * @param action Touch event action. See {@link com.annahid.libs.artenus.input.InputManager}
	 *               for possible values.
	 * @param x      The x position of the event on the stage
	 * @param y      The y position of the event on the stage
	 * @return    {@code true} if handled, {@code false} otherwise
	 */
	public boolean handleTouch(int action, float x, float y);
}
