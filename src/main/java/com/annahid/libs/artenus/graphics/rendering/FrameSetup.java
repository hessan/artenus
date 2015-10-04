package com.annahid.libs.artenus.graphics.rendering;

/**
 * Holds information about the frame that is going to be rendered. Post-processing filters can
 * modify the frame size or other information about the frame for each pass. This class is used to
 * keep track of the changes, and pass it along the subsequent passes and filters.
 *
 * @author Hessan Feghhi
 */
public class FrameSetup {
    /**
     * Frame width.
     */
    private int width;

    /**
     * Frame height.
     */
    private int height;

    /**
     * Creates a new instance of {@code FrameSetup} with default information.
     */
    public FrameSetup() {
        width = height = 1;
    }

    /**
     * Creates a new instance of {@code FrameSetup} based on a previous setup.
     *
     * @param baseSetup Base setup to copy from
     */
    public FrameSetup(FrameSetup baseSetup) {
        this.width = baseSetup.width;
        this.height = baseSetup.height;
    }

    /**
     * Gets the frame width in this setup.
     *
     * @return Frame width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Sets the frame width for this setup.
     *
     * @param width Frame width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Gets the frame height for this setup.
     *
     * @return Frame height
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the frame height for this setup.
     *
     * @param height Frame height
     */
    public void setHeight(int height) {
        this.height = height;
    }
}
