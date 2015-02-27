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
