package com.annahid.libs.artenus.input;

/**
 * Represents a button. This can be a visual touch button on the screen, a physical button on a
 * game-pad, or anything else that can produce or simulate press and release events.
 */
public interface Button {
    /**
     * Gets the current listener responsible for this button's push and release events.
     */
    ButtonListener getListener();

    /**
     * Assigns a listener to this button to handle its push and release events.
     *
     * @param listener The listener
     */
    void setListener(ButtonListener listener);
}
