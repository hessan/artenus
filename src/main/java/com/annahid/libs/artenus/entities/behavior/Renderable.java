package com.annahid.libs.artenus.entities.behavior;

import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.Effect;

/**
 * Interface for all entities that support rendering of visual content.
 */
public interface Renderable {
	/**
	 * Rendering flag: ignore color filtering, if present.
	 * @see Renderable#setColorFilter
	 */
	int FLAG_IGNORE_COLOR_FILTER = 1;

	/**
	 * Rendering flag: ignore effects, if present.
	 * @see com.annahid.libs.artenus.graphics.Effect
	 */
	int FLAG_IGNORE_EFFECTS = 2;    /**
	 * Sets the color filter for this entity. The original colors should be multiplied by this color
	 * when rendering.
	 *
	 * @param r The red multiplier
	 * @param g The green multiplier
	 * @param b The blue multiplier
	 */
	void setColorFilter(float r, float g, float b);

	/**
	 * Sets the color filter for this entity. The original colors should be multiplied by this color
	 * when rendering.
	 *
	 * @param rgb The color multipliers
	 */
	void setColorFilter(RGB rgb);

	/**
	 * Gets the color filter for this entity. Modifying the fields in the returned value affects
	 * the color filter.
	 *
	 * @return The color filter
	 */
	@SuppressWarnings("unused")
	RGB getColorFilter();

	/**
	 * Gets the effect currently assigned to this entity. Multiple effects can be chained together
	 * to create a single effect.
	 *
	 * @return The effect chain currently assigned to this entity
	 * @see com.annahid.libs.artenus.graphics.Effect
	 */
	Effect getEffect();

	/**
	 * Assigns an effect to this entity. Each entity can only have one effect assigned to it.
	 * Multiple effects can be chained together to create a single effect.
	 *
	 * @see com.annahid.libs.artenus.graphics.Effect
	 */
	void setEffect(Effect effect);

	/**
	 * Renders the visual content.
	 *
	 * @param flags Rendering flags
	 */
	void render(int flags);
}
