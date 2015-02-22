package com.annahid.libs.artenus.graphics;

import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is the central point to manipulate {@code Texture} objects in the framework.
 * It provides methods to create, load and unload textures and it will make sure that
 * textures that are used are valid. This class handles the "loading" of graphical assets
 * when your game starts up, or before every scene with local assets. But this procedure
 * is effectively hidden from the developer and you do not need to worry about creating
 * your own loading screens and texture handling. 
 * @author Hessan Feghhi
 *
 */
public final class TextureManager {
	/**
	 * Indicates that the texture manager has all the textures but they are not loaded.
	 */
	public static final int STATE_FRESH = 0;

	/**
	 * Indicates that the texture manager is loading the textures.
	 */
	public static final int STATE_LOADING = 1;

	/**
	 * Indicates that the texture manager is unloading some or all of the textures. This
	 * can also be a part of the loading process, where it first unloads the previous
	 * textures which are no longer required. It just indicates the course of action that
	 * is currently being taken.
	 */
	public static final int STATE_UNLOADING = 2;

	/**
	 * Indicates that the texture manager has finished loading the textures. In this
	 * state the textures are functional.
	 */
	public static final int STATE_LOADED = 3;

	private static SparseArray<Texture> texMap = new SparseArray<>();
	private static List<Texture> texList = new ArrayList<>();
	private static int state;
	private static int[] localTex = null;
	private static Texture loadingTexture;
	private static int loadedCount = 0; // For displaying purposes only

	private static final Object texLock = new Object();

	static int loadingTexW, loadingTexH;

	/**
	 * This class is a descriptor for a font. Fonts need to be introduced to the texture manager
	 * when loading the game, so it can create proper font textures based on the information provided.
	 * Note that this is not a real font texture and is just a description of one.
	 *
	 * @author Hessan Feghhi
	 */
	public static final class FontInfo {
		int resourceId, characterHeight;
		int hSpacing, vSpacing;
		char startChar;
		int[] charOffsets;

		/**
		 * Constructs a new {@code FontInfo} based on the information provided.
		 *
		 * @param resourceId      The resource identifier for the image containing character graphics.
		 * @param characterHeight The height of each character. The image will be divided vertically
		 *                        into lines of this height.
		 * @param startChar       The starting character represented in this font. This will represent the
		 *                        first block taken out of the image.
		 * @param charOffsets     The x positions of the characters. It starts with the first character and
		 *                        for each character you should specify two points for the left and the right. Each time the
		 *                        given x coordinate falls below the previous one, it is taken as a new line signal and the
		 *                        divider is moved a line forward.
		 * @param hs              Horizontal spacing.
		 * @param vs              Vertical spacing.
		 */
		public FontInfo(int resourceId, int characterHeight, char startChar, int[] charOffsets, int hs, int vs) {
			this.resourceId = resourceId;
			this.startChar = startChar;
			this.charOffsets = charOffsets;
			this.characterHeight = characterHeight;
			hSpacing = hs;
			vSpacing = vs;
		}
	}

	/**
	 * Gets the texture displayed in the loading screen. The framework has a default loading texture,
	 * but it can also be modified for each game.
	 *
	 * @return The loading texture.
	 */
	public static Texture getLoadingTexture() {
		return loadingTexture;
	}

	/**
	 * Gets the width of the texture displayed in the loading screen.
	 *
	 * @return The width of the loading texture.
	 */
	public static int getLoadingTextureWidth() {
		return loadingTexW;
	}

	/**
	 * Gets the height of the texture displayed in the loading screen.
	 *
	 * @return The height of the loading texture.
	 */
	public static int getLoadingTextureHeight() {
		return loadingTexH;
	}

	/**
	 * Sets the texture displayed in the loading screen. It is recommended that you don't call this
	 * method directly and use {@code Stage} attributes to set the loading texture.
	 *
	 * @param resId The resource identifier for the loading texture.
	 * @see com.annahid.libs.artenus.ui.Stage
	 */
	public static void setLoadingTexture(int resId) {
		loadingTexture = new Texture(resId);
	}

	/**
	 * Sets up the texture set for the framework. This method is one of the building blocks of the
	 * "loading" procedure in the game. It should be called from the {@code onLoadStage(Stage)}
	 * method of {@code StageManager}. All textures used widely throughout the game should be
	 * introduced to this method. Textures loaded using this method will always be available. Be
	 * careful not to load a lot of textures using this method and consider memory constraints on
	 * your target devices. If you have textures that are only used in some scenes, consider using
	 * local loading of those textures instead of globally loading them using this method.
	 *
	 * @param textureSet The set of resource identifiers for the textures to load.
	 * @param fontSet    The set of font descriptors for the fonts used. Fonts cannot be loaded
	 *                   locally and you should globally load all required fonts using this method.
	 * @see com.annahid.libs.artenus.ui.StageManager
	 */
	public static void setup(int[] textureSet, FontInfo[] fontSet) {
		state = STATE_LOADING;

		final List<Texture> tempList = new ArrayList<>();
		final SparseArray<Texture> tempMap = new SparseArray<>();

		Texture tex;

		for (int textureId : textureSet) {
			tex = new Texture(textureId);
			tempMap.put(textureId, tex);
			tempList.add(tex);
		}

		for (FontInfo font : fontSet) {
			tex = new Font(font.resourceId, font.characterHeight, font.startChar, font.charOffsets);
			((Font) tex).setLetterSpacing(font.hSpacing, font.vSpacing);
			tempMap.put(font.resourceId, tex);
			tempList.add(tex);
		}

		state = STATE_UNLOADING;

		final List<Texture> bkList = texList;
		final SparseArray<Texture> bkMap = texMap;

		texList = tempList;
		texMap = tempMap;

		for (int i = 0; i < bkList.size(); i++)
			bkList.get(i).destroy();

		bkList.clear();
		bkMap.clear();

		state = STATE_FRESH;
	}

