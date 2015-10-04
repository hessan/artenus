package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.core.Scene;

/**
 * An entity that applies all its methods on an underlying entity. This class is the superclass
 * of all classes that provide a modification on the behavior of other entities.
 *
 * @author Hessan Feghhi
 */
public abstract class FilteredEntity
        implements Entity, Animatable, Transformable, Renderable {

    @Override
    public final void setAnimation(AnimationHandler animation) {
        if (target instanceof Animatable)
            ((Animatable) target).setAnimation(animation);
    }

    @Override
    public final AnimationHandler getAnimation() {
        if (target instanceof Animatable)
            return ((Animatable) target).getAnimation();

        return null;
    }

    @Override
    public Point2D getPosition() {
        if (target instanceof Transformable)
            return ((Transformable) target).getPosition();

        return null;
    }

    @Override
    public final void setPosition(Point2D position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        if (target instanceof Transformable)
            ((Transformable) target).setPosition(x, y);
    }

    @Override
    public final void move(float amountX, float amountY) {
        final Point2D pos = getPosition();
        setPosition(pos.x + amountX, pos.y + amountY);
    }

    @Override
    public float getRotation() {
        if (target instanceof Transformable)
            return ((Transformable) target).getRotation();

        return 0;
    }

    @Override
    public void setRotation(float angle) {
        if (target instanceof Transformable)
            ((Transformable) target).setRotation(angle);
    }

    @Override
    public final void rotate(float angle) {
        setRotation(getRotation() + angle);
    }

    @Override
    public Point2D getScale() {
        if (target instanceof Transformable)
            return ((Transformable) target).getScale();

        return null;
    }

    @Override
    public final void setScale(float scaleValue) {
        setScale(scaleValue, scaleValue);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        if (target instanceof Transformable)
            ((Transformable) target).setScale(scaleX, scaleY);
    }

    @Override
    public void setColorFilter(float r, float g, float b) {
        if (target instanceof Renderable)
            ((Renderable) target).setColorFilter(r, g, b);
    }

    @Override
    public final void setColorFilter(RGB rgb) {
        setColorFilter(rgb.r, rgb.g, rgb.b);
    }

    @Override
    public RGB getColorFilter() {
        if (target instanceof Renderable)
            return ((Renderable) target).getColorFilter();

        return null;
    }

    @Override
    public Effect getEffect() {
        if (target instanceof Renderable)
            return ((Renderable) target).getEffect();

        return null;
    }

    @Override
    public void setEffect(Effect effect) {
        if (target instanceof Renderable)
            ((Renderable) target).setEffect(effect);
    }

    @Override
    public void setAlpha(float alpha) {
        if (target instanceof Renderable)
            ((Renderable) target).setAlpha(alpha);
    }

    @Override
    public float getAlpha() {
        if (target instanceof Renderable)
            return ((Renderable) target).getAlpha();
        return 0;
    }

    @Override
    public void advance(float elapsedTime) {
        if (target instanceof Animatable)
            ((Animatable) target).advance(elapsedTime);
    }

    /**
     * Calls {@code render} on the underlying entity, with specified flags.
     *
     * @param flags Rendering flags
     * @see Renderable
     */
    @Override
    public void render(RenderingContext ctx, int flags) {
        if (target instanceof Renderable)
            ((Renderable) target).render(ctx, flags);
    }

    @Override
    public void onAttach(Scene scene) {
        target.onAttach(scene);
    }

    @Override
    public void onDetach(Scene scene) {
        target.onDetach(scene);
    }

    public final Entity getUnderlyingEntity() {
        return target;
    }

    protected FilteredEntity(Entity e) {
        target = e == null ? NullEntity.getInstance() : e;
    }

    protected Entity target;
}
