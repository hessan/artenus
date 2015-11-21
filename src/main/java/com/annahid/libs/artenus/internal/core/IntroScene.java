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

package com.annahid.libs.artenus.internal.core;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.R;
import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.core.Stage;
import com.annahid.libs.artenus.core.StageManager;
import com.annahid.libs.artenus.graphics.sprites.ImageSprite;
import com.annahid.libs.artenus.graphics.TextureManager;

import java.lang.ref.WeakReference;

/**
 * Represents the splash screen that is displayed before the global loading screen. It splashes a
 * logo of the framework on the screen. It is recommended that you do not suppress this scene.
 *
 * @author Hessan Feghhi
 */
final class IntroScene extends Scene {
    /**
     * Indicates whether the intro scene has already been displayed.
     */
    static boolean introShown = false;

    /**
     * Holds the singleton instance.
     */
    private static WeakReference<Scene> instance = null;

    /**
     * Holds the white intensity of the screen (used for the logo animation).
     */
    private float whiteness = 0;

    /**
     * Used to avoid creating multiple instances of the game.
     */
    private boolean gameRun = false;

    private ImageSprite annahid = null;

    private final boolean showIntro;

    /**
     * Holds the timestamp for when the intro scene started to display.
     */
    private final long startTime;

    private IntroScene(Stage parentStage) {
        super(parentStage);
        startTime = System.currentTimeMillis();
        showIntro = !Artenus.shouldHideIntro();
    }

    public static Scene getInstance(Stage stage) {
        if (instance == null) {
            instance = new WeakReference<Scene>(new IntroScene(stage));
        }
        return instance.get();
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

            annahid.setPosition(stage.getLogicalWidth() / 2, stage.getLogicalHeight() / 2);

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

            final StageManager manager = stage.getManager();

            if (manager == null)
                throw new IllegalStateException("No stage manager is specified.");

            manager.onLoadStage(stage);
            ((StageImpl) stage).forceScene(stage.getManager().createInitialScene(stage));
            gameRun = true;
        }
    }
}
