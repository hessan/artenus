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

package com.annahid.libs.artenus.graphics;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Used by the Artenus framework to render textured entities.
 *
 * @author Hessan Feghhi
 */
public class TextureShaderProgram implements ShaderProgram {
    /**
     * Holds vertex shader code.
     */
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 aTexCoord;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  vTexCoord = aTexCoord;" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            '}';

    /**
     * Holds fragment shader code, taking texture sampler as an argument.
     */
    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "uniform sampler2D uTex;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  gl_FragColor = vColor * texture2D( uTex, vTexCoord );" +
            '}';

    /**
     * Holds the default texture buffer used if no other is specified.
     */
    private static FloatBuffer defaultTextureBuffer;

    /**
     * Holds OpenGL shader program handle.
     */
    protected int mProgram;

    /**
     * Holds projection matrix.
     */
    protected int mMVPMatrixHandle;

    /**
     * Holds the handle to the color variable in the OpenGL ES fragment shader.
     */
    protected int mColorHandle;

    /**
     * Holds the handle to the position variable in the OpenGL ES vertex shader.
     */
    protected int mPositionHandle;

    /**
     * Holds the handle to the texture coordinates variable in the OpenGL ES vertex shader.
     */
    protected int mTexCoordsHandle;

    /**
     * Holds the handle to the sampler variable in the OpenGL ES fragment shader.
     */
    protected int mSamplerHandle;

    /**
     * Holds the handle to the vertex shader.
     */
    private int mVertexShader;

    /**
     * Holds the handle to the fragment shader.
     */
    private int mFragmentShader;

    /**
     * Gets the default texture coordinates buffer, which includes the whole area of the texture
     * image.
     *
     * @return Default texture coordinates
     */
    public static FloatBuffer getDefaultTextureBuffer() {
        if (defaultTextureBuffer == null) {
            final float texture[] = {
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    0.0f, 1.0f,
                    1.0f, 1.0f,
            };
            final ByteBuffer ibb = ByteBuffer.allocateDirect(texture.length * 4);
            ibb.order(ByteOrder.nativeOrder());
            defaultTextureBuffer = ibb.asFloatBuffer();
            defaultTextureBuffer.put(texture);
            defaultTextureBuffer.position(0);
        }
        return defaultTextureBuffer;
    }

    /**
     * Compiles the shader program and sets up variables and handles.
     */
    @Override
    public void compile() {
        compile(vertexShaderCode, fragmentShaderCode);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "uTex");
    }

    @Override
    public void destroy() {
        GLES20.glDeleteShader(mFragmentShader);
        GLES20.glDeleteShader(mVertexShader);
        GLES20.glDeleteProgram(mProgram);
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

    @Override
    public void feed(float r, float g, float b, float a) {
        GLES20.glUniform4f(mColorHandle, r, g, b, a);
    }

    @Override
    public void activate() {
        GLES20.glUseProgram(mProgram);
        GLES20.glEnableVertexAttribArray(mTexCoordsHandle);
        GLES20.glVertexAttribPointer(
                mTexCoordsHandle,
                2,
                GLES20.GL_FLOAT,
                false,
                0,
                TextureShaderProgram.getDefaultTextureBuffer()
        );
    }

    @Override
    public void cleanup() {
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordsHandle);
    }

    /**
     * Feeds texture a coordinate buffer to this shader program.
     *
     * @param buffer Texture coordinate buffer
     */
    public void feedTexCoords(FloatBuffer buffer) {
        if (buffer == null)
            buffer = defaultTextureBuffer;
        GLES20.glEnableVertexAttribArray(mTexCoordsHandle);
        GLES20.glVertexAttribPointer(mTexCoordsHandle, 2, GLES20.GL_FLOAT, false, 0, buffer);
    }

    /**
     * Feeds a texture to this shader program.
     *
     * @param textureDataHandle Texture data handle
     */
    public void feed(int textureDataHandle) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(mSamplerHandle, 0);
    }

    /**
     * Compiles this shader program with given vertex and fragment shader codes.
     *
     * @param vertexShaderCode   Vertex shader code
     * @param fragmentShaderCode Fragment shader code
     */
    protected void compile(String vertexShaderCode, String fragmentShaderCode) {
        mVertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        mFragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, mVertexShader);
        GLES20.glAttachShader(mProgram, mFragmentShader);
        GLES20.glLinkProgram(mProgram);
    }

    /**
     * Loads a shader of the given type to attach to an OpenGL ES shader program.
     *
     * @param type       Shader type
     * @param shaderCode Shader code in plain text
     *
     * @return The handle to the shader
     */
    private int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
