package com.annahid.libs.artenus.entities.behavior;

import com.annahid.libs.artenus.input.TouchEvent;

/**
 * Interface for all entities that can receive touch events.
 *
 * @author Hessan Feghhi
 */
public interface Touchable {
    /**
     * This method is called whenever a touch event arrives.
     *
     * @param event Event information
     * @return {@code true} if handled, {@code false} otherwise
     */
    boolean handleTouch(TouchEvent event);
}
