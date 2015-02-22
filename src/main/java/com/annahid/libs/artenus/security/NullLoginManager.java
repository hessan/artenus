package com.annahid.libs.artenus.security;

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
