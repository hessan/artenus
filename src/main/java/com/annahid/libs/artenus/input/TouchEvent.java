package com.annahid.libs.artenus.input;

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
    public static final int EVENT_LEAVE = 3;

    float x, y;
    int action, pointerId;

    public TouchEvent(int action, int pointerId, float x, float y) {
        this.x = x;
        this.y = y;
        this.action = action;
        this.pointerId = pointerId;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getAction() {
        return action;
    }

    public int getPointerId() {
        return pointerId;
    }
}
