package com.annahid.libs.artenus.unified.impl;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.unified.UnifiedServices;
import com.annahid.libs.artenus.security.LoginManager;
import com.annahid.libs.artenus.security.LoginStatusListener;

final class AmazonLoginManager implements LoginManager {
	private LoginStatusListener listener = null;
	private boolean gamesLoggedIn = false;

	public void setGamesLoggedIn(final boolean loggedIn) {
		gamesLoggedIn = loggedIn;

		Artenus.getInstance().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (listener != null)
					listener.onStatusChanged(loggedIn, UnifiedServices.SERVICE_GAMES);
			}
		});
	}

	@Override
	public boolean isLoginRequired(int services) {
		return false;
	}

	@Override
	public boolean isLoggedIn(int services) {
		return (services & UnifiedServices.SERVICE_GAMES) != 0 && gamesLoggedIn;
	}

	@Override
	public void setLoginStatusListener(LoginStatusListener listener) {
		this.listener = listener;
	}

	@Override
	public void launchLogin(int services) {
	}

	@Override
	public void logout(int services) {
	}
}
