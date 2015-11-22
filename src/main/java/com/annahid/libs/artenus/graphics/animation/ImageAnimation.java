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

package com.annahid.libs.artenus.graphics.animation;

import com.annahid.libs.artenus.entities.behavior.Animatable;
import com.annahid.libs.artenus.graphics.sprites.ImageSprite;

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
     * Contains frames that will be displayed for the animation, in order of appearance.
     */
    private int[] frames;

    /**
     * Holds animation trend.
     */
    private Trends trend;

    /**
     * Holds array index to the currently displayed frame.
     */
    private int currentFrame;

    /**
     * Holds the sign of the increment.
     */
    private int delta;

    /**
     * Holds the delay in milliseconds between frames.
     */
    private int frameDelay = 33;

    /**
     * Holds the timestamp for the previous frame.
     */
    private long lastFrame = 0;

    /**
     * Creates an {@code ImageAnimation} with the given set of frames and a loop trend.
     *
     * @param animationFrames The array of frames from the cutout to animate through
     *
     * @see Trends#LOOP
     */
    public ImageAnimation(int[] animationFrames) {
        this(animationFrames, Trends.LOOP, 0);
    }

    /**
     * Creates an {@code ImageAnimation} with the given set of frames and trend.
     *
     * @param animationFrames The array of frames from the cutout to animate through
     * @param trend           The trend at which the frames will be animated
     *
     * @see Trends
     */
    public ImageAnimation(int[] animationFrames, Trends trend) {
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
     *
     * @see Trends
     * @see com.annahid.libs.artenus.graphics.sprites.ImageSprite.Cutout
     */
    public ImageAnimation(int[] animationFrames, Trends trend, int startIndex) {
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
     *
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
     *
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
     *
     * @see com.annahid.libs.artenus.graphics.sprites.ImageSprite.Cutout
     */
    public int getFrame() {
        return currentFrame;
    }

    /**
     * Gets the trend of this image animation. The value can  be one of {@link Trends#LOOP},
     * {@link Trends#ONCE} or {@link Trends#PING_PONG}.
     *
     * @return The animation trend
     *
     * @see Trends
     */
    public Trends getTrend() {
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
