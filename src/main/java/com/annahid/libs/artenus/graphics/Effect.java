package com.annahid.libs.artenus.graphics;

import com.annahid.libs.artenus.entities.behavior.Renderable;

/**
 * Superclass of all visual effects that can be added to a sprite. Currently there is only one
 * effect available, but there will be more added in the future. Each entity can only have a
 * single effect assigned to it through the
 * {@link com.annahid.libs.artenus.entities.behavior.Renderable#setEffect(Effect)} method. Multiple
 * effects must be added as a chain, using the {@link com.annahid.libs.artenus.graphics.Effect#over}
 * method.
 *
 * @see com.annahid.libs.artenus.entities.Entity
 * @author Hessan Feghhi
 */
public abstract class Effect {
	/**
	 * Chains another effect with this effect. The renderer will process this effect after it is
	 * done processing the chained effect.
	 *
	 * @param effect The effect to be chained
	 * @return The chained effect, which is normally the same as the argument
	 */
	public Effect over(Effect effect) {
		baseEffect = effect;
		return effect;
	}

	/**
	 * Renders the renderable, applying the effect, and taking the alpha value the renderable
	 * prefers to be rendered with.
	 *
	 * @param renderable renderable to be drawn using this effect
	 * @param alpha      preferred alpha value
	 */
	public abstract void render(Renderable renderable, float alpha);

	protected Effect baseEffect;
}
