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

package com.annahid.libs.artenus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.filters.BlurFilter;
import com.annahid.libs.artenus.graphics.rendering.ShaderManager;
import com.annahid.libs.artenus.internal.core.StageImpl;
import com.annahid.libs.artenus.sound.SoundManager;
import com.annahid.libs.artenus.core.Stage;
import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.AdLayout;

import java.lang.ref.WeakReference;

/**
 * The abstract superclass of a typical Artenus SDK activity. This class should be
 * extended by the game application and the {@code init()} method must be implemented
 * to handle basic initializations and retrieving the stage.
 *
 * @author Hessan Feghhi
 */
public abstract class Artenus extends Activity {
    private static Artenus instance;
    private static boolean hideIntro;
    private static UnifiedServices.Store manifestStore;
    private WeakReference<StageImpl> stage;
    private AudioManager audio;
    private boolean hasOutFocused = false;

    /**
     * Constructs a new instance of Artenus activity.
     *
     * @param hideIntro A value indicating whether to hide the initial splash screen
     */
    protected Artenus(boolean hideIntro) {
        Artenus.hideIntro = hideIntro;
    }

    /**
     * Constructs a new instance of Artenus activity.
     */
    protected Artenus() {
        this(false);
    }

    /**
     * Gets the currently running instance of {@code Artenus}.
     *
     * @return The running instance
     */
    public static Artenus getInstance() {
        return instance;
    }

    /**
     * Determines whether an intro screen should be displayed before the game.
     *
     * @return true if there should be an intro, false otherwise
     */
    public static boolean shouldHideIntro() {
        return hideIntro;
    }

    /**
     * Gets the store preference specified in the application manifest for unified services.
     *
     * @return Store identifier
     * @see com.annahid.libs.artenus.unified.UnifiedServices
     */
    public static UnifiedServices.Store getManifestAppStore() {
        return manifestStore;
    }

    /**
     * Gets the game stage. Each game has only one stage, where its different scenes are displayed.
     *
     * @return The stage
     */
    public Stage getStage() {
        return stage.get();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        instance = this;

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(
                    getPackageName(), PackageManager.GET_META_DATA);
            final String store = ai.metaData.getString("com.annahid.libs.artenus.APP_STORE");
            manifestStore = UnifiedServices.Store.NONE;

            if (store != null) {
                switch (store) {
                    case "google":
                        manifestStore = UnifiedServices.Store.GOOGLE;
                        break;
                    case "amazon":
                        manifestStore = UnifiedServices.Store.AMAZON;
                        break;
                    case "bazaar":
                        manifestStore = UnifiedServices.Store.BAZAAR;
                        break;
                    case "cando":
                        manifestStore = UnifiedServices.Store.CANDO;
                        break;
                    case "samsung":
                        manifestStore = UnifiedServices.Store.SAMSUNG;
                        break;
                }
            }
        } catch (PackageManager.NameNotFoundException | NullPointerException e) {
            manifestStore = UnifiedServices.Store.NONE;
        }

        if (!SoundManager.isContextInitialized())
            SoundManager.initContext(this);

        setContentView(R.layout.game_layout);
        stage = new WeakReference<>((StageImpl) findViewById(R.id.gameStage));
        ShaderManager.register(TextureManager.getShaderProgram());
        BlurFilter.init();
        init(stage.get());

        UnifiedServices unified = UnifiedServices.getInstance();

        if (unified == null)
            unified = UnifiedServices.getInstance(0);

        if (unified.hasServices(UnifiedServices.SERVICE_ADS))
            unified.getAdManager().setAdLayout((AdLayout) stage.get().getParent());

        unified.onCreate(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                audio.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                        AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                return true;
            default:
                return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, @NonNull KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK)
            if (stage != null && stage.get() != null)
                if (stage.get().onBackButton())
                    return true;
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus)
            hasOutFocused = true;
        else if (hasOutFocused) {
            SoundManager.resumeMusic();
            hasOutFocused = false;
        }
    }

    /**
     * Exits the game or application.
     */
    @SuppressWarnings("UnusedDeclaration")
    public final void exit() {
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stage.get().onPause();
        SoundManager.pauseMusic();
        UnifiedServices.getInstance().onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!hasOutFocused)
            SoundManager.resumeMusic();

        stage.get().onResume();
        UnifiedServices.getInstance().onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UnifiedServices.getInstance().onDestroy(this);
        TextureManager.unloadAll();
        SoundManager.unloadAll();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UnifiedServices.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStop() {
        super.onStop();
        UnifiedServices.getInstance().onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        UnifiedServices.getInstance().onStart();
    }

    /**
     * Initializes the game. Subclasses should use this method to load textures and other resources
     * into the framework.
     *
     * @param stage The stage for the game
     */
    protected abstract void init(Stage stage);
}
