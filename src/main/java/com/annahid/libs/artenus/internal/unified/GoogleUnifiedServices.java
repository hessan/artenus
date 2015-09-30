package com.annahid.libs.artenus.internal.unified;

import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.AdManager;
import com.annahid.libs.artenus.unified.GameServices;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.security.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;

public final class GoogleUnifiedServices extends UnifiedServices {
	private static final int SERVICE_MASK = SERVICE_BILLING | SERVICE_GAMES | SERVICE_ADS;

	GoogleInventoryManager inventoryManager = null;
	GoogleGameServices gameServices = null;
	GoogleLoginManager loginManager = null;
	GoogleAdManager adManager = null;

	@Override
	public int init(int inputServices) {
		loginManager = new GoogleLoginManager();

		if (hasServices(SERVICE_BILLING))
			inventoryManager = new GoogleInventoryManager();

		if (hasServices(SERVICE_GAMES)) {
			gameServices = new GoogleGameServices();
		}

		if (hasServices(SERVICE_ADS)) {
			adManager = new GoogleAdManager();
		}

		return inputServices & SERVICE_MASK;
	}

	@Override
	public void onCreate(Context context) {
		if (inventoryManager != null)
			inventoryManager.onCreate(context);

		GoogleApiClient.Builder builder = new GoogleApiClient.Builder(context)
				.addConnectionCallbacks(loginManager)
				.addOnConnectionFailedListener(loginManager);

		boolean anyServices = false;

		if (hasServices(SERVICE_GAMES)) {
			builder.addApi(Games.API).addScope(Games.SCOPE_GAMES);
			anyServices = true;
		}

		if (hasServices(SERVICE_ADS)) {
			adManager.onCreate();
		}

		if (anyServices) {
			GoogleApiClient apiClient = builder.build();

			if (hasServices(SERVICE_GAMES)) {
				loginManager.setGoogleApiClient(apiClient);
				gameServices.setGoogleApiClient(apiClient);
			}

			loginManager.setGoogleApiClient(apiClient);
		}

		loginManager.onCreate(context);
	}

	@Override
	public final void onDestroy(Context context) {
		if (inventoryManager != null)
			inventoryManager.onDestroy(context);
	}

	@Override
	public void onStart() {
		loginManager.onStart();
	}

	@Override
	public void onStop() {
		loginManager.onStop();
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return loginManager.onActivityResult(requestCode, resultCode, data) ||
				(inventoryManager != null &&
						inventoryManager.onActivityResult(requestCode, resultCode, data));
	}

	@Override
	public Store getStore() {
		return Store.GOOGLE;
	}

	@Override
	public AdManager getAdManager() {
		return adManager;
	}

	@Override
	public GameServices getGameServices() {
		return gameServices;
	}

	@Override
	public LoginManager getLoginManager() {
		return loginManager;
	}

	@Override
	public InventoryManager getInventoryManager() {
		return inventoryManager;
	}
}
