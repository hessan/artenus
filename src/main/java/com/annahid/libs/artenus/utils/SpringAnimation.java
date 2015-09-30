package com.annahid.libs.artenus.utils;

import com.annahid.libs.artenus.entities.AnimationHandler;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Transformable;

/**
 * Performs a spring-like animation on an entity. This animation is NOT a complete physical
 * simulation of a spring, and is just a loose visual effect that can be used for various screen
 * elements such as menus and buttons.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class SpringAnimation implements AnimationHandler {
    /**
     * A value that specifies the animation should affect the x component of the entity's position
     */
    public static final int X = 1;

    /**
     * A value that specifies the spring animation should affect the y component of the entity's
     * position
     */
    public static final int Y = 2;

    /**
     * A value that specifies the spring animation should affect the entity's scale
     */
    public static final int SCALE = 4;

    /**
     * A value that specifies the spring animation should affect the entity's rotational angle
     */
    public static final int ROTATION = 8;

    private int t;
    private float decay, k, s;
    private float currentValue;
    private float targetValue;
    private float maxDiff;

    /**
     * Constructs a {@code SpringAnimation} with the give type and values. The animation
     * will involve the selected component bounce back and forth centered on the target
     * value in a spring motion. For more control over the spring motion, use
     * {@link #SpringAnimation(int, float, float, float, float)}.
     *
     * @param type    The type of the animation which indicates the property of the entity
     *                the spring motion will affect
     * @param initial The initial value of the selected property for the animation
     * @param target  The target value of the selected property for the animation
     * @see #X
     * @see #Y
     * @see #SCALE
     * @see #ROTATION
     * @see #SpringAnimation(int, float, float, float, float)
     */
    public SpringAnimation(int type, float initial, float target) {
        this(type, initial, target, 16f, 0);
    }

    /**
     * Constructs a {@code SpringAnimation} with the give type and values. The animation
     * will involve the selected component oscillate centered on the target
     * value in a spring motion. You can also specify the K value of the spring and a decay
     * factor to make the motion slow down gradually.
     *
     * @param type        The type of the animation which indicates the property of the entity
     *                    the spring motion will affect
     * @param initial     The initial value of the selected property for the animation
     * @param target      The target value of the selected property for the animation
     * @param kValue      The K value of the spring, which determines how quickly it will bounce
     *                    back from a stretched position
     * @param decayFactor The decay factor of the spring motion. If this value is set to any
     *                    number above zero, the motion will decayed until it reaches a halt. The
     *                    speed of decay is determined by the value of this parameter.
     */
    public SpringAnimation(int type, float initial, float target, float kValue, float decayFactor) {
        t = type;
        currentValue = initial;
        targetValue = target;
        decay = decayFactor;
        k = kValue;
        maxDiff = Math.abs(initial - target);
        s = 0;
    }

    /**
     * Gets the current value of the property used in the animation.
     *
     * @return The value of the selected property. Selected property is determined by animation type.
     * @see #getType()
     */
    public float getCurrentValue() {
        return currentValue;
    }

    /**
     * Gets the target value of the property used in the animation. The target value can be viewed
     * as the center of oscillation.
     *
     * @return The target value of the selected property. Selected property is determined by
     * animation type
     * @see #getType()
     */
    public float getTargetValue() {
        return targetValue;
    }

    /**
     * Gets the target value of the property used in the animation. The target value can be viewed
     * as the center of oscillation.
     *
     * @param value The target value of the selected property. Selected property is determined by
     *              animation type.
     * @see #getType()
     */
    public final void setTargetValue(float value) {
        targetValue = value;
    }

    /**
     * Gets the spring animation type of this {@code SpringAnimation} object.
     *
     * @return The type of spring, which indicates the property of the entity that is affected
     * by the animation
     * @see #X
     * @see #Y
     * @see #SCALE
     * @see #ROTATION
     */
    public final int getType() {
        return t;
    }

    @Override
    public final void advance(Animatable animatable, float elapsedTime) {
        final float dx = (targetValue - currentValue) * k - s * decay;
        s += dx * elapsedTime;
        currentValue += s * elapsedTime;

        if (Math.abs(currentValue - targetValue) > maxDiff) {
            if (currentValue > targetValue)
                currentValue = targetValue + maxDiff;
            else currentValue = targetValue - maxDiff;
        }

        if (animatable instanceof Transformable) {
            final Transformable entity = (Transformable) animatable;

            if (t == X)
                entity.getPosition().x = currentValue;
            else if (t == Y)
                entity.getPosition().y = currentValue;
            else if (t == SCALE)
                entity.setScale(currentValue, currentValue);
            else entity.setRotation(currentValue);
        }
    }
}