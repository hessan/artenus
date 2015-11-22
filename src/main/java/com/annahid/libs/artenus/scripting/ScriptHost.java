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

package com.annahid.libs.artenus.scripting;

/**
 * Interface for classes that interpret and execute scripts. If your game supports scripting, you
 * can implement this interface to take advantage of the script console user interface.
 *
 * @author Hessan Feghhi
 * @see ConsoleActivity
 */
public interface ScriptHost {
    /**
     * Executes a script code
     *
     * @param command The command or piece of code
     *
     * @return The result or {@code null} if there is not result
     */
    Object execute(String command);

    /**
     * Called when the script console is closing. It should free all resources allocated to the
     * script host.
     */
    void onExit();
}
