/*
 *  This file is part of Artenus.
 *
 *  Artenus is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.entities;

import com.annahid.libs.artenus.entities.behavior.Behaviors;
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

    protected Entity target;

    protected FilteredEntity(Entity e) {
        target = e == null ? NullEntity.getInstance() : e;
    }

    @Override
    public final AnimationHandler getAnimation() {
        if (target.hasBehavior(Behaviors.ANIMATABLE))
            return ((Animatable) target).getAnimation();

        return null;
    }

    @Override
    public final void setAnimation(AnimationHandler animation) {
        if (target.hasBehavior(Behaviors.ANIMATABLE))
            ((Animatable) target).setAnimation(animation);
    }

    @Override
    public Point2D getPosition() {
        if (target.hasBehavior(Behaviors.TRANSFORMABLE))
            return ((Transformable) target).getPosition();

        return null;
    }

    @Override
    public final void setPosition(Point2D position) {
        setPosition(position.x, position.y);
    }

    @Override
    public void setPosition(float x, float y) {
        if (target.hasBehavior(Behaviors.TRANSFORMABLE))
            ((Transformable) target).setPosition(x, y);
    }

    @Override
    public final void move(float amountX, float amountY) {
        final Point2D pos = getPosition();
        setPosition(pos.x + amountX, pos.y + amountY);
    }

    @Override
    public float getRotation() {
        if (target.hasBehavior(Behaviors.TRANSFORMABLE))
            return ((Transformable) target).getRotation();

        return 0;
    }

    @Override
    public void setRotation(float angle) {
        if (target.hasBehavior(Behaviors.TRANSFORMABLE))
            ((Transformable) target).setRotation(angle);
    }

    @Override
    public final void rotate(float angle) {
        setRotation(getRotation() + angle);
    }

    @Override
    public Point2D getScale() {
        if (target.hasBehavior(Behaviors.TRANSFORMABLE))
            return ((Transformable) target).getScale();

        return null;
    }

    @Override
    public final void setScale(float scaleValue) {
        setScale(scaleValue, scaleValue);
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        if (target.hasBehavior(Behaviors.TRANSFORMABLE))
            ((Transformable) target).setScale(scaleX, scaleY);
    }

    @Override
    public void setColorFilter(float r, float g, float b) {
        if (target.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) target).setColorFilter(r, g, b);
    }

    @Override
    public RGB getColorFilter() {
        if (target.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) target).getColorFilter();

        return null;
    }

    @Override
    public final void setColorFilter(RGB rgb) {
        setColorFilter(rgb.r, rgb.g, rgb.b);
    }

    @Override
    public Effect getEffect() {
        if (target.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) target).getEffect();

        return null;
    }

    @Override
    public void setEffect(Effect effect) {
        if (target.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) target).setEffect(effect);
    }

    @Override
    public float getAlpha() {
        if (target.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) target).getAlpha();
        return 0;
    }

    @Override
    public void setAlpha(float alpha) {
        if (target.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) target).setAlpha(alpha);
    }

    @Override
    public void advance(float elapsedTime) {
        if (target.hasBehavior(Behaviors.ANIMATABLE))
            ((Animatable) target).advance(elapsedTime);
    }

    /**
     * Calls {@code render} on the underlying entity, with specified flags.
     *
     * @param flags Rendering flags
     *
     * @see Renderable
     */
    @Override
    public void render(RenderingContext ctx, int flags) {
        if (target.hasBehavior(Behaviors.RENDERABLE))
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

    /**
     * Determines whether the underlying entity has the specified behavior.
     *
     * @param behavior Behavior to be checked
     *
     * @return {@code true} if the underlying entity has the behavior, {@code false} otherwise
     */
    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return behavior != Behaviors.TOUCHABLE && target.hasBehavior(behavior);
    }

    public final Entity getUnderlyingEntity() {
        return target;
    }
}
