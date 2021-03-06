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

import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.input.TouchEvent;
import com.annahid.libs.artenus.input.TouchMap;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.EntityCollection;
import com.annahid.libs.artenus.input.GameInput;
import com.annahid.libs.artenus.physics.PhysicsSimulator;
import com.annahid.libs.artenus.entities.behavior.Touchable;

/**
 * Represents a single scene in a game. If you view the whole game as a play, the terms for {@link
 * Stage} and {@code Scene} will make complete sense, as they are intentionally named this way in
 * the framework. A game can have several scenes based on its nature. For example, a simple game
 * consists of a menu scene, a game scene and a game over scene where scores and rankings are shown.
 * Scenes methods provide the means to manipulate stage properties and navigate between scenes.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public class Scene implements Touchable {
    /**
     * Holds the parent stage for this scene. You MUST NOT modify the value of this field from
     * within subclasses, as this might cause inconsistency in the scene-stage relation.
     */
    protected Stage stage;

    /**
     * Holds the current dialog being displayed over the scene. It is {@code null} if there is no
     * dialog present.
     */
    Dialog dialog = null;

    /**
     * Holds the background color of the scene.
     */
    private RGB bgColor = new RGB(0, 0, 0);

    /**
     * Contains entities that make up the scene.
     */
    private final EntityCollection entities;

    /**
     * Holds the physics simulator responsible for this scene. This value can be {@code null}.
     */
    private PhysicsSimulator physics;

    /**
     * Holds the touch map responsible for this scene.
     */
    private TouchMap touchManager;

    /**
     * Indicates whether the scene is in a loaded state.
     */
    private boolean loaded;

    /**
     * Holds the timestamp of the instant the scene went to a halt (e.g. to show a dialog).
     */
    private long haltStart;

    /**
     * Creates a new scene belonging to the given {@code Stage}.
     *
     * @param parentStage The parent stage
     *
     * @see Stage
     */
    public Scene(Stage parentStage) {
        stage = parentStage;
        entities = new EntityCollection();
        entities.onAttach(this);
        physics = null;
        loaded = false;
        haltStart = 0;
        touchManager = new TouchMap();
    }

    /**
     * Gets the default physics simulator for this {@code Scene}. The physics simulator is
     * originally {@code null}, but it is allocated on the first access, including the invocation of
     * this method.
     *
     * @return The physics simulator
     *
     * @see PhysicsSimulator
     */
    public final PhysicsSimulator getPhysicsSimulator() {
        if (physics == null) {
            physics = new PhysicsSimulator();
        }
        return physics;
    }

    public final TouchMap getTouchMap() {
        return touchManager;
    }

    /**
     * Gets the background color for this scene. The returned object is not a copy, and
     * modifications will take effect in the scene.
     *
     * @return The background color
     */
    public final RGB getBackColor() {
        return bgColor;
    }

    /**
     * Sets the background color for this scene.
     *
     * @param color The background color
     */
    public final void setBackColor(RGB color) {
        bgColor.r = color.r;
        bgColor.g = color.g;
        bgColor.b = color.b;
    }

    /**
     * Sets the background color for this scene.
     *
     * @param r The red component of the background color
     * @param g The green component of the background color
     * @param b The blue component of the background color
     */
    public final void setBackColor(float r, float g, float b) {
        bgColor.r = r;
        bgColor.g = g;
        bgColor.b = b;
    }

    /**
     * Adds an entity to the scene.
     *
     * @param entity The entity to be added
     */
    public void add(Entity entity) {
        entities.add(entity);
    }

    /**
     * Removes an entity from the scene.
     *
     * @param entity The entity to be removed
     */
    public final void remove(Entity entity) {
        entities.recursiveRemove(entity);
    }

    /**
     * Advances the animation for this scene. Subclasses should always call this superclass method,
     * as it handles physics and animation.
     *
     * @param elapsedTime The time elapsed since last frame
     */
    public void advance(float elapsedTime) {
        if (dialog != null) {
            dialog.advance(elapsedTime);
        } else {
            touchManager.dispatch();
        }
        if (!isHalted()) {
            entities.advance(elapsedTime);
            if (physics != null) {
                physics.step(elapsedTime, 6, 4);
                physics.handleCollisions();
            }
        }
    }

    /**
     * Halts the scene. All animation and physics will stop until the scene is unhalted. Note that
     * although dialogs are also scenes, they cannot be halted.
     *
     * @see Dialog
     */
    public final void halt() {
        if (!isDialog()) {
            haltStart = System.currentTimeMillis();
            onHalted();
        }
    }

    /**
     * Indicates whether the scene is currently halted.
     *
     * @return {@code true} if the scene is halted, and {@code false} otherwise
     */
    public final boolean isHalted() {
        return haltStart > 0;
    }

    /**
     * Un-halts a previously halted scene. All animation and physics will resume.
     */
    public final void unhalt() {
        if (!isDialog()) {
            final float delay = (float) (System.currentTimeMillis() - haltStart) / 1000.0f;
            haltStart = 0;
            onUnhalted(delay);
        }
    }

    /**
     * Gets the current dialog that is currently showing on the scene. Dialogs halt the scene until
     * they are dismissed.
     *
     * @return The current dialog
     *
     * @see Dialog
     */
    public final Dialog getDialog() {
        return dialog;
    }

    /**
     * Gets the stage that this scene is displayed on.
     *
     * @return The parent stage
     */
    public final Stage getStage() {
        return stage;
    }

    /**
     * Indicates whether the resources for this scene are loaded.
     *
     * @return {@code true} if loaded, and {@code false} otherwise
     */
    public final boolean isLoaded() {
        return loaded;
    }

    /**
     * Handles the back button event. Subclasses can override this method to handle the back button.
     * Calling the superclass method is not necessary.
     *
     * @return {@code true} if handled, {@code false} if passed
     */
    public boolean onBackButton() {
        return true;
    }

    /**
     * Called by the framework when all resources for the scene are safely in place. It is a good
     * entry point for subclasses to create their objects and entities. Remember to call the
     * superclass method.
     */
    public void onLoaded() {
        loaded = true;
    }

    /**
     * Called by the framework to load scene-local resources. If a scene has local resources, it
     * should implement this method to load them. Within this method you can call the {@code
     * addLocal} method from {@code TextureManager} to load your resources. Local resources are
     * automatically unloaded when the scene changes.
     */
    public void onLocalLoad() {
    }

    /**
     * Handles input for this scene.
     *
     * @param inputManager The input manager that triggered the event
     */
    public void handleInput(GameInput inputManager) {
    }

    /**
     * Called by the framework after this scene goes to a halted state. You can override this method
     * for example to pause your game timers.
     */
    protected void onHalted() {
    }

    /**
     * Called by the framework after this scene goes out of a halted state. You can use this method
     * to resume your game timers.
     *
     * @param delay The time passed, in seconds, in the halted state
     */
    protected void onUnhalted(float delay) {
    }

    /**
     * Called by the framework whenever a dialog is dismissed from the scene. Dialogs always invoke
     * this method on their parent scene when they are dismissed.
     *
     * @param dialog The dismissed dialog
     *
     * @see Dialog
     */
    protected void onDialogDismissed(Dialog dialog) {
    }

    @Override
    public boolean handleTouch(TouchEvent event) {
        return entities.handleTouch(event);
    }

    /**
     * Renders the scene on the given OpenGL context.
     */
    public final void render(RenderingContext context) {
        boolean skipRender = false;

        if (dialog != null) {
            skipRender = dialog.isFull();
        }
        if (!skipRender) {
            entities.render(context, 0);
        }
        if (dialog != null) {
            if (!dialog.isLoaded()) {
                dialog.onLoaded();
            }
            dialog.render(context);
        }
    }

    /**
     * Returns a value indicating whether this scene is a dialog.
     *
     * @return {@code false}
     */
    boolean isDialog() {
        return false;
    }
}
