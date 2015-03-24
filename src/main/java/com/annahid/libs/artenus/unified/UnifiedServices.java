package com.annahid.libs.artenus.unified;

import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.impl.AmazonUnifiedServices;
import com.annahid.libs.artenus.unified.impl.BazaarUnifiedServices;
import com.annahid.libs.artenus.unified.impl.CandoUnifiedServices;
import com.annahid.libs.artenus.unified.impl.DummyUnifiedServices;
import com.annahid.libs.artenus.unified.impl.GoogleUnifiedServices;
import com.annahid.libs.artenus.security.LoginManager;

/**
 * Provides all store-specific game functionality in a unified way. In-app billing, ad
 * management, and game services are handled by this class.
 */
@SuppressWarnings("UnusedDeclaration")
public abstract class UnifiedServices {
	/**
	 * The Store defines the app store the unified services operate in
	 */
	public enum Store {
		/**
		 * Undefined app-store
		 */
		NONE,
		/**
		 * Google Play
		 */
		GOOGLE,
		/**
		 * Amazon Appstore
		 */
		AMAZON,

		/**
		 * Cafe Bazaar (local Iranian app-store)
		 */
		BAZAAR,

		/**
		 * Samsung Apps
		 */
		SAMSUNG,

		/**
		 * Cando (local Iranian app-store)
		 */
		CANDO
	}

	public static final int SERVICE_BILLING = 1;
	public static final int SERVICE_GAMES = 2;
	public static final int SERVICE_ADS = 32;

	private static UnifiedServices instance = null;

	public static UnifiedServices getInstance(int services) {
		if (instance == null) {
			switch (Artenus.getManifestAppStore()) {
				case GOOGLE:
					instance = new GoogleUnifiedServices();
					break;
				case AMAZON:
					instance = new AmazonUnifiedServices();
					break;
				case BAZAAR:
					instance = new BazaarUnifiedServices();
					break;
				case CANDO:
					instance = new CandoUnifiedServices();
					break;
				default:
					instance = new DummyUnifiedServices();
					break;
			}

			instance.services = services;
			instance.services = instance.init(services);
		}

		return instance;
	}

	public static UnifiedServices getInstance() {
		return instance;
	}

	private int services;

	protected UnifiedServices() {
	}

	public abstract void onCreate(Context context);

	public abstract void onDestroy(Context context);

	public void onPause() {
	}

	public void onResume() {
	}

	public void onStart() {
	}

	public void onStop() {
	}

	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return false;
	}

	public final boolean hasServices(int servicesMask) {
		return (services & servicesMask) != 0;
	}

	public final void setInventorySKUs(String[] inventorySKUs) {
		getInventoryManager().setSKUs(inventorySKUs);
	}

	public abstract Store getStore();

	public abstract AdManager getAdManager();

	public abstract GameServices getGameServices();

	public abstract LoginManager getLoginManager();

	public abstract InventoryManager getInventoryManager();

	protected abstract int init(int inputServices);
}
