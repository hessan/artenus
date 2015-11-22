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
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;

import com.annahid.libs.artenus.core.StageEvents;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;
import com.annahid.libs.artenus.graphics.filters.FilterPassSetup;
import com.annahid.libs.artenus.graphics.filters.PostProcessingFilter;
import com.annahid.libs.artenus.graphics.rendering.ShaderManager;
import com.annahid.libs.artenus.graphics.rendering.Viewport;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.input.TouchMap;
import com.annahid.libs.artenus.data.RGB;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Used by stage as the internal OpenGL ES 2.0 renderer.
 *
 * @author Hessan Feghhi
 */
final class InternalRenderer implements GLSurfaceView.Renderer, RenderingContext {
    /**
     * Contains currently effective filters.
     */
    List<PostProcessingFilter> filters = new ArrayList<>(10);

    /**
     * Holds calculated logical width.
     */
    float vw;

    /**
     * Holds calculated logical height.
     */
    float vh;

    /**
     * Holds the real width of the rendering area.
     */
    int screenWidth;

    /**
     * Holds the real height of the rendering area.
     */
    int screenHeight;

    /**
     * Holds the transformation matrix stack used to comply with rendering context requirement.
     */
    private final Stack<float[]> matrixStack = new Stack<>();

    /**
     * Holds the projection matrix, which will be multipled with the transformation matrix.
     */
    private final float[] mvpMatrix = new float[16];

    /**
     * Holds current transformation matrix.
     */
    private float[] currentMatrix;

    /**
     * Holds the scratch matrix, used for computations.
     */
    private float[] scratch;

    /**
     * Holds the vertex buffer for the default rectangular primitive.
     */
    private FloatBuffer vertexBuffer = null;

    /**
     * Holds the current shader program.
     */
    private ShaderProgram shader;

    /**
     * Holds the default shader program used when {@code null} shader program is specified.
     */
    private ShaderProgram basicShader;

    /**
     * Holds the parent stage.
     */
    private StageImpl stage;

    /**
     * Holds the graphics shown when texture loading is in progress.
     */
    private LoadingGraphics loading = new LoadingGraphics();

    /**
     * Contains the two back-buffers used for triple buffering.
     */
    private RenderTarget[] targets = new RenderTarget[2];

    /**
     * Used to delay texture loading a bit to let the loading screen appear first. It holds the
     * start time for delay calculation.
     */
    private long loadingDelay = 0;

    /**
     * Creates a new internal renderer for the given internal stage implementation.
     *
     * @param stage Internal stage
     */
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

        currentMatrix = new float[] {
                1, 0, 0, 0,
                0, 1, 0, 0,
                0, 0, 1, 0,
                0, 0, 0, 1
        };

        scratch = new float[16];
        basicShader = new BasicShaderProgram();
        ShaderManager.register(basicShader);
        shader = basicShader;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glEnable(GLES20.GL_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);
        GLES20.glBlendFunc(GLES20.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA);

        if (stage.handler != null)
            stage.handler.onEvent(stage, StageEvents.DISPLAY);

        ShaderManager.loadAll();
        TextureManager.unloadTextures();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        if (width > height) {
            vh = 600.0f;
            vw = width * vh / height;
        } else {
            vw = 600.0f;
            vh = height * vw / width;
        }

        screenWidth = width;
        screenHeight = height;
        TextureManager.setTextureScalingFactor(
                (float) Math.min(screenHeight, screenWidth) / 600.0f
        );

        Matrix.orthoM(mvpMatrix, 0, 0, vw, vh, 0, -1, 1);

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

    @Override
    public void pushMatrix() {
        float[] newMatrix = currentMatrix.clone();
        matrixStack.push(currentMatrix);
        currentMatrix = newMatrix;
    }

    @Override
    public void popMatrix() {
        if (matrixStack.size() > 0) {
            currentMatrix = matrixStack.pop();
        }
    }

    @Override
    public void rotate(float angle) {
        Matrix.rotateM(currentMatrix, 0, angle, 0, 0, 1);
    }

    @Override
    public void scale(float x, float y) {
        Matrix.scaleM(currentMatrix, 0, x, y, 1);
    }

    @Override
    public void translate(float x, float y) {
        Matrix.translateM(currentMatrix, 0, x, y, 0);
    }

    @Override
    public void identity() {
        Matrix.setIdentityM(currentMatrix, 0);
    }

    @Override
    public void setColorFilter(float r, float g, float b, float a) {
        shader.feed(r, g, b, a);
    }

    @Override
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
        if (currentMatrix == null)
            return null;

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

    /**
     * Gets the loading graphics.
     *
     * @return Loading graphics
     */
    LoadingGraphics getLoadingGraphics() {
        return loading;
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
