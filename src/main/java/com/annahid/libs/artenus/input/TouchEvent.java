package com.annahid.libs.artenus.input;

/**
 * Represents a touch event in the Artenus framework.
 *
 * @author Hessan Feghhi
 */
public class TouchEvent {
    /**
     * Indicates that a pressed gesture has just started.
     */
    public static final int EVENT_DOWN = 0;
    /**
     * Indicates that a pressed gesture has finished.
     */
    public static final int EVENT_UP = 1;
    /**
     * Indicates that a change has happened during a press gesture (between
     * {@link TouchEvent#EVENT_UP} and
     * {@link TouchEvent#EVENT_DOWN}).
     */
    public static final int EVENT_MOVE = 2;

    /**
     * Indicates that a pressed gesture has finished outside the desired area. This value is used
     * internally to signal touch buttons when the touch sequence is finished and they should
     * cancel the button press event.
     */
    public static final int EVENT_LEAVE = 3;

    /**
     * The x coordinate of the touch event.
     */
    float x;

    /**
     * The y coordinate of the touch event.
     */
    float y;

    /**
     * The action type of the touch event.
     */
    int action;

    /**
     * The pointer identifier associated to the touch event (for multi-touch systems).
     */
    int pointerId;

    /**
     * Creates a new touch event.
     *
     * @param action    Action type, which can be one of {@link #EVENT_DOWN}, {@link #EVENT_MOVE},
     *                  {@link #EVENT_UP}, or {@link #EVENT_LEAVE}
     * @param pointerId The pointer identifier associated with this touch event
     * @param x         The x coordinate of the touched or released point
     * @param y         The y coordinate of the touched or released point
     */
    public TouchEvent(int action, int pointerId, float x, float y) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.pointerId = pointerId;
    }

    /**
     * Gets the x coordinate of the touched or released point (depending on the action).
     *
     * @return The x coordinate
     */
    public float getX() {
        return x;
    }


    /**
     * Gets the y coordinate of the touched or released point (depending on the action).
     *
     * @return The y coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Gets the action associated to this touch event.
     *
     * @return One of {@link #EVENT_DOWN}, {@link #EVENT_MOVE}, {@link #EVENT_UP}, or
     * {@link #EVENT_LEAVE}
     */
    public int getAction() {
        return action;
    }

    /**
     * Gets the pointeer identifier associated with this touch event (for multi-touch systems).
     *
     * @return The pointer identifier
     */
    public int getPointerId() {
        return pointerId;
    }
}
