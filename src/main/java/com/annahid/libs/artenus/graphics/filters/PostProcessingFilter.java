package com.annahid.libs.artenus.graphics.filters;

import com.annahid.libs.artenus.graphics.rendering.FrameSetup;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;

/**
 * Interface for post-processing filters. These filters are applied to the rendered frame after the
 * rendering is complete, and before the frame is displayed on the screen. Color tinting and frame
 * blurring are examples of post-processing filters.
 *
 * @author Hessan Feghhi
 */
public interface PostProcessingFilter {
    /**
     * Sets up the current rendering pass for this filter. A filter can process the render output in
     * several passes, and each pass will have access to the result of the previous. Pass 0 is where
     * the raw render output is given to the filter for processing. The frame setup for the previous
     * pass (or for the render output) is given to this function, which can be modified for the next
     * pass. Width and height specified for the next pass cannot exceed raw output dimensions.
     *
     * @param pass  Current pass number (starting at 0)
     * @param setup The frame setup for the previous pass
     * @return A value indicating whether another pass is required to complete this filter (other
     * than the current pass that is being set up)
     */
    boolean setup(int pass, FrameSetup setup);

    /**
     * Renders the frame for the current pass. On pass 0 the raw rendering output is given to the
     * function, and subsequent passes receive output from their predecessor. The rendering context
     * can be used to draw elements for the next pass (if there is one), or for the final output of
     * this filter. Note that the result of the final pass might still be processed by other
     * post-processing filters before actually being displayed on the screen.
     *
     * @param pass          This current pass number (starting at 0)
     * @param context       The rendering context
     * @param renderedFrame The raw rendered frame if pass is 0, or the result of the previous pass
     */
    void render(int pass, RenderingContext context, RenderTarget renderedFrame);
}
