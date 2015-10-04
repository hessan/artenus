package com.annahid.libs.artenus.entities.behavior;

import com.annahid.libs.artenus.entities.AnimationHandler;

/**
 * Interface for all entities that can be animated. Sprites all implement this behavior.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.graphics.sprites.SpriteEntity
 */
public interface Animatable {
    /**
     * Gets the animation handler currently affecting this animatable.
     *
     * @return Animation handler
     */
    AnimationHandler getAnimation();

    /**
     * Assigns an animation handler to handle animations for this animatable.
     *
     * @param animation Animation handler, or {@code null} to remove the animation handler
     */
    void setAnimation(AnimationHandler animation);

    /**
     * Advances the animation for this animatable. This method is called once per animation frame.
     *
     * @param elapsedTime the amount of time since last call to this method
     */
    void advance(float elapsedTime);
}
