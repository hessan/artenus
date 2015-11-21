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
