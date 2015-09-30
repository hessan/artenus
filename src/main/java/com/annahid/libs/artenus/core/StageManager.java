package com.annahid.libs.artenus.core;

/**
 * Interface for all classes that handle stage events. This is one of the key interfaces that must
 * be implemented in any game that uses this framework. It is the companion of {@code Stage} and
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
     * Indicates that the stage has gone into a paused state. This event
     * is triggered when the activity's {@code onPause} is invoked.
     */
    int EVENT_PAUSE = 1;
    /**
     * Indicates that the stage has resumed from a paused state. This event
     * is triggered when the activity's {@code onResume} is invoked.
     */
    int EVENT_RESUME = 2;
    /**
     * Indicates that the stage has just allocated resources to display
     * content. This event is triggered when the OpenGL view's
     * {@code onSurfaceCreated} method is invoked.
     */
    int EVENT_DISPLAY = 3;

    /**
     * This method is called to load the required global resources for the
     * stage. The "loading" logic of the game should be implemented in this
     * method. Note that displaying a loading screen is handled automatically
     * and all the game developer needs to do is to load resources here.
     *
     * @param stage The stage for which the resources are going to be loaded.
     * @see com.annahid.libs.artenus.core.Stage
     */
    void onLoadStage(Stage stage);

    /**
     * Create the initial scene for the stage. It will be the first scene that
     * is displayed on the stage after the Artenus logo splash and the loading
     * scene. It can be a splash screen or the main menu scene of the game.
     * When creating the initial scene, you should NOT add it to the stage
     * manually. This will be done later automatically in the framework.
     *
     * @param stage The stage that the new scene belongs to
     * @return The first scene of the game
     * @see com.annahid.libs.artenus.core.Stage
     * @see com.annahid.libs.artenus.core.Scene
     */
    Scene createInitialScene(Stage stage);

    /**
     * This method is invoked whenever an external event is triggered. This
     * event can be one of "pause", "resume" or "display".
     *
     * @param stage   The stage for which the event has occurred
     * @param eventId The event identifier. The value is one of
     *                {@link StageManager#EVENT_PAUSE},
     *                {@link StageManager#EVENT_RESUME}, or
     *                {@link StageManager#EVENT_DISPLAY}.
     */
    void onEvent(Stage stage, int eventId);
}
