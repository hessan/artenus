package com.annahid.libs.artenus.graphics;

/**
 * Interface for classes that support rendering of visual content
 */
public interface Renderable {
	/**
	 * Rendering flag: ignore color filtering, if present.
	 * @see com.annahid.libs.artenus.entities.Entity#setColorFilter
	 */
	public static final int FLAG_IGNORE_COLOR_FILTER = 1;

	/**
	 * Rendering flag: ignore effects, if present.
	 * @see com.annahid.libs.artenus.graphics.Effect
	 */
	public static final int FLAG_IGNORE_EFFECTS = 2;

	/**
	 * Renders the visual content.
	 *
	 * @param flags Rendering flags
	 */
	public void render(int flags);
}
