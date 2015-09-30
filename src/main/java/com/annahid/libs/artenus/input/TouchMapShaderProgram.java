package com.annahid.libs.artenus.input;

import android.opengl.GLES20;

import com.annahid.libs.artenus.graphics.TextureShaderProgram;

class TouchMapShaderProgram extends TextureShaderProgram {
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
        mObjectIdHandle = GLES20.glGetUniformLocation(mProgram, "uObjectId");
    }

    public void feedObjectId(int objectId) {
        GLES20.glUniform1f(mObjectIdHandle, objectId / 255.0f);
    }

    private int mObjectIdHandle;

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
                    "uniform float uObjectId;" +
                    "varying vec2 vTexCoord;" +
                    "void main() {" +
                    "  vec4 temp = vColor * texture2D( uTex, vTexCoord );" +
                    "  float a = temp.a;" +
                    "  a = a < 0.5 ? 0.0 : 1.0;" +
                    "  gl_FragColor = vec4(uObjectId * a, 0, 0, a);" +
                    '}';
}
