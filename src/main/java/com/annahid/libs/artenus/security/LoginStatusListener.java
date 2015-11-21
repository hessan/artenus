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
	void onStatusChanged(boolean loggedIn, int serviceMask);
}
