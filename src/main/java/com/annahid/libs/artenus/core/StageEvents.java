package com.annahid.libs.artenus.core;

/**
 * Events that can happen during the stage's lifecycle. The stage notifies its manager whenever any
 * of these events occur.
 *
 * @author Hessan Feghhi
 * @see StageManager
 */
public enum StageEvents {
    /**
     * Indicates that the stage has gone into a paused state. This event is triggered when the
     * Artenus activity goes in the background.
     */
    PAUSE(1),
    /**
     * Indicates that the stage has resumed from a paused state. This event is triggered when the
     * Artenus activity resumes from a background state.
     */
    RESUME(2),
    /**
     * Indicates that the stage has just allocated resources to display content. This event is
     * triggered when the OpenGL view's {@code onSurfaceCreated} method is invoked.
     */
    DISPLAY(3);

    /**
     * The current value of this enumeration.
     */
    private final int value;

    /**
     * Creates a new stage event.
     *
     * @param value Event
     */
    StageEvents(int value) {
        this.value = value;
    }

    /**
     * Gets the current value of this enumeration.
     *
     * @return Current value
     */
    public int getValue() {
        return value;
    }
}
