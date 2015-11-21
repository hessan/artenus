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

package com.annahid.libs.artenus.graphics.rendering;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Life-cycle manager for shader programs. All shader programs should be registered with this class
 * in order to be properly handled by the framework. Default shader programs that come with the
 * framework are automatically registered with this class.
 */
public class ShaderManager {
    /**
     * Compiled shader programs associated for shader classes.
     */
    private static Collection<ShaderProgram> programs = new ArrayList<>(16);

    /**
     * A value indicating whether there is a rendering context and compiling shader programs is
     * possible.
     */
    private static boolean safeToCompile = false;

    /**
     * Registers a shader program with the shader manager.
     *
     * @param program The shader program
     */
    public static void register(ShaderProgram program) {
        programs.add(program);

        if (safeToCompile) {
            program.compile();
        }
    }

    /**
     * Called by the renderer when the rendering context is ready and shader programs can be
     * compiled. Manual use of this method is not recommended.
     */
    public static void loadAll() {
        if (safeToCompile) {
            for(ShaderProgram program : programs) {
                program.destroy();
            }
        }
        safeToCompile = true;
        for(ShaderProgram program : programs) {
            program.compile();
        }
    }
}
