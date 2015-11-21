/*
 *  This file is part of the Artenus 2D Framework.
 *  Copyright (C) 2015  Hessan Feghhi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Foobar is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.annahid.libs.artenus.graphics.sprites;

import com.annahid.libs.artenus.entities.AnimationHandler;
import com.annahid.libs.artenus.entities.behavior.Animatable;

/**
 * Performs an animation based on the frames contained in an image sprite's cutout. Note that this
 * class targets only image sprites. You must only assign this animation to an instance of
 * {@link com.annahid.libs.artenus.graphics.sprites.ImageSprite}.
 *
 * @author Hessan Feghhi
 * @see com.annahid.libs.artenus.graphics.sprites.ImageSprite
 */
@SuppressWarnings("UnusedDeclaration")
public final class ImageAnimation implements AnimationHandler {
    /**
     * The Trend specifies how the animation frames are repeated. The default is LOOP.
     */
    public enum Trend {
        /**
         * Animation trend that involves playing frames in a loop
         */
        LOOP,
        /**
         * Animation trend that involves playing frames only once. Once the
         * animation reaches the final frame, it stops at that frame.
         */
        ONCE,
        /**
         * Animation trend that plays frames backwards after it reaches the
         * final frame and goes into a forward trend when it reaches the first
         * frame and plays this way in a loop.
         */
        PING_PONG
    }

    /**
     * The list of frames that will be displayed for the animation.
     */
    private int[] frames;

    /**
     * Animation trend.
     */
    private Trend trend;

    /**
     * Array index to the currently displayed frame.
     */
    private int currentFrame;

    /**
     * The sign of the increment.
     */
    private int delta;

    /**
     * The delay in milliseconds between frames.
     */
    private int frameDelay = 33;

    /**
     * The timestamp for the previous frame.
     */
    private long lastFrame = 0;

    /**
     * Creates an {@code ImageAnimation} with the given set of frames and a loop trend.
     *
     * @param animationFrames The array of frames from the cutout to animate through
     * @see Trend#LOOP
     */
    public ImageAnimation(int[] animationFrames) {
        this(animationFrames, Trend.LOOP, 0);
    }

    /**
     * Creates an {@code ImageAnimation} with the given set of frames and trend.
     *
     * @param animationFrames The array of frames from the cutout to animate through
     * @param trend           The trend at which the frames will be animated
     * @see Trend
     */
    public ImageAnimation(int[] animationFrames, Trend trend) {
        this(animationFrames, trend, 0);
    }

    /**
     * Creates an {@code ImageAnimation} with the given set of frames and trend. The animation
     * is started at a given frame index.
     *
     * @param animationFrames The array of frames from the cutout to animate through
     * @param trend           The trend at which the frames will be animated
     * @param startIndex      The frame index at which the animation should start. This index
     *                        determines the item in the frame array and not the frame index
     *                        belonging to the sprite's cutout.
     * @see Trend
     * @see com.annahid.libs.artenus.graphics.sprites.ImageSprite.Cutout
     */
    public ImageAnimation(int[] animationFrames, Trend trend, int startIndex) {
        frames = animationFrames;
        this.trend = trend;
        delta = 1;
        currentFrame = startIndex;
    }

    /**
     * Adjusts frame interval for the animation. This is a legacy method which is still supported.
     * It is recommended to use {@link #setFrameDelay(float)} instead as it is guaranteed not to
     * be deprecated in future versions.
     *
     * @param delay The interval between frames in milliseconds
     * @see #setFrameDelay(float)
     */
    public void setFrameDelay(int delay) {
        frameDelay = delay;
        lastFrame = System.currentTimeMillis();
    }

    /**
     * Adjusts frame interval for the animation. The difference between this method and
     * {@link #setFrameDelay(int)} is that this method gets the delay in seconds and in floating
     * point format, which is compatible with the rest of the framework.
     *
     * @param delay The interval between frames in seconds
     * @see #setFrameDelay(int)
     */
    public void setFrameDelay(float delay) {
        frameDelay = (int) (delay * 1000);
        lastFrame = System.currentTimeMillis();
    }

    /**
     * Gets the current frame index of the animation.
     *
     * @return The current frame of the animation. This value indicates the index in the frame
     * array, and not the frame index belonging to the sprite's cutout.
     * @see com.annahid.libs.artenus.graphics.sprites.ImageSprite.Cutout
     */
    public int getFrame() {
        return currentFrame;
    }

    /**
     * Gets the trend of this image animation. The value can  be one of {@link Trend#LOOP},
     * {@link Trend#ONCE} or {@link Trend#PING_PONG}.
     *
     * @return The animation trend
     * @see Trend
     */
    public Trend getTrend() {
        return trend;
    }

    /**
     * Called whenever the animation should update the image sprite based on elapsed time.
     * Frame timing is automatically handled based on your adjustment using
     * {@link #setFrameDelay(int)}.
     *
     * @see ImageAnimation#setFrameDelay(int)
     */
    @Override
    public void advance(Animatable sprite, float elapsedTime) {
        if (System.currentTimeMillis() - lastFrame >= frameDelay)
            lastFrame = System.currentTimeMillis();
        else return;

        switch (trend) {
            case LOOP:
                currentFrame = (currentFrame + 1) % frames.length;
                break;
            case PING_PONG:
                currentFrame += delta;

                if (currentFrame == 0 || currentFrame == frames.length - 1)
                    delta = -delta;
                break;
            default:
                if (currentFrame < frames.length - 1)
                    currentFrame++;
        }

        ((ImageSprite) sprite).gotoFrame(frames[currentFrame]);
    }
}
