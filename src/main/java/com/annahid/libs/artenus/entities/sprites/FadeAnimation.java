package com.annahid.libs.artenus.entities.sprites;

import com.annahid.libs.artenus.entities.AnimationHandler;
import com.annahid.libs.artenus.entities.Entity;

/**
 * This class performs a fading effect animation for sprites. You can specify whether
 * you want the sprite to appear or disappear.
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class FadeAnimation implements AnimationHandler {
	private boolean fin;
	private float s;

	/**
	 * Constructs a {@code FadeAnimation} with a given behavioral pattern.
	 *
	 * @param fadeIn Whether the handled sprite should appear (or fade in).
	 * @param speed  The speed of fading in. Setting this value to 1 causes a
	 *               fully hidden object to appear completely in one second. Higher values make this
	 *               transition faster and lower values make it slower.
	 */
	public FadeAnimation(boolean fadeIn, float speed) {
		fin = fadeIn;
		s = speed;
	}

	@Override
	public void advance(Entity entity, float elapsedTime) {

		if (!(entity instanceof SpriteEntity))
			return;

		SpriteEntity sprite = (SpriteEntity) entity;

		final float alpha = sprite.getAlpha();

		if (fin) {
			if (alpha < 1)
				sprite.setAlpha(Math.min(1, alpha + elapsedTime * s));
			else sprite.setAnimation(null);
		} else {
			if (alpha > 0)
				sprite.setAlpha(Math.max(0, alpha - elapsedTime * s));
			else sprite.setAnimation(null);
		}
	}
}