	/**
	 * Loads the set of local texture for a {@code Scene}. This method should be called within the
	 * {@link com.annahid.libs.artenus.ui.Scene#onLocalLoad()} method of the {@code Scene} class.
	 *
	 * @param textureSet The set of resource identifier for the textures to load.
	 * @see com.annahid.libs.artenus.ui.Scene
	 */
	public static void loadLocal(int... textureSet) {
		Texture tex;

		for (int textureId : textureSet) {
			tex = new Texture(textureId);
			texMap.put(textureId, tex);
			texList.add(tex);
			state = STATE_LOADING;
		}

		localTex = textureSet;
		state = STATE_FRESH;
	}

	/**
	 * Unloads local textures previously loaded using {@link #loadLocal(int[])}. This method is
	 * called internally and you do not need to manually handle the unloading of textures.
	 */
	public static void unloadLocal() {
		if (localTex != null) {
			state = STATE_UNLOADING;

			synchronized (texLock) {
				for (int textureId : localTex) {
					final Texture tex = texMap.get(textureId);

					if (tex != null) {
						tex.destroy();
						texMap.remove(textureId);
						texList.remove(tex);
					}
				}
			}
		}

		localTex = null;
		state = STATE_FRESH;
	}

	/**
	 * Gets the {@code Texture} associated with the given resource identifier.
	 *
	 * @param resourceId The resource identifier.
	 * @return The {@code Texture} corresponding to the resource identifier or {@code null} if
	 * the texture has not been set up in the texture manager.
	 * @see com.annahid.libs.artenus.graphics.Texture
	 */
	public static Texture getTexture(int resourceId) {
		return texMap.get(resourceId);
	}

	/**
	 * Gets the {@code Font} associated with the given resource identifier. If the given resource
	 * identifier does not represent a font this method might throw an exception.
	 *
	 * @param resourceId The resource identifier.
	 * @return The {@code Font} corresponding to the resource identifier or {@code null} if the
	 * font has not been set up in the texture manager.
	 * @see com.annahid.libs.artenus.graphics.Font
	 */
	public static Font getFont(int resourceId) {
		return (Font) texMap.get(resourceId);
	}

	/**
	 * Gets the current state of the texture manager.
	 *
	 * @return One of the following values:
	 * {@link #STATE_FRESH},
	 * {@link #STATE_LOADING},
	 * {@link #STATE_UNLOADING}, or
	 * {@link #STATE_LOADED}.
	 */
	public static int getCurrentState() {
		return state;
	}

	/**
	 * Loads all the textures. This is part of the "loading" process and normally the loading
	 * screen is displayed before this method starts. Manual use of this method is not recommended.
	 */
	public static void loadTextures() {
		if (state != STATE_FRESH)
			return;

		state = STATE_LOADING;

		final Object[] list = texList.toArray();
		int ret = STATE_LOADED;
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

				if (!tex.isLoaded()) {
					ret = STATE_FRESH;
				} else count++;
			}
		}

		loadedCount = ret == STATE_LOADED ? 0 : count;
		state = ret;
	}

	public static int getLoadedCount() {
		return loadedCount;
	}

	public static int getTextureCount() {
		return texList.size();
	}

	/**
	 * Unloads all textures and clears the list. After calling this method,
	 * {@link #setup(int[], com.annahid.libs.artenus.graphics.TextureManager.FontInfo[])} needs to be called again to load a new set of textures.
	 */
	public static void unloadAll() {
		synchronized (texLock) {
			state = STATE_UNLOADING;

			for (int i = 0; i < texList.size(); i++)
				texList.get(i).destroy();

			texList.clear();
			texMap.clear();
			state = STATE_FRESH;
		}
	}

	/**
	 * Unloads the textures, but keeps the bookkeeping information. This method is called
	 * to temporarily free memory allocated to texture when the application is paused. This
	 * method is called internally and manual use is not recommended.
	 */
	public static void unloadTextures() {
		synchronized (texLock) {
			state = STATE_UNLOADING;

			for (int i = 0; i < texList.size(); i++)
				texList.get(i).destroy();

			state = STATE_FRESH;
		}
	}
}
