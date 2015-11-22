package com.annahid.libs.artenus.graphics.animation;

/**
 * Animation trends that specify how the animation is repeated. The default is LOOP.
 */
public enum Trends {
    /**
     * Trend that involves playing frames in a loop.
     */
    LOOP,

    /**
     * Trend that involves playing frames only once. Once the animation reaches the final frame, it
     * stops at that frame.
     */
    ONCE,

    /**
     * Trend that plays frames backwards after it reaches the final frame and goes into a forward
     * trend when it reaches the first frame and plays this way in a loop.
     */
    PING_PONG
}
