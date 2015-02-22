package com.annahid.libs.artenus.security;

public interface LoginManager {
	public boolean isLoginRequired(int services);

	public boolean isLoggedIn(int services);

	public void setLoginStatusListener(LoginStatusListener listener);

	public void launchLogin(int services);

	public void logout(int services);
}
