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
