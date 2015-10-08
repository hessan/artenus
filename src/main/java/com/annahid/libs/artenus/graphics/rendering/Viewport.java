package com.annahid.libs.artenus.graphics.rendering;

/**
 * Represents a viewport for a render target. A viewport is an area on the frame that is used for
 * rendering. The rest of the available area on the target will not be touched.
 */
public class Viewport {
    /**
     * Viewport width.
     */
    protected int width;

    /**
     * Viewport height.
     */
    protected int height;

    /**
     * Creates a new viewport with given width and height.
     * @param width Viewport width
     * @param height Viewport height
     */
    public Viewport(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the frame width in this viewport.
     *
     * @return Frame width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Gets the frame height for this viewport.
     *
     * @return Frame height
     */
    public int getHeight() {
        return this.height;
    }
}
