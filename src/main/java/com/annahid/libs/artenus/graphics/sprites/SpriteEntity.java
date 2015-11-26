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

package com.annahid.libs.artenus.graphics.sprites;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.graphics.animation.AnimationHandler;
import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.entities.behavior.Behaviors;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.graphics.Effect;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.core.Scene;

/**
 * Superclass for all sprites. A sprite is a mobile piece of graphic that can represent an object in
 * the game. Sprites are the graphical entities that are used in scenes. They can be moved, rotated,
 * scaled and animated according to their role in the game.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.core.Scene
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class SpriteEntity implements Entity, Animatable, Renderable, Transformable {
    /**
     * Holds color filter currently applied to this sprite.
     */
    protected final RGB cf = new RGB(1, 1, 1);

    /**
     * Holds current position of this sprite.
     */
    protected final Point2D pos;

    /**
     * Holds scaling factor in both dimensions.
     */
    protected final Point2D scale;

    /**
     * Holds the rotational angle.
     */
    protected float rotation;

    /**
     * Holds the animation currently applied to this sprite.
     */
    protected AnimationHandler anim;

    /**
     * Holds the alpha transparency value for this sprite.
     */
    protected float alpha;

    /**
     * Holds the effect currently applied to this sprite.
     */
    Effect effect;

    /**
     * Called by subclasses to initialize the sprite.
     */
    protected SpriteEntity() {
        pos = new Point2D(0.0f, 0.0f);
        scale = new Point2D(1.0f, 1.0f);
        rotation = 0.0f;
        alpha = 1.0f;
    }

    /**
     * Gets the current animation handler assigned to this sprite.
     *
     * @return The animation handler, or {@code null} if it there is no animation assigned to
     * this sprite
     */
    @Override
    public final AnimationHandler getAnimation() {
        return anim;
    }

    /**
     * Sets the animation handler for this sprite. Once set, the animation will be played on this
     * sprite automatically, until set to {@code null}.
     *
     * @param animation The animation handler, or {@code null} to remove animation
     */
    public final void setAnimation(AnimationHandler animation) {
        anim = animation;
    }

    @Override
    public final Point2D getPosition() {
        return pos;
    }

    /**
     * Sets the position of this sprite. This method is guaranteed not to have any performance
     * benefit over {@link SpriteEntity#setPosition(float, float)}.
     *
     * @param position The new position
     */
    @Override
    public final void setPosition(Point2D position) {
        if (position != null)
            setPosition(position.x, position.y);
    }

    @Override
    public final float getRotation() {
        return rotation;
    }

    @Override
    public final void setRotation(float angle) {
        rotation = angle;
    }

    /**
     * Gets the 2-dimensional scaling factor for this sprite.
     *
     * @return The scaling factor over horizontal and vertical axes
     */
    @Override
    public final Point2D getScale() {
        return scale;
    }

    /**
     * Sets the scaling factor for this sprite. Horizontal and vertical scaling factors will be set
     * to a single value.
     *
     * @param scaleValue The scaling factor
     */
    @Override
    public final void setScale(float scaleValue) {
        scale.x = scale.y = scaleValue;
    }

    /**
     * Sets the scaling factor for this sprite, specifying different values horizontally and
     * vertically.
     *
     * @param scaleX Horizontal scaling factor
     * @param scaleY Vertical scaling factor
     */
    @Override
    public final void setScale(float scaleX, float scaleY) {
        scale.x = scaleX;
        scale.y = scaleY;
    }

    /**
     * Rotates this sprite the given number of degrees. This rotation will be relative to this
     * sprite's current rotation angle.
     *
     * @param angle The angle in degrees to rotate
     */
    @Override
    public final void rotate(float angle) {
        rotation += (angle % 360);
    }

    /**
     * Moves this sprite the given distance. The translation will be relative to this sprite's
     * current position.
     *
     * @param amountX The horizontal translation
     * @param amountY The vertical translation
     */
    @Override
    public final void move(float amountX, float amountY) {
        pos.x += amountX;
        pos.y += amountY;
    }

    /**
     * Sets the position of this sprite.
     *
     * @param x The x coordinate of the new position
     * @param y The y coordinate of the new position
     */
    @Override
    public void setPosition(float x, float y) {
        pos.x = x;
        pos.y = y;
    }

    /**
     * Gets the transparency value for this sprite.
     *
     * @return The alpha value for transparency
     *
     * @see com.annahid.libs.artenus.graphics.sprites.SpriteEntity#setAlpha(float)
     */
    @Override
    public final float getAlpha() {
        return alpha;
    }

    /**
     * Sets the transparency value for this sprite. An alpha value of 1 indicates a fully opaque
     * sprite and a value of 0 is an invisible sprite. Any value in between can be specified to
     * achieve transparency.
     *
     * @param alphaValue The alpha value for transparency
     */
    @Override
    public final void setAlpha(float alphaValue) {
        alpha = alphaValue;
    }

    /**
     * Sets the color filter for this sprite. The original colors will be multiplied by this color
     * when rendering.
     *
     * @param r The red multiplier
     * @param g The green multiplier
     * @param b The blue multiplier
     */
    @Override
    public final void setColorFilter(float r, float g, float b) {
        cf.r = r;
        cf.g = g;
        cf.b = b;
    }

    /**
     * Gets the color filter for this sprite. Modifying the fields in the returned value affects
     * the color filter.
     *
     * @return The color filter
     */
    @Override
    public final RGB getColorFilter() {
        return cf;
    }

    /**
     * Sets the color filter for this sprite. The original colors will be multiplied by this color
     * when rendering.
     *
     * @param rgb The color multipliers
     */
    @Override
    public final void setColorFilter(RGB rgb) {
        cf.r = rgb.r;
        cf.g = rgb.g;
        cf.b = rgb.b;
    }

    @Override
    public void onAttach(Scene scene) {
    }

    @Override
    public void onDetach(Scene scene) {
    }

    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return behavior == Behaviors.RENDERABLE
                || behavior == Behaviors.ANIMATABLE
                || behavior == Behaviors.TRANSFORMABLE;
    }

    @Override
    public Effect getEffect() {
        return effect;
    }

    @Override
    public void setEffect(Effect effect) {
        this.effect = effect;
    }

    /**
     * Advances the animation for this sprite. This method is called by
     * {@link com.annahid.libs.artenus.entities.EntityCollection} when the scene is being advanced.
     *
     * @param elapsedTime The elapsed time since the previous frame
     */
    @Override
    public void advance(float elapsedTime) {
        if (anim == null) {
            return;
        }
        anim.advance(this, elapsedTime);
    }
}
