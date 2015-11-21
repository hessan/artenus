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

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.behavior.Behaviors;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.core.Scene;

/**
 * Replaces {@code null} entity values where such a value can be problematic.
 *
 * @author Hessan Feghhi
 */
final class NullEntity implements Entity, Transformable {
    /**
     * Holds the singleton instance of {@code NullEntity}.
     */
    private static final Entity instance = new NullEntity();

    /**
     * Holds a dummy point used for position and scale fields, so they are not returned as null.
     */
    private static final Point2D dummyPoint = new Point2D(1, 1);

    /**
     * Prevents explicit instantiation.
     */
    private NullEntity() {
    }

    /**
     * Gets the singleton instance.
     *
     * @return The instance
     */
    public static Entity getInstance() {
        return instance;
    }

    @Override
    public Point2D getPosition() {
        return null;
    }

    @Override
    public void setPosition(Point2D position) {
    }

    @Override
    public void setPosition(float x, float y) {
    }

    @Override
    public void move(float amountX, float amountY) {
    }

    @Override
    public float getRotation() {
        return 0;
    }

    @Override
    public void setRotation(float angle) {
    }

    @Override
    public void rotate(float angle) {
    }

    @Override
    public Point2D getScale() {
        return dummyPoint;
    }

    @Override
    public void setScale(float scaleValue) {
    }

    @Override
    public void setScale(float scaleX, float scaleY) {
    }

    @Override
    public void onAttach(Scene scene) {
    }

    @Override
    public void onDetach(Scene scene) {
    }

    @Override
    public boolean hasBehavior(Behaviors behavior) {
        return behavior == Behaviors.TRANSFORMABLE;
    }
}
