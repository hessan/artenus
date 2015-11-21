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

package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureShaderProgram;

import java.nio.FloatBuffer;

/**
 * Represents a shader program used by the blur post-processing filter.
 */
final class BlurShaderProgram extends TextureShaderProgram {
    /**
     * Holds vertex shader code for the blur program.
     */
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 aTexCoord;" +
            "uniform float vx_offset;" +
            "varying vec2 vTexCoord;" +
            "uniform float rt_w;" +
            "uniform float rt_h;" +
            "uniform int pass;" +
            "varying vec2 v_blurTexCoords[6];" +
            "void main() {" +
            "  vTexCoord = aTexCoord;" +
            "  if ( pass == 0 ) {" +
            "    v_blurTexCoords[ 0] = vTexCoord + vec2(0.0, -3.0 / rt_h * vx_offset);" +
            "    v_blurTexCoords[ 1] = vTexCoord + vec2(0.0, -2.0 / rt_h * vx_offset);" +
            "    v_blurTexCoords[ 2] = vTexCoord + vec2(0.0, -1.0 / rt_h * vx_offset);" +
            "    v_blurTexCoords[ 3] = vTexCoord + vec2(0.0,  1.0 / rt_h * vx_offset);" +
            "    v_blurTexCoords[ 4] = vTexCoord + vec2(0.0,  2.0 / rt_h * vx_offset);" +
            "    v_blurTexCoords[ 5] = vTexCoord + vec2(0.0,  3.0 / rt_h * vx_offset);" +
            "  } else {" +
            "    v_blurTexCoords[ 0] = vTexCoord + vec2(-3.0 * vx_offset / rt_w, 0.0);" +
            "    v_blurTexCoords[ 1] = vTexCoord + vec2(-2.0 * vx_offset / rt_w, 0.0);" +
            "    v_blurTexCoords[ 2] = vTexCoord + vec2(-1.0 * vx_offset / rt_w, 0.0);" +
            "    v_blurTexCoords[ 3] = vTexCoord + vec2( 1.0 * vx_offset / rt_w, 0.0);" +
            "    v_blurTexCoords[ 4] = vTexCoord + vec2( 2.0 * vx_offset / rt_w, 0.0);" +
            "    v_blurTexCoords[ 5] = vTexCoord + vec2( 3.0 * vx_offset / rt_w, 0.0);" +
            "  }" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            '}';

    /**
     * Holds fragment shader code for the program.
     */
    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform sampler2D sceneTex;" +
            "varying vec2 vTexCoord;" +
            "varying vec2 v_blurTexCoords[6];" +
            "void main() {" +
            "  gl_FragColor = texture2D(sceneTex, vTexCoord)*0.383103" +
            "  + texture2D(sceneTex, v_blurTexCoords[ 0])*0.00598" +
            "  + texture2D(sceneTex, v_blurTexCoords[ 1])*0.060626" +
            "  + texture2D(sceneTex, v_blurTexCoords[ 2])*0.241843" +
            "  + texture2D(sceneTex, v_blurTexCoords[ 3])*0.241843" +
            "  + texture2D(sceneTex, v_blurTexCoords[ 4])*0.060626" +
            "  + texture2D(sceneTex, v_blurTexCoords[ 5])*0.00598;" +
            '}';

    /**
     * Holds the handle to the render target width variable in the OpenGL ES fragment shader.
     */
    private int mRTWidthHandle;

    /**
     * Holds the handle to the render target height variable in the OpenGL ES fragment shader.
     */
    private int mRTHeightHandle;

    /**
     * Holds the handle to the blur offset variable in the OpenGL ES fragment shader.
     */
    private int mOffsetHandle;

    /**
     * Holds the handle to the pass variable in the OpenGL ES fragment shader.
     */
    private int mPassHandle;

    /**
     * Compiles this shader program and gathers all shader variable handles.
     */
    @Override
    public void compile() {
        super.compile(vertexShaderCode, fragmentShaderCode);
        mRTWidthHandle = GLES20.glGetUniformLocation(mProgram, "rt_w");
        mRTHeightHandle = GLES20.glGetUniformLocation(mProgram, "rt_h");
        mOffsetHandle = GLES20.glGetUniformLocation(mProgram, "vx_offset");
        mPassHandle = GLES20.glGetUniformLocation(mProgram, "pass");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "sceneTex");
    }

    @Override
    public void feed(float[] mat) {
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mat, 0);
    }

    @Override
    public void feed(FloatBuffer vertexBuffer) {
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, vertexBuffer
        );
    }

    /**
     * Does nothing. Blur shader program does not support color filtering.
     *
     * @param r The red component of the color
     * @param g The green component of the color
     * @param b The blue component of the color
     * @param a The alpha component of the color
     */
    @Override
    public void feed(float r, float g, float b, float a) {
        // We don't need a color value.
    }

    /**
     * Feeds the amount of blur to the shader program.
     *
     * @param amount Blur amount
     */
    public void feedAmount(float amount) {
        GLES20.glUniform1f(mOffsetHandle, amount);
    }

    /**
     * Feeds frame dimensions to the shader program.
     *
     * @param w Frame width
     * @param h Frame height
     */
    public void feed(float w, float h) {
        GLES20.glUniform1f(mRTWidthHandle, w);
        GLES20.glUniform1f(mRTHeightHandle, h);
    }

    /**
     * Feeds rendering pass to the shader program.
     *
     * @param pass Rendering pass (can take either 0 or 1)
     */
    public void feedPass(int pass) {
        GLES20.glUniform1i(mPassHandle, pass % 2);
    }
}
