/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
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
import com.annahid.libs.artenus.graphics.animation.AnimationHandler;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.input.TouchEvent;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.core.Scene;

/**
 * Represents a pair of connected entities. This class is preferred to {@link EntityCollection}
 * whenever you want to encapsulate only two entities.
 *
 * @author Hessan Feghhi
 */
public class EntityPair implements Entity, Animatable, Touchable, Transformable, Renderable {
    /**
     * Holds the animation handler affecting the entities in the pair.
     */
    protected AnimationHandler anim;

    /**
     * Holds the first entity of the pair.
     */
    private Entity first;

    /**
     * Holds the second entity of the pair.
     */
    private Entity second;

    /**
     * Holds the position of the pair as a whole.
     */
    private final Point2D pos;

    /**
     * Holds the scale of the pair as a whole.
     */
    private final Point2D scale;

    /**
     * Holds the rotational angle for the pair as a whole.
     */
    private float rotation;

    /**
     * Constructs a new entity pair, with given entities.
     *
     * @param first  The first entity in the pair
     * @param second The second entity in the pair
     */
    public EntityPair(Entity first, Entity second) {
        pos = new Point2D(0, 0);
        scale = new Point2D(1, 1);
        this.first = first == null ? NullEntity.getInstance() : first;
        this.second = second == null ? NullEntity.getInstance() : second;
    }

    /**
     * Gets the first entity in this pair.
     *
     * @return The first entity
     */
    public final Entity getFirst() {
        return first;
    }

    /**
     * Gets the second entity in this pair.
     *
     * @return The second entity
     */
    public final Entity getSecond() {
        return second;
    }

    @Override
    public AnimationHandler getAnimation() {
        return anim;
    }

    @Override
    public void setAnimation(AnimationHandler animation) {
        anim = animation;
    }

    @Override
    public Point2D getPosition() {
        return pos;
    }

    @Override
    public void setPosition(Point2D position) {
        setPosition(position.x, position.y);
    }

    @Override
    public float getRotation() {
        return rotation;
    }

    @Override
    public void setRotation(float angle) {
        rotation = angle;
    }

    @Override
    public Point2D getScale() {
        return scale;
    }

    @Override
    public void setScale(float scaleValue) {
        scale.x = scale.y = scaleValue;
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
        scale.x = scaleX;
        scale.y = scaleY;
    }

    @Override
    public void rotate(float angle) {
        rotation += angle;
    }

    @Override
    public void move(float amountX, float amountY) {
        pos.x += amountX;
        pos.y += amountY;
    }

    @Override
    public void setPosition(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    @Override
    public void setColorFilter(float r, float g, float b) {
        if (first.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) first).setColorFilter(r, g, b);
        if (second.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) second).setColorFilter(r, g, b);
    }

    @Override
    public RGB getColorFilter() {
        if (first.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) first).getColorFilter();
        else if (second.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) second).getColorFilter();
        else return new RGB(0, 0, 0);
    }

    @Override
    public void setColorFilter(RGB rgb) {
        setColorFilter(rgb.r, rgb.g, rgb.b);
    }

    @Override
    public void onAttach(Scene scene) {
        first.onAttach(scene);
        second.onAttach(scene);
    }

    @Override
    public void onDetach(Scene scene) {
        first.onDetach(scene);
        second.onDetach(scene);
    }

    /**
     * Determines whether this entity pair has the specified behavior.
     *
     * @param behavior Behavior to be checked
     *
     * @return {@code true} if this entity pair has the behavior, {@code false} otherwise
     */
    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return behavior != Behaviors.RENDERABLE
                || first.hasBehavior(behavior)
                || second.hasBehavior(behavior);
    }

    /**
     * Renders both entities in the pair.
     *
     * @param flags Rendering flags
     */
    @Override
    public void render(RenderingContext ctx, int flags) {
        ctx.pushMatrix();
        ctx.translate(pos.x, pos.y);
        ctx.rotate(rotation);
        ctx.scale(scale.x, scale.y);

        if (first.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) first).render(ctx, flags);
        if (second.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) second).render(ctx, flags);

        ctx.popMatrix();
    }

    @Override
    public void advance(float elapsedTime) {
        if (anim != null)
            anim.advance(this, elapsedTime);

        if (first.hasBehavior(Behaviors.ANIMATABLE))
            ((Animatable) first).advance(elapsedTime);
        if (second.hasBehavior(Behaviors.ANIMATABLE))
            ((Animatable) second).advance(elapsedTime);
    }

    @Override
    public boolean handleTouch(TouchEvent event) {
        return (
                first.hasBehavior(Behaviors.TOUCHABLE) && ((Touchable) first).handleTouch(event)
        ) || (
                second.hasBehavior(Behaviors.TOUCHABLE) && ((Touchable) second).handleTouch(event)
        );
    }

    @Override
    public float getAlpha() {
        if (first.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) first).getAlpha();
        if (second.hasBehavior(Behaviors.RENDERABLE))
            return ((Renderable) second).getAlpha();
        return 0;
    }

    @Override
    public void setAlpha(float alpha) {
        if (first.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) first).setAlpha(alpha);
        if (second.hasBehavior(Behaviors.RENDERABLE))
            ((Renderable) second).setAlpha(alpha);
    }
}
