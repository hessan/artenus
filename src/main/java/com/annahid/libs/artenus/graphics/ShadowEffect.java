package com.annahid.libs.artenus.graphics;

import android.opengl.GLES10;

public final class ShadowEffect extends Effect {
	public ShadowEffect(float dx, float dy, float shadowAlpha) {
		this.dx = dx;
		this.dy = dy;
		this.shadowAlpha = shadowAlpha;
	}

	@Override
	public void render(Renderable renderable, float alpha) {
		GLES10.glPushMatrix();
		GLES10.glTranslatef(dx, dy, 0);
		GLES10.glColor4f(0, 0, 0, alpha * shadowAlpha);

		if (baseEffect == null)
			renderable.render(Renderable.FLAG_IGNORE_COLOR_FILTER | Renderable.FLAG_IGNORE_EFFECTS);
		else baseEffect.render(renderable, alpha);

		GLES10.glPopMatrix();

		if (baseEffect == null)
			renderable.render(Renderable.FLAG_IGNORE_EFFECTS);
		else baseEffect.render(renderable, alpha);
	}

	private float dx, dy, shadowAlpha;
}
