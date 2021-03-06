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

package com.annahid.libs.artenus.graphics;

import android.content.res.Resources;
import android.util.SparseArray;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;
import com.annahid.libs.artenus.data.ConcurrentCollection;

import java.util.Collection;

/**
 * Serves as the central point to manipulate {@code Texture} objects in the framework. It provides
 * methods to create, load and unload textures and it will make sure that textures that are used are
 * valid. This class handles the "loading" of graphical assets when your game starts up, or before
 * every scene with local assets. But this procedure is effectively hidden from the developer and
 * you do not need to worry about creating your own loading screens and texture handling.
 *
 * @author Hessan Feghhi
 */
public final class TextureManager {
    /**
     * Used for mutual exclusion.
     */
    private static final Object texLock = new Object();

    /**
     * Holds the shader program used to draw textured entities in the framework.
     */
    static TextureShaderProgram program = new TextureShaderProgram();

    /**
     * Holds the width of the loading texture image.
     */
    static int loadingTexW;

    /**
     * Holds the height of the loading texture image.
     */
    static int loadingTexH;

    /**
     * Maps resource identifiers to texture objects.
     */
    private static SparseArray<Texture> texMap = new SparseArray<>();

    /**
     * Contains textures currently managed by the texture manager.
     */
    private static Collection<Texture> texList = new ConcurrentCollection<>();

    /**
     * Holds loading status.
     */
    private static States state = States.FRESH;

    /**
     * Contains local texture identifiers for the current scene.
     */
    private static int[] localTex = null;

    /**
     * Holds the loading texture.
     */
    private static Texture loadingTexture;

    private static int loadedCount = 0; // For displaying purposes only

    /**
     * Holds the scaling factor used for adapting texture dimensions to screen dimensions.
     */
    private static float texScale = 1.0f;

    /**
     * Gets the shader program used to draw textured entities in the framework.
     *
     * @return The shader program
     */
    public static ShaderProgram getShaderProgram() {
        return program;
    }

    /**
     * Gets the texture displayed in the loading screen. The framework has a default loading texture,
     * but it can also be modified for each game.
     *
     * @return The loading texture
     */
    public static Texture getLoadingTexture() {
        return loadingTexture;
    }

    /**
     * Sets the texture displayed in the loading screen. It is recommended that you don't call this
     * method directly and use {@code Stage} attributes to set the loading texture.
     *
     * @param resId The resource identifier for the loading texture
     *
     * @see com.annahid.libs.artenus.core.Stage
     */
    public static void setLoadingTexture(int resId) {
        loadingTexture = new Texture(resId);
    }

    /**
     * Gets the width of the texture displayed in the loading screen.
     *
     * @return The width of the loading texture
     */
    public static int getLoadingTextureWidth() {
        return loadingTexW;
    }

    /**
     * Gets the height of the texture displayed in the loading screen.
     *
     * @return The height of the loading texture
     */
    public static int getLoadingTextureHeight() {
        return loadingTexH;
    }

    /**
     * Declares textures for the current context. This method is one of the building blocks of the
     * "loading" procedure in the game. It should be called from the {@code onLoadStage(Stage)}
     * method of {@code StageManager}. All textures used widely throughout the game should be
     * introduced using this method. Textures loaded using this method will always be available. Be
     * careful not to load a lot of textures using this method and consider memory constraints on
     * your target devices. If you have textures that are only used in some scenes, consider using
     * local loading of those textures instead of globally loading them using this method.
     *
     * @param textureSet The set of resource identifiers for the textures and fonts to load
     *
     * @see com.annahid.libs.artenus.core.StageManager
     * @see #addLocal(int...)
     */
    public static void add(int... textureSet) {
        state = States.LOADING;

        final SparseArray<Texture> tempMap = new SparseArray<>();

        for (Texture tex : texList) {
            tempMap.put(tex.resId, tex);
        }

        Resources res = Artenus.getInstance().getResources();

        for (int textureId : textureSet) {
            Texture tex;

            if (res.getResourceTypeName(textureId).equalsIgnoreCase("raw")) {

                try {
                    tex = new Font(textureId);
                } catch (Exception ex) {
                    tex = new Texture(textureId);
                }
            } else tex = new Texture(textureId);

            tempMap.put(textureId, tex);
            texList.add(tex);
        }

        final SparseArray<Texture> bkMap = texMap;

        texMap = tempMap;
        bkMap.clear();

        state = States.FRESH;
    }

    /**
     * Declares local textures for a scene. This method should be called within the
     * {@link com.annahid.libs.artenus.core.Scene#onLocalLoad()} method of the {@code Scene} class.
     *
     * @param textureSet The set of resource identifier for the textures to load
     *
     * @see com.annahid.libs.artenus.core.Scene
     */
    public static void addLocal(int... textureSet) {
        Texture tex;

        state = States.LOADING;

        for (int textureId : textureSet) {
            tex = new Texture(textureId);
            texMap.put(textureId, tex);
            texList.add(tex);
        }

        localTex = textureSet;
        state = States.FRESH;
    }

