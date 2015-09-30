package com.annahid.libs.artenus.graphics;

import android.opengl.GLES20;

import com.annahid.libs.artenus.core.ShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class TextureShaderProgram implements ShaderProgram {
    private static final String vertexShaderCode =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "attribute vec2 aTexCoord;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  vTexCoord = aTexCoord;" +
            "  gl_Position = uMVPMatrix * vPosition;" +
            '}';
    private static final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "uniform sampler2D uTex;" +
            "varying vec2 vTexCoord;" +
            "void main() {" +
            "  gl_FragColor = vColor * texture2D( uTex, vTexCoord );" +
            '}';
    public static FloatBuffer defaultTextureBuffer;
    protected int mProgram;
    protected int mMVPMatrixHandle;
    protected int mColorHandle;
    protected int mPositionHandle;
    protected int mTexCoordsHandle;
    protected int mSamplerHandle;

    public TextureShaderProgram() {
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

    @Override
    public void compile() {
        final int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        final int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        mTexCoordsHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "uTex");
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
    }

    @Override
    public void cleanup() {
        GLES20.glDisableVertexAttribArray(mPositionHandle);
        GLES20.glDisableVertexAttribArray(mTexCoordsHandle);
    }

    public void feedTexCoords(FloatBuffer buffer) {
        if (buffer == null)
            buffer = defaultTextureBuffer;

        GLES20.glEnableVertexAttribArray(mTexCoordsHandle);
        GLES20.glVertexAttribPointer(mTexCoordsHandle, 2, GLES20.GL_FLOAT, false, 0, buffer);
    }

    public void feed(int textureDataHandle) {
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureDataHandle);
        GLES20.glUniform1i(mSamplerHandle, 0);
    }

    protected int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
