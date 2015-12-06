package com.annahid.libs.artenus.graphics.effects;

import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.FilteredEntity;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;

/**
 * Renders the underlying entity with a shadow effect.
 *
 * @author Hessan Feghhi
 */
public class DropShadow extends FilteredEntity {
    /**
     * Holds the horizontal distance of the shadow from the renderable entity.
     */
    private float dx;

    /**
     * Holds the vertical distance of the shadow from the renderable entity.
     */
    private float dy;

    /**
     * Holds the alpha transparency value of the shadow.
     */
    private float shadowAlpha;

    /**
     * Creates a new drop shadow effect with given parameters.
     *
     * @param target The renderable entity to apply the effect to
     * @param dx Horizontal position offset of the shadow
     * @param dy Vertical position offset of the shadow
     * @param shadowAlpha Transparency value of the shadow
     */
    public DropShadow(Entity target, float dx, float dy, float shadowAlpha) {
        super(target);

        if (!(target instanceof Renderable)) {
            throw new IllegalArgumentException("Drop shadow can only work with renderables.");
        }

        this.dx = dx;
        this.dy = dy;
        this.shadowAlpha = shadowAlpha;
    }

    /**
     * Creates a new drop shadow effect for the given renderable entity.
     *
     * @param target The renderable entity to apply the effect to
     */
    public DropShadow(Entity target) {
        this(target, 2, 2, 0.3f);
    }

    /**
     * Sets the alpha transparency value of the shadow.
     *
     * @param shadowAlpha Transparency value of the shadow
     * @return This instance
     */
    public DropShadow setShadowAlpha(float shadowAlpha) {
        this.shadowAlpha = shadowAlpha;
        return this;
    }

    /**
     * Sets the position offset of this drop shadow effect.
     *
     * @param dx Horizontal position offset of the shadow
     * @param dy Vertical position offset of the shadow
     * @return This instance
     */
    public DropShadow setOffset(float dx, float dy) {
        this.dx = dx;
        this.dy = dy;
        return this;
    }

    @Override
    public void render(RenderingContext context, int flags) {
        final ShaderProgram shaderBackup = context.getShader();
        context.setShader(ShadowShaderProgram.getInstance());
        context.pushMatrix();
        context.translate(dx, dy);
        ShadowShaderProgram.getInstance().feed(shadowAlpha);
        super.render(context, flags | Renderable.FLAG_PRESERVE_SHADER_PROGRAM);
        context.popMatrix();
        context.setShader(shaderBackup);
        super.render(context, flags);
    }
}
