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

import com.annahid.libs.artenus.unified.Stores;
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
    public void onCreate(Context context) {
    }

    @Override
    public void onDestroy(Context context) {
    }

    @Override
    public Stores getStore() {
        return Stores.SAMSUNG;
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
