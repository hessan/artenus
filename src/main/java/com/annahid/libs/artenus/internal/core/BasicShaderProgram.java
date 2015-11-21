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

package com.annahid.libs.artenus.internal.core;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;

import java.nio.FloatBuffer;

/**
 * The default shader program used when {@code null} is specified for the shader program.
 *
 * @author Hessan Feghhi
 */
class BasicShaderProgram implements ShaderProgram {
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            '}';
    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            '}';
    /**
     * OpenGL shader program handle.
     */
    int mProgram;

    /**
     * Handle to the vertex shader.
     */
    private int mVertexShader;

    /**
     * Handle to the fragment shader.
     */
    private int mFragmentShader;

    int mMVPMatrixHandle;
    int mColorHandle;
    int mPositionHandle;

    /**
     * Compiles the shader program.
     */
    @Override
    public void compile() {
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, mVertexShader);
        GLES20.glAttachShader(mProgram, mFragmentShader);
        GLES20.glLinkProgram(mProgram);

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
    }

    @Override
    public void feed(float[] mat) {
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mat, 0);
    }

    @Override
    public void feed(FloatBuffer vertexBuffer) {
        GLES20.glEnableVertexAttribArray(mPositionHandle);
        GLES20.glVertexAttribPointer(
                mPositionHandle, 3, GLES20.GL_FLOAT, false, 12, vertexBuffer
        );
    }

    @Override
    public void feed(float r, float g, float b, float a) {
        GLES20.glUniform4f(mColorHandle, r, g, b, a);
    }

    @Override
    public void activate() {
        GLES20.glUseProgram(mProgram);
    }

    @Override
    public void cleanup() {
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    @Override
    public void destroy() {
        GLES20.glDeleteShader(mFragmentShader);
        GLES20.glDeleteShader(mVertexShader);
        GLES20.glDeleteProgram(mProgram);
    }

    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
