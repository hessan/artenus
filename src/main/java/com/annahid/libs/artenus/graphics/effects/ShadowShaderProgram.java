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

package com.annahid.libs.artenus.graphics.effects;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.ShaderManager;

/**
 * Used by the touch map to render button hot (clickable) regions.
 *
 * @author Hessan Feghhi
 */
class ShadowShaderProgram extends TextureShaderProgram {
    /**
     * Holds the vertex shader.
     */
    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 aTexCoord;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  vTexCoord = aTexCoord;" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            '}';

    /**
     * Holds fragment shader code that converts the mapped texture to a mono-chrome image.
     */
    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "uniform sampler2D uTex;" +
            "uniform float uAlpha;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  vec4 temp = uAlpha * texture2D( uTex, vTexCoord );" +
            "  gl_FragColor = vec4(0, 0, 0, temp.a);" +
            '}';

    private static ShadowShaderProgram instance;

    /**
     * Registers this shader program with the shader manager.
     */
    static {
        ShadowShaderProgram.instance = new ShadowShaderProgram();
        ShaderManager.register(ShadowShaderProgram.instance);
    }

    /**
     * The handle to the shadow alpha variable in the OpenGL ES fragment shader.
     */
    private int mShadowAlpha;

    static ShadowShaderProgram getInstance() {
        return instance;
    }

    @Override
    public void compile() {
        super.compile(VERTEX_SHADER_CODE, FRAGMENT_SHADER_CODE);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "uTex");
        mShadowAlpha = GLES20.glGetUniformLocation(mProgram, "uAlpha");
    }

    /**
     * Does nothing. This shader does not support color filtering.
     *
     * @param r Red component (not used)
     * @param g Green component (not used)
     * @param b Blue component (not used)
     * @param a Alpha component
     */
    @Override
    public void feed(float r, float g, float b, float a) {
        // This shader program does not support color filtering.
    }

    /**
     * Feeds the transparency value of the shadow to the shader program.
     *
     * @param shadowAlpha Shadow alpha transparency value
     */
    public void feed(float shadowAlpha) {
        GLES20.glUniform1f(mShadowAlpha, shadowAlpha);
    }
}
