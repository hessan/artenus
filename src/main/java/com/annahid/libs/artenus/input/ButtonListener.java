package com.annahid.libs.artenus.input;

/**
 * The interface for all classes that handle push button events.
 *
 * @author Hessan Feghhi
 */
public interface ButtonListener {
    /**
     * Called when a button is pressed.
     *
     * @param button The button that is pressed
     */
    void onPress(Button button);

    /**
     * Called when a button is released, regardless of whether or not the user meant to activate
     * the action associated with the button. This is usually the case for virtual buttons, where
     * the user has a choice to either complete the click, or lift their finger or the mouse
     * outside the button's hot region to cancel the event. If the button has an action associated
     * only with its release event and the interval between the press and the release does not have
     * any effect, it is recommended NOT to take that action if the {@code cancel} argument is true.
     *
     * @param button The button that is released
     * @param cancel true if the event is cancelled, false otherwise
     */
    void onRelease(Button button, boolean cancel);
}
