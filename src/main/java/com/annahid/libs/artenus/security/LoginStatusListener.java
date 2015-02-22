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
	 * @param loggedIn    Whether the service is logged in.
	 * @param serviceMask Bits indicate services that this login is valid for.
	 */
	public void onStatusChanged(boolean loggedIn, int serviceMask);
}
