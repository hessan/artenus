package com.annahid.libs.artenus.security;

/**
 * Interface for classes that listen for service login events.
 * 
 * @author Hessan Feghhi
 *
 */
public interface LoginStatusListener {
	/**
	 * This method is called whenever the login state changes.
	 *
	 * @param loggedIn    {@code true} if the service is logged in, {@code false} otherwise
	 * @param serviceMask Bits-masked services that the login status is valid for
	 */
	public void onStatusChanged(boolean loggedIn, int serviceMask);
}
