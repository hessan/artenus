package com.annahid.libs.artenus.core;

import com.annahid.libs.artenus.data.RGB;

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
    float screenToStageX(float x);

    /**
     * Converts screen y to stage x coordination using the scaling factor.
     *
     * @param y Screen y
     * @return Stage y
     */
    float screenToStageY(float y);

    /**
     * Converts stage x to screen x coordination using the scaling factor.
     *
     * @param x Stage x
     * @return Screen x
     */
    float stageToScreenX(float x);

    /**
     * Converts stage y to screen y coordination using the scaling factor.
     *
     * @param y Stage y
     * @return Screen y
     */
    float stageToScreenY(float y);

    /**
     * Sets the next scene that should be shown on this {@code Stage}. The
     * scene will not be shown immediately and will go through a transition
     * effect and possibly a local loading procedure.
     *
     * @param scene The new scene
     */
    void setScene(Scene scene);

    /**
     * Gets the current scene. If a new scene has been set, but the transition
     * is not yet complete, previous scene might be returned.
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
     * Sets the default background color for scenes that don't specify one.
     *
     * @param rgb The default color
     */
    void setDefaultBackColor(RGB rgb);

    /**
     * Sets the amount of blur on this stage.
     *
     * @param blur The value of a blur in the range 0 to 1
     * @deprecated This method will be removed in a later release, as blurring will be handled
     * through effects.
     */
    void setBlur(float blur);

    /**
     * Gets the current value of blur specified for this {@code Stage}.
     *
     * @return The blur value in the range 0 to 1
     * @deprecated This method will be removed in a later release, as blurring will be handled
     * through effects.
     */
    float getBlur();

    /**
     * This method handles the physical back button for this {@code Stage}.
     *
     * @return {@code true} if the back button has been handled by this {@code Stage} or the
     * currently active {@code Scene}, {@code false} otherwise
     * @see com.annahid.libs.artenus.core.Scene
     */
    boolean onBackButton();

    /**
     * Registers a shader program with this stage, so the stage handles its lifecycle. All internal
     * shader programs are automatically registered.
     *
     * @param shader The shader program to be registered
     */
    void registerShader(ShaderProgram shader);

    /**
     * Unregisters a shader program from this stage.
     *
     * @param shader The shader to be unregisterd
     */
    void unregisterShader(ShaderProgram shader);

    /**
     * Gets the currently assigned stage manager. A stage manager handles basic events
     * and functionality for this {@code Stage}.
     *
     * @return The stage manager for this stage
     */
    StageManager getManager();

    /**
     * Appoints a stage manager to handle required functionality for this {@code Stage}.
     * You MUST assign a stage manager before doing anything else with this {@code Stage}.
     *
     * @param stageManager The stage manager
     */
    void setManager(StageManager stageManager);
}
