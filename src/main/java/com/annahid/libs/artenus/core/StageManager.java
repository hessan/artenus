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

/**
 * Interface for classes that handle stage events. This is one of the key interfaces that must be
 * implemented in any game that uses this framework. It is the companion of {@code Stage} and
 * handles basic functionality for the game. You can assign a {@code StageManager} to your stage
 * using {@link com.annahid.libs.artenus.core.Stage#setManager}. Failing to provide a stage manager
 * will cause your application to crash with an {@code IllegalStateException} when it tries to load
 * the first scene.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.core.Stage
 */
public interface StageManager {

    /**
     * Called by the framework to load the required global resources for the stage. The "loading"
     * logic of the game should be implemented in this method. Note that displaying a loading screen
     * is handled automatically and all the game developer needs to do is to load resources here.
     *
     * @param stage The stage for which the resources are going to be loaded.
     *
     * @see com.annahid.libs.artenus.core.Stage
     */
    void onLoadStage(Stage stage);

    /**
     * Creates the initial scene for the stage. It will be the first scene that is displayed on the
     * stage after the Artenus logo splash and the loading scene. It can be a splash screen or the
     * main menu scene of the game. When creating the initial scene, you should NOT add it to the
     * stage manually. This will be done later automatically in the framework.
     *
     * @param stage The stage that the new scene belongs to
     *
     * @return The first scene of the game
     *
     * @see com.annahid.libs.artenus.core.Stage
     * @see com.annahid.libs.artenus.core.Scene
     */
    Scene createInitialScene(Stage stage);

    /**
     * Invoked by the framework whenever an external event is triggered. This event can be one of
     * "pause", "resume" or "display".
     *
     * @param stage Stage for which the event has occurred
     * @param event The event
     */
    void onEvent(Stage stage, StageEvents event);
}
