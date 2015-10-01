package com.annahid.libs.artenus.graphics;

import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.core.RenderingContext;

/**
 * A subclass of {@link com.annahid.libs.artenus.graphics.Effect} that represents a shadow effect.
 *
 * @author Hessan Feghhi
 */
public final class ShadowEffect extends Effect {
    /**
     * The horizontal distance of the shadow from the renderable entity.
     */
    private float dx;

    /**
     * The vertical distance of the shadow from the renderable entity.
     */
    private float dy;

    /**
     * The alpha transparency value of the shadow.
     */
    private float shadowAlpha;

    /**
     * Constructs a new shadow effect with specified properties.
     *
     * @param dx          Horizontal distance of the shadow
     * @param dy          Vertical distance of the shadow
     * @param shadowAlpha The transparency value of the shadow
     */
    public ShadowEffect(float dx, float dy, float shadowAlpha) {
        this.dx = dx;
        this.dy = dy;
        this.shadowAlpha = shadowAlpha;
    }

    @Override
    public void render(RenderingContext context, Renderable renderable, float alpha) {
        context.pushMatrix();
        context.translate(dx, dy);
        context.setColorFilter(0, 0, 0, alpha * shadowAlpha);

        if (baseEffect == null) {
            renderable.render(
                    context,
                    Renderable.FLAG_IGNORE_COLOR_FILTER | Renderable.FLAG_IGNORE_EFFECTS
            );
        } else baseEffect.render(context, renderable, alpha);

        context.popMatrix();

        if (baseEffect == null)
            renderable.render(context, Renderable.FLAG_IGNORE_EFFECTS);
        else baseEffect.render(context, renderable, alpha);
    }
}
