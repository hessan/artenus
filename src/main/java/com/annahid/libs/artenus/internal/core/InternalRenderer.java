package com.annahid.libs.artenus.internal.core;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.annahid.libs.artenus.core.RenderingContext;
import com.annahid.libs.artenus.core.ShaderProgram;
import com.annahid.libs.artenus.core.StageManager;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.input.TouchMap;
import com.annahid.libs.artenus.data.ConcurrentCollection;
import com.annahid.libs.artenus.data.RGB;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The internal stage renderer.
 */
class InternalRenderer implements GLSurfaceView.Renderer, RenderingContext {
    /**
     * The transformation matrix stack used to comply with rendering context requirement.
     */
    private final Stack<float[]> matrixStack = new Stack<>();

    /**
     * The projection matrix, which will be multipled with the transformation matrix.
     */
    private final float[] mvpMatrix = new float[16];

    /**
     * The default background color of the stage, if it is not defined by the current scene.
     */
    private final RGB clearColor = new RGB(0, 0, 0);

    /**
     * Calculated logical width.
     */
    float vw;

    /**
     * Calculated logical height.
     */
    float vh;

    /**
     * The real width of the rendering area.
     */
    int screenWidth;

    /**
     * The real height of the rendering area.
     */
    int screenHeight;

    /**
     * Current transformation matrix.
     */
    private float[] currentMatrix;

    /**
     * Scratch matrix, used for computations.
     */
    private float[] scratch;

    /**
     * The vertex buffer for the default rectangular primitive.
     */
    private FloatBuffer vertexBuffer = null;

    /**
     * The current shader program.
     */
    private ShaderProgram shader;

    /**
     * The default shader program used when {@code null} shader program is specified.
     */
    private ShaderProgram basicShader;

    private StageImpl stage;
    private Collection<ShaderProgram> registeredShaders = new ConcurrentCollection<>();
    private LoadingGraphics loading = new LoadingGraphics();

    /**
     * This field is used to delay texture loading  a bit to let
     * the loading screen appear before. It holds the start time
     * for delay calculation.
     */
    private long lll = 0;

    public InternalRenderer(StageImpl stage) {
        this.stage = stage;

        if (vertexBuffer == null) {
            final float vertices[] = {
                    -0.5f, -0.5f, 0.0f,  // 0. left-bottom
                    0.5f, -0.5f, 0.0f,  // 1. right-bottom
                    -0.5f, 0.5f, 0.0f,  // 2. left-top
                    0.5f, 0.5f, 0.0f   // 3. right-top
            };

            final ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
            vbb.order(ByteOrder.nativeOrder());
            vertexBuffer = vbb.asFloatBuffer();
            vertexBuffer.put(vertices);
            vertexBuffer.position(0);
        }

        currentMatrix = new float[]{
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };

        scratch = new float[16];
        basicShader = new BasicShaderProgram();
        shader = basicShader;
        registerShader(shader);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);

        for (ShaderProgram shader : registeredShaders)
            shader.compile();

        GLES20.glEnable(GLES20.GL_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glBlendFunc(GLES20.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        if (stage.handler != null)
            stage.handler.onEvent(stage, StageManager.EVENT_DISPLAY);

        TextureManager.unloadTextures();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        if (width > height) {
            vh = 600f;
            vw = width * vh / height;
        } else {
            vw = 600f;
            vh = height * vw / width;
        }

        final RGB clearColor = stage.currentScene == null ?
                this.clearColor : stage.currentScene.getBackColor();
        GLES20.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);
        screenWidth = width;
        screenHeight = height;
        TextureManager.setTextureScalingFactor(
                (float) Math.min(screenHeight, screenWidth) / 600.0f
        );
        onResize(vw, vh);
        TouchMap.update((int) vw, (int) vh);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        final int ts = TextureManager.getCurrentState();

        if (ts != TextureManager.STATE_LOADED) {
            if (IntroScene.introShown)
                loading.render(this, 0);

            if (lll == 0)
                lll = System.currentTimeMillis();

            if (ts == TextureManager.STATE_FRESH && System.currentTimeMillis() - lll > 200)
                TextureManager.loadTextures();

            return;
        }

        lll = 0;

        final RGB clearColor = stage.currentScene == null ?
                this.clearColor : stage.currentScene.getBackColor();
        GLES20.glClearColor(clearColor.r, clearColor.g, clearColor.b, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        if (stage.currentScene != null) {
            if (!stage.currentScene.isLoaded())
                stage.currentScene.onLoaded();

            stage.currentScene.render(this);
        }

        shader.cleanup();
        setShader(null);

        if (stage.stPhase > 0) {
            pushMatrix();
            translate(vw / 2, vh / 2);
            scale(vw, vh);
            GLES20.glDisable(GLES20.GL_TEXTURE_2D);
            setColorFilter(0, 0, 0, stage.stPhase);
            rect();
            popMatrix();
        }

        if (stage.currentScene != null) {
            if (stage.currentScene.getDialog() != null)
                stage.currentScene.getDialog().getTouchMap().process(this);
            else stage.currentScene.getTouchMap().process(this);
            shader.cleanup();
        }

        setColorFilter(1, 1, 1, 1);
    }

    public void pushMatrix() {
        float[] newMatrix = currentMatrix.clone();
        matrixStack.push(currentMatrix);
        currentMatrix = newMatrix;
    }

    public void popMatrix() {
        if (matrixStack.size() > 0) {
            currentMatrix = matrixStack.pop();
        }
    }

    public void rotate(float angle) {
        Matrix.rotateM(currentMatrix, 0, angle, 0, 0, 1);
    }

    public void scale(float x, float y) {
        Matrix.scaleM(currentMatrix, 0, x, y, 1);
    }

    public void translate(float x, float y) {
        Matrix.translateM(currentMatrix, 0, x, y, 0);
    }

    public void identity() {
        Matrix.setIdentityM(currentMatrix, 0);
    }

    public void setColorFilter(float r, float g, float b, float a) {
        shader.feed(r, g, b, a);
    }

    public void rect() {
        Matrix.multiplyMM(scratch, 0, mvpMatrix, 0, currentMatrix, 0);
        shader.feed(scratch);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
    }

    @Override
    public ShaderProgram getShader() {
        return shader;
    }

    @Override
    public void setShader(ShaderProgram shader) {
        this.shader = shader == null ? basicShader : shader;
        this.shader.activate();
        this.shader.feed(vertexBuffer);
    }

    @Override
    public float getWidth() {
        return vw;
    }

    @Override
    public float getHeight() {
        return vh;
    }

    @Override
    public int getScreenWidth() {
        return screenWidth;
    }

    @Override
    public int getScreenHeight() {
        return screenHeight;
    }

    @Override
    public float[] getMatrix() {
        return currentMatrix.clone();
    }

    @Override
    public void pushMatrix(float[] mat) {
        matrixStack.push(currentMatrix);
        currentMatrix = mat;
    }

    LoadingGraphics getLoadingGraphics() {
        return loading;
    }

    void setClearColor(float r, float g, float b) {
        this.clearColor.r = r;
        this.clearColor.g = g;
        this.clearColor.b = b;
        GLES20.glClearColor(r, g, b, 1.0f);
    }

    void onResize(float w, float h) {
        Matrix.orthoM(mvpMatrix, 0, 0, w, h, 0, -1, 1);
    }

    void registerShader(ShaderProgram shader) {
        registeredShaders.add(shader);
    }

    void unregisterShader(ShaderProgram shader) {
        registeredShaders.remove(shader);
    }
}
