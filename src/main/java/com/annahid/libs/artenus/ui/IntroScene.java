package com.annahid.libs.artenus.ui;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.R;
import com.annahid.libs.artenus.graphics.sprites.ImageSprite;
import com.annahid.libs.artenus.graphics.TextureManager;

/**
 * This scene is displayed before the global loading screen. It splashes a logo
 * of the framework on the screen. This scene must not be in any way suppressed.
 * Doing so is against the terms of use of this framework.
 * 
 * @author Hessan Feghhi
 *
 */
final class IntroScene extends Scene {
	static boolean introShown = false;

	private long startTime;
	private ImageSprite annahid = null;
	private float whiteness = 0;
	private boolean gameRun = false;
	private boolean showIntro;

	public IntroScene(Stage parentStage) {
		super(parentStage);
		startTime = System.currentTimeMillis();
		showIntro = !Artenus.shouldHideIntro();
	}

	@Override
	public final void advance(float elapsedTime) {
		super.advance(elapsedTime);

		final long diff = System.currentTimeMillis() - startTime;

		if (showIntro) {
			if (annahid == null) {
				annahid = new ImageSprite(R.raw.annahid, new ImageSprite.Cutout(256, 128, 1));
				add(annahid);
			}

			annahid.setPosition(stage.getGLWidth() / 2, stage.getGLHeight() / 2);

			if (diff > 3500) {
				whiteness = Math.max(0, whiteness - elapsedTime * 2);
				annahid.setAlpha(Math.max(0, annahid.getAlpha() - elapsedTime * 4));
			} else whiteness = Math.min(1, whiteness + elapsedTime * 4);

			if (!gameRun) {
				setBackColor(whiteness, whiteness, whiteness);

				if (diff > 4000)
					runGame(stage);
				else {
					final float scale = annahid.getScale().x + 0.075f * elapsedTime;
					annahid.setScale(scale, scale);
				}
			}
		} else runGame(stage);
	}

	@Override
	public void onLocalLoad() {
		TextureManager.addLocal(R.raw.annahid);
	}

	private void runGame(Stage stage) {
		if (!gameRun) {
			remove(annahid);
			introShown = true;

			final StageManager manager = stage.getStageManager();

			if(manager == null)
				throw new IllegalStateException("No stage manager is specified.");

			manager.onLoadStage(stage);
			stage.forceScene(stage.getStageManager().createInitialScene(stage));
			gameRun = true;
		}
	}
}
