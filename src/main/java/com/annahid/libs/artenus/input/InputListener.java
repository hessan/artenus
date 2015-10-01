package com.annahid.libs.artenus.input;

/**
 * An interface for classes that response to input status changes.
 *
 * @author Hessan Feghhi
 */
public interface InputListener {
    /**
     * Signals this {@link InputListener} that a change has occurred in the input status. This can
     * be a slight change in the direction of the knob or the pressing or releasing of some action
     * buttons.
     *
     * @param input The calling input manager. Use this to retrieve information about the event
     */
    void inputStatusChanged(GameInput input);
}
