package com.annahid.libs.artenus.unified.impl;

import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.AdManager;
import com.annahid.libs.artenus.unified.GameServices;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.security.LoginManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.plus.Plus;

public final class BazaarUnifiedServices extends UnifiedServices {
	private static final int SERVICE_MASK = SERVICE_BILLING | SERVICE_GAMES | SERVICE_ADS;

	private BazaarInventoryManager inventoryManager = null;
	private GoogleLoginManager loginManager = null;
	private AdadAdManager adManager = null;
	GoogleGameServices gameServices = null;

	@Override
	protected int init(int inputServices) {
		loginManager = new GoogleLoginManager();

		if (hasServices(SERVICE_BILLING))
			inventoryManager = new BazaarInventoryManager();

		if(hasServices(SERVICE_GAMES))
			gameServices = new GoogleGameServices();

		if(hasServices(SERVICE_ADS))
			adManager = new AdadAdManager();

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

		if (hasServices(SERVICE_ADS))
			adManager.onCreate();

		if (anyServices) {
			builder.addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN);
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
	public void onDestroy(Context context) {
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
		return Store.BAZAAR;
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
