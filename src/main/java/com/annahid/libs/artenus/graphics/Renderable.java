package com.annahid.libs.artenus.graphics;

/**
 * An interface for classes that support rendering of visual content
 */
public interface Renderable {
	/**
	 * A flag that causes the renderer to ignore color filtering, if present.
	 */
	public static final int FLAG_IGNORE_COLOR_FILTER = 1;

	/**
	 * A flag that causes the renderer to ignore effects, if present.
	 */
	public static final int FLAG_IGNORE_EFFECTS = 2;

	/**
	 * Renders the visual content.
	 *
	 * @param flags Rendering flags
	 */
	public void render(int flags);
}
