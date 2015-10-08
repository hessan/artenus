package com.annahid.libs.artenus.internal.core;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.filters.FilterPassSetup;
import com.annahid.libs.artenus.graphics.filters.PostProcessingFilter;
import com.annahid.libs.artenus.graphics.rendering.Viewport;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;
import com.annahid.libs.artenus.core.StageManager;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.input.TouchMap;
import com.annahid.libs.artenus.data.ConcurrentCollection;
import com.annahid.libs.artenus.data.RGB;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * The internal stage renderer.
 *
 * @author Hessan Feghhi
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
     * The list of filters currently effective.
     */
    List<PostProcessingFilter> filters = new ArrayList<>(10);

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

    /**
     * Parent stage.
     */
    private StageImpl stage;

    /**
     * Shader programs currently registered with the stage.
     */
    private Collection<ShaderProgram> registeredShaders = new ConcurrentCollection<>();
    private LoadingGraphics loading = new LoadingGraphics();
    private RenderTarget[] targets = new RenderTarget[2];

    /**
     * This field is used to delay texture loading  a bit to let
     * the loading screen appear before. It holds the start time
     * for delay calculation.
     */
    private long loadingDelay = 0;

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

        screenWidth = width;
        screenHeight = height;
        TextureManager.setTextureScalingFactor(
                (float) Math.min(screenHeight, screenWidth) / 600.0f
        );
        onResize(vw, vh);

        for (int i = 0; i < targets.length; i++) {
            if (targets[i] != null) {
                targets[i].dispose();
            }
            targets[i] = RenderTarget.create(width, height);
        }

        TouchMap.update((int) vw, (int) vh);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        final int ts = TextureManager.getCurrentState();
        if (ts != TextureManager.STATE_LOADED) {
            if (IntroScene.introShown)
                loading.render(this);

            if (loadingDelay == 0)
                loadingDelay = System.currentTimeMillis();

            if (ts == TextureManager.STATE_FRESH && System.currentTimeMillis() - loadingDelay > 200)
                TextureManager.loadTextures();

            return;
        }
        loadingDelay = 0;

        final Viewport defaultViewport = new Viewport(screenWidth, screenHeight);
        RenderTarget renderTarget = targets[0], inputTarget = targets[1];
        renderTarget.setViewport(defaultViewport);
        inputTarget.setViewport(defaultViewport);
        bindTarget(renderTarget);
        renderRaw();
        PostProcessingFilter[] filters =
                this.filters.toArray(new PostProcessingFilter[this.filters.size()]);
        for (PostProcessingFilter filter : filters) {
            boolean hasMorePasses = true;
            int pass = 0;
            while (hasMorePasses) {
                FilterPassSetup setup = new FilterPassSetup(renderTarget.getViewport());
                hasMorePasses = filter.setup(pass, setup);

                if (!setup.isInPlace()) {
                    if (renderTarget == targets[0]) {
                        renderTarget = targets[1];
                        inputTarget = targets[0];
                    } else {
                        renderTarget = targets[0];
                        inputTarget = targets[1];
                    }
                    bindTarget(renderTarget);
                    clear(0, 0, 0);
                }

                renderTarget.setViewport(setup);
                filter.render(pass, this, inputTarget);
                pass++;
            }
        }

        bindTarget(null);
        GLES20.glViewport(0, 0, screenWidth, screenHeight);
        TextureShaderProgram program = (TextureShaderProgram) TextureManager.getShaderProgram();
        clear(0, 0, 0);
        setShader(program);
        setColorFilter(1, 1, 1, 1);
        pushMatrix();
        identity();
        program.feed(renderTarget.getTextureHandle());
        program.feedTexCoords(renderTarget.getTextureCoords());
        translate(vw / 2, vh / 2);
        scale(vw, -vh);
        rect();
        popMatrix();

        if (stage.currentScene != null) {
            if (stage.currentScene.getDialog() != null)
                stage.currentScene.getDialog().getTouchMap().process(this);
            else stage.currentScene.getTouchMap().process(this);
            shader.cleanup();
        }
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

    @Override
    public void clear(float r, float g, float b) {
        GLES20.glClearColor(r, g, b, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void bindTarget(RenderTarget target) {
        if (target == null) {
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
            return;
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, target.getFrameBufferHandle());
        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                target.getTextureHandle(),
                0
        );
        Viewport setup = target.getViewport();
        GLES20.glViewport(0, 0, setup.getWidth(), setup.getHeight());
    }

    LoadingGraphics getLoadingGraphics() {
        return loading;
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

    /**
     * Renders the current frame on the first render target.
     */
    private void renderRaw() {
        final RGB clearColor = stage.currentScene == null ?
                new RGB(0, 0, 0) : stage.currentScene.getBackColor();
        clear(clearColor.r, clearColor.g, clearColor.b);

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
            setColorFilter(0, 0, 0, stage.stPhase);
            rect();
            popMatrix();
        }

        setColorFilter(1, 1, 1, 1);
    }
}
