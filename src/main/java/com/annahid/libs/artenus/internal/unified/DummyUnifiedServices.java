package com.annahid.libs.artenus.internal.unified;

import android.content.Context;

import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.unified.AdManager;
import com.annahid.libs.artenus.unified.GameServices;
import com.annahid.libs.artenus.unified.InventoryManager;
import com.annahid.libs.artenus.security.LoginManager;
import com.annahid.libs.artenus.security.NullLoginManager;

public final class DummyUnifiedServices extends UnifiedServices {
	private LoginManager loginManager = null;

	@Override
	protected int init(int inputServices) {
		loginManager = new NullLoginManager();
		return 0;
	}

	@Override
	public void onCreate(Context context) { }

	@Override
	public void onDestroy(Context context) { }

	@Override
	public Store getStore() {
		return Store.SAMSUNG;
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
		return null;
	}
}
