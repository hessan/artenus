package com.annahid.libs.artenus.internal.unified;

import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.AdManager;
import com.annahid.libs.artenus.unified.GameServices;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.security.LoginManager;

public class AmazonUnifiedServices extends UnifiedServices {
	private static final int SERVICE_MASK = SERVICE_BILLING | SERVICE_GAMES | SERVICE_ADS;

	private AmazonInventoryManager inventoryManager = null;
	private AmazonGameServices gameServices = null;
	private AmazonLoginManager loginManager = null;
	private AmazonAdManager adManager = null;

	@Override
	public int init(int inputServices) {
		loginManager = new AmazonLoginManager();

		if (hasServices(SERVICE_BILLING))
			inventoryManager = new AmazonInventoryManager();

		if (hasServices(SERVICE_GAMES))
			gameServices = new AmazonGameServices(loginManager);

		if (hasServices(SERVICE_ADS))
			adManager = new AmazonAdManager();

		return inputServices & SERVICE_MASK;
	}

	@Override
	public void onCreate(Context context) {
		if (inventoryManager != null)
			inventoryManager.onCreate(context);
		if (adManager != null)
			adManager.onCreate();
	}

	public void onDestroy(Context context) {
		if (inventoryManager != null)
			inventoryManager.onDestroy(context);
		if (adManager != null)
			adManager.destroyAd();
	}

	@Override
	public void onPause() {
		if (gameServices != null)
			gameServices.onPause();
	}

	@Override
	public void onResume() {
		if (inventoryManager != null)
			inventoryManager.onResume();

		if (gameServices != null)
			gameServices.onResume();
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		if(inventoryManager != null)
			inventoryManager.onActivityResult(requestCode, resultCode, data);

		return false;
	}

	@Override
	public Store getStore() {
		return Store.AMAZON;
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
