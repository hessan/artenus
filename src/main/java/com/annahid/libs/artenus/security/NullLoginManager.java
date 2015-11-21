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

package com.annahid.libs.artenus.security;

/**
 * An implementation of {@link com.annahid.libs.artenus.security.LoginManager} provided by unified
 * services providers that do not have a user authentication component.
 */
public class NullLoginManager implements LoginManager {
	@Override
	public boolean isLoginRequired(int services) {
		return false;
	}

	@Override
	public boolean isLoggedIn(int services) {
		return false;
	}

	@Override
	public void setLoginStatusListener(LoginStatusListener listener) {
	}

	@Override
	public void launchLogin(int services) {
	}

	@Override
	public void logout(int services) {
	}
}
