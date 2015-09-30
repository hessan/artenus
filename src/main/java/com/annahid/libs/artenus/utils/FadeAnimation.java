package com.annahid.libs.artenus.utils;

import com.annahid.libs.artenus.entities.AnimationHandler;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.graphics.sprites.SpriteEntity;

/**
 * Performs a fading effect animation for sprites. You can specify whether
 * you want the sprite to appear or disappear.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class FadeAnimation implements AnimationHandler {
    /**
     * Constructs a {@code FadeAnimation} with the specified behavior.
     *
     * @param fadeIn Whether the handled sprite should appear (or fade in)
     * @param speed  The speed of fading in. Setting this value to 1 causes a fully hidden object
     *               to appear completely in one second. Higher values make this transition faster
     *               and lower values make it slower.
     */
    public FadeAnimation(boolean fadeIn, float speed) {
        fin = fadeIn;
        s = speed;
    }

    @Override
    public void advance(Animatable entity, float elapsedTime) {

        if (!(entity instanceof SpriteEntity))
            return;

        final SpriteEntity sprite = (SpriteEntity) entity;
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

    private boolean fin;
    private float s;
}
