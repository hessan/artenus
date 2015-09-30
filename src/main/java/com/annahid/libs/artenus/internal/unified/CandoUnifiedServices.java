package com.annahid.libs.artenus.internal.unified;

import android.content.Context;
import android.content.Intent;

import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.AdManager;
import com.annahid.libs.artenus.unified.GameServices;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.security.LoginManager;
import com.annahid.libs.artenus.security.NullLoginManager;

public final class CandoUnifiedServices extends UnifiedServices {
	private CandoInventoryManager inventoryManager = null;
	private LoginManager loginManager = null;

	@Override
	protected int init(int inputServices) {
		loginManager = new NullLoginManager();

		if (hasServices(SERVICE_BILLING))
			inventoryManager = new CandoInventoryManager();

		return inputServices & (SERVICE_BILLING);
	}

	@Override
	public void onCreate(Context context) {
		if (inventoryManager != null)
			inventoryManager.onCreate(context);
	}

	@Override
	public void onDestroy(Context context) {
		if (inventoryManager != null)
			inventoryManager.onDestroy(context);
	}

	@Override
	public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
		return
				inventoryManager != null &&
						inventoryManager.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public Store getStore() {
		return Store.CANDO;
	}

	@Override
	public AdManager getAdManager() {
		return null;
	}

	@Override
	public GameServices getGameServices() {
		return null;
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
