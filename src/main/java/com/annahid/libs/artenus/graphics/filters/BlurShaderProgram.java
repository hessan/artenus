package com.annahid.libs.artenus.graphics.filters;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;

import java.nio.FloatBuffer;

/**
 * A shader program used by the blur post-processing filter.
 */
class BlurShaderProgram implements ShaderProgram {
    /**
     * Vertex shader code for the blur program.
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
     * Fragment shader code for the program.
     */
    private static final String fragmentShaderCodeV =
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
     * OpenGL shader program handle.
     */
    private int mProgram;

    /**
     * Projection matrix.
     */
    private int mMVPMatrixHandle;

    /**
     * The handle to the position variable in the OpenGL ES vertex shader.
     */
    private int mPositionHandle;

    /**
     * The handle to the texture coordinates variable in the OpenGL ES vertex shader.
     */
    private int mTexCoordsHandle;

    /**
     * The handle to the sampler variable in the OpenGL ES fragment shader.
     */
    private int mSamplerHandle;

    /**
     * The handle to the render target width variable in the OpenGL ES fragment shader.
     */
    private int mRTWidthHandle;

    /**
     * The handle to the render target height variable in the OpenGL ES fragment shader.
     */
    private int mRTHeightHandle;

    /**
     * The handle to the blur offset variable in the OpenGL ES fragment shader.
     */
    private int mOffsetHandle;

    /**
     * The handle to the pass variable in the OpenGL ES fragment shader.
     */
    private int mPassHandle;

    /**
     * Compiles this shader program and gathers all shader variable handles.
     */
    @Override
    public void compile() {
        final int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        final int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCodeV);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);

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
     * This method does nothing. Blur shader program does not support color filtering.
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
     * Activates this shader program.
     */
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

    /**
     * Cleans up any resources allocated for the shader program, and disassociates it from the
     * graphics context.
     */
    @Override
    public void cleanup() {
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordsHandle);
    }

    /**
     * Feeds a texture to the shader program.
     * @param textureDataHandle Texture handle
     */
    public void feed(int textureDataHandle) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(mSamplerHandle, 0);
    }

    /**
     * Feeds the amount of blur to the shader program.
     * @param amount Blur amount
     */
    public void feedAmount(float amount) {
        GLES20.glUniform1f(mOffsetHandle, amount);
    }

    /**
     * Feeds frame dimensions to the shader program.
     * @param w Frame width
     * @param h Frame height
     */
    public void feed(float w, float h) {
        GLES20.glUniform1f(mRTWidthHandle, w);
        GLES20.glUniform1f(mRTHeightHandle, h);
    }

    /**
     * Feeds rendering pass to the shader program.
     * @param pass Rendering pass (can take either 0 or 1)
     */
    public void feedPass(int pass) {
        GLES20.glUniform1i(mPassHandle, pass % 2);
    }

    /**
     * Feeds texture coordinates to the shader program.
     * @param buffer Texture coordinate buffer
     */
    public void feedTexCoords(FloatBuffer buffer) {
        GLES20.glEnableVertexAttribArray(mTexCoordsHandle);
        GLES20.glVertexAttribPointer(mTexCoordsHandle, 2, GLES20.GL_FLOAT, false, 0, buffer);
    }

    /**
     * Loads a shader of the given type to attach to an OpenGL ES shader program.
     *
     * @param type       Shader type
     * @param shaderCode Shader code in plain text
     * @return The handle to the shader
     */
    protected int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
