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

package com.annahid.libs.artenus.core;

import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.filters.PostProcessingFilter;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;

/**
 * <p>This interface is one of the key components of this framework. The {@code Stage} is where
 * everything actually happens. Each game has a stage on which different scenes appear. Its instance
 * is passed to the application through {@link com.annahid.libs.artenus.Artenus#init}. The
 * application needs to assign a {@link com.annahid.libs.artenus.core.StageManager} to the stage in
 * order to handle its important event.</p>
 *
 * @author Hessan Feghhi
 * @see StageManager
 */
public interface Stage {
    /**
     * Sets the background color of the loading screen.
     *
     * @param bgColor Background color
     */
    void setLoadingBackColor(RGB bgColor);

    /**
     * Converts screen x to stage x coordination using the scaling factor.
     *
     * @param x Screen x
     * @return Stage x
     */
    float screenToLogicalX(float x);

    /**
     * Converts screen y to stage x coordination using the scaling factor.
     *
     * @param y Screen y
     * @return Stage y
     */
    float screenToLogicalY(float y);

    /**
     * Converts stage x to screen x coordination using the scaling factor.
     *
     * @param x Stage x
     * @return Screen x
     */
    float logicalToScreenX(float x);

    /**
     * Converts stage y to screen y coordination using the scaling factor.
     *
     * @param y Stage y
     * @return Screen y
     */
    float logicalToScreenY(float y);

    /**
     * Sets the next scene that should be shown on this {@code Stage}. The scene will not be shown
     * immediately and will go through a transition effect and possibly a local loading procedure.
     *
     * @param scene The new scene
     */
    void setScene(Scene scene);

    /**
     * Gets the current scene. If a new scene has been set, but the transition is not yet complete,
     * previous scene might be returned.
     *
     * @return The current scene
     */
    Scene getScene();

    /**
     * Gets the logical width of the stage. If the device is in portrait mode, this value is always
     * 600 and otherwise it is the scaled version of screen width to match the height of 600.
     *
     * @return Logical width in texels
     */
    float getLogicalWidth();

    /**
     * Gets the logical height of the stage. If the device is in landscape mode, this value is
     * always 600 and otherwise it is the scaled version of screen height to match the width of 600.
     *
     * @return Logical height in texels
     */
    float getLogicalHeight();

    /**
     * Adds a post-processing filter to the rendering pipeline. Filters will be applied in the order
     * they are added to the stage.
     *
     * @param filter The filter to be added
     */
    void addFilter(PostProcessingFilter filter);

    /**
     * Removes a post-processing filter from the rendering pipeline.
     *
     * @param filter The filter to be removed
     */
    void removeFilter(PostProcessingFilter filter);

    /**
     * Gets the currently assigned stage manager. A stage manager handles basic events and
     * functionality for this {@code Stage}.
     *
     * @return The stage manager for this stage
     */
    StageManager getManager();

    /**
     * Appoints a stage manager to handle required functionality for this {@code Stage}. You MUST
     * assign a stage manager before doing anything else with this {@code Stage}.
     *
     * @param stageManager The stage manager
     */
    void setManager(StageManager stageManager);
}