    /**
     * Unloads local textures previously loaded using {@link #addLocal(int...)}. This method is
     * called internally and you do not need to manually handle the unloading of textures.
     */
    public static void unloadLocal() {
        if (localTex != null) {
            state = States.UNLOADING;

            for (int textureId : localTex) {
                final Texture tex = texMap.get(textureId);
                tex.destroy();
                texMap.remove(textureId);
                texList.remove(tex);
            }
        }

        localTex = null;
        state = States.FRESH;
    }

    /**
     * Gets the {@code Texture} associated with the given resource identifier.
     *
     * @param resourceId The resource identifier
     *
     * @return The {@code Texture} corresponding to the resource identifier or {@code null} if
     * the texture has not been set up in the texture manager
     *
     * @see com.annahid.libs.artenus.graphics.Texture
     */
    public static Texture getTexture(int resourceId) {
        return texMap.get(resourceId);
    }

    /**
     * Gets the {@code Font} associated with the given resource identifier. If the given resource
     * identifier does not represent a font this method might throw an exception.
     *
     * @param resourceId The resource identifier
     *
     * @return The {@code Font} corresponding to the resource identifier or {@code null} if the
     * font has not been set up in the texture manager
     *
     * @see com.annahid.libs.artenus.graphics.Font
     */
    public static Font getFont(int resourceId) {
        return (Font) texMap.get(resourceId);
    }

    /**
     * Gets the current state of the texture manager.
     *
     * @return The state
     */
    public static States getCurrentState() {
        return state;
    }

    /**
     * Loads all the textures. This is part of the "loading" process and normally the loading
     * screen is displayed before this method starts. Manual use of this method is not recommended.
     */
    public static void loadTextures() {
        if (state != States.FRESH)
            return;

        state = States.LOADING;

        final Object[] list = texList.toArray();
        States ret = States.LOADED;
        int count = 0;

        synchronized (texLock) {
            int parallelCount = 0;

            for (Object obj : list) {
                final Texture tex = (Texture) obj;

                if (!tex.isLoaded()) {
                    if (!tex.loading) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                tex.loadImage();
                            }
                        }).start();
                    } else tex.loadEGL();

                    if (++parallelCount == 3)
                        break;
                }
            }

            for (Object obj : list) {
                final Texture tex = (Texture) obj;

                if (tex != null && !tex.isLoaded()) {
                    ret = States.FRESH;
                } else count++;
            }
        }

        loadedCount = ret == States.LOADED ? 0 : count;
        state = ret;
    }

    /**
     * Gets the number of textures that are loaded and ready to be displayed.
     *
     * @return Number of textures
     */
    public static int getLoadedCount() {
        return loadedCount;
    }

    /**
     * Gets the number of textures currently managed by the texture manager.
     *
     * @return Number of textures
     */
    public static int getTextureCount() {
        return texList.size();
    }

    /**
     * Unloads all textures and clears the list. After calling this method,
     * {@link #add(int...)} needs to be called again to add a new set of textures.
     */
    public static void unloadAll() {
        synchronized (texLock) {
            state = States.UNLOADING;

            for (Texture tex : texList)
                tex.destroy();

            texList.clear();
            texMap.clear();
            state = States.FRESH;
        }
    }

    /**
     * Unloads the textures, but keeps the bookkeeping information. This method is called
     * to temporarily free memory allocated to texture when the application is paused. This
     * method is called internally and manual use is not recommended.
     */
    public static void unloadTextures() {
        synchronized (texLock) {
            state = States.UNLOADING;

            for (Texture tex : texList)
                tex.destroy();

            state = States.FRESH;
        }
    }

    /**
     * Retrieves the texture scaling factor. Normally the stage is assumed to have the smallest
     * dimension of 600. The smallest dimension means the the width in portrait mode and height in
     * landscape mode. If the real dimensions is larger that this, textures are scaled to maintain
     * this perception from the developer's point of view. This method yields the scaling factor
     * used.
     *
     * @return The texture scaling factor.
     */
    public static float getTextureScalingFactor() {
        return texScale;
    }

    /**
     * Called by the stage to set the scaling factor. You should not call this method manually.
     *
     * @param factor Texture scaling factor.
     *
     * @see TextureManager#getTextureScalingFactor()
     */
    public static void setTextureScalingFactor(float factor) {
        texScale = factor;
    }

    /**
     * States a texture manager can be in.
     */
    public enum States {
        /**
         * State indicating that the texture manager has all the textures but they are not loaded.
         */
        FRESH,

        /**
         * State indicating that the texture manager is loading the textures.
         */
        LOADING,

        /**
         * State indicating that the texture manager is unloading some or all of the textures. This
         * can also be a part of the loading process, where it first unloads the previous textures
         * which are no longer required. It just indicates the course of action that is currently
         * being taken.
         */
        UNLOADING,

        /**
         * State indicating that the texture manager has finished loading the textures. In this
         * state the textures are functional.
         */
        LOADED
    }
}
