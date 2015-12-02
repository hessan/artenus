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

package com.annahid.libs.artenus.unified;

/**
 * Interface for classes that listen to ad placement events. Ad-driven games implement this
 * interface to adjust game content according the size of ad units.
 */
public interface AdPlacementListener {
    /**
     * Called when the ad unit changes state from hidden to visible, or vice versa.
     *
     * @param visible {@code true} if the ad unit is visible, {@code false} otherwise
     */
    void onAdVisibilityChange(boolean visible);

    /**
     * Called when the height of the ad unit changes. This includes when it virtually becomes 0 as a
     * result of hiding.
     *
     * @param height New height of the ad unit
     */
    void onHeightChange(int height);
}
