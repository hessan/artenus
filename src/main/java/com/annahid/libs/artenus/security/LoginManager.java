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

import com.annahid.libs.artenus.unified.UnifiedServices;

/**
 * Interface for unified services that support or require user login. Most of the methods in this
 * interface work on a group of services. However, this is rarely used. {@link UnifiedServices}
 * implementations often require a single login for all their services. As a result, login status
 * changes for all services together.
 *
 * @see com.annahid.libs.artenus.unified.UnifiedServices
 */
public interface LoginManager {
    /**
     * Checks whether specified unified services require user authentication.
     *
     * @param services Bit-masked service flags
     *
     * @return    {@code true} if all services require user authentication, {@code false} otherwise
     */
    boolean isLoginRequired(int services);

    /**
     * Checks whether specified unified services are authenticated.
     *
     * @param services Bit-masked service flags
     *
     * @return    {@code true} if all services are authenticated, {@code false} otherwise
     */
    boolean isLoggedIn(int services);

    /**
     * Assigns a new login listener to capture login events for this {@code LoginManager}.
     *
     * @param listener Login listener, or {@code null} to remove the login listener
     */
    void setLoginStatusListener(LoginStatusListener listener);

    /**
     * Launches the login user interface, which asks the user for credentials.
     *
     * @param services Services to validate by this login session (this might not be used by
     *                 the specific implementation)
     */
    void launchLogin(int services);

    /**
     * Exists the currently authenticated session for specified services.
     *
     * @param services Services for which to end login session (this might not be used by
     *                 the specific implementation)
     */
    void logout(int services);
}
