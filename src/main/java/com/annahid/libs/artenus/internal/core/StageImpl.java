package com.annahid.libs.artenus.internal.core;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.R;
import com.annahid.libs.artenus.core.Dialog;
import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;
import com.annahid.libs.artenus.core.Stage;
import com.annahid.libs.artenus.core.StageManager;
import com.annahid.libs.artenus.graphics.filters.PostProcessingFilter;
import com.annahid.libs.artenus.input.TouchEvent;
import com.annahid.libs.artenus.data.RGB;
import com.annahid.libs.artenus.graphics.TextureManager;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The default implementation of the {@code Stage} interface.
 *
 * @author Hessan Feghhi
 * @see StageManager
 */
@SuppressWarnings("UnusedDeclaration")
public final class StageImpl extends GLSurfaceView implements Stage {
    /**
     * The stage manager currently handling stage events.
     */
    StageManager handler = null;

    /**
     * The current scene.
     */
    Scene currentScene;

    /**
     * The next scene to be displayed. This field only has a non-null value when a scene transition
     * is in progress.
     */
    Scene nextScene;

    /**
     * Scene transition phase.
     */
    float stPhase;

    /**
     * The thread running the main animation loop.
     */
    private Thread advanceThread;

    private AtomicInteger advanceThreadId = new AtomicInteger(1000);

    /**
     * The renderer.
     */
    private InternalRenderer mRenderer;

    /**
     * Creates a new stage on the given application context and with the given set of XML
     * attributes. This method will be automatically called if you put the {@code Stage} tag in your
     * layout XML.
     *
     * @param context The application context
     * @param attrs   The set of attributes
     */
    public StageImpl(Context context, AttributeSet attrs) {
        super(context, attrs);
        TextureManager.setLoadingTexture(R.raw.loading);
        currentScene = null;
        nextScene = null;

        setEGLContextClientVersion(2);

        @SuppressWarnings("deprecation")
        int px = Artenus.getInstance().getWindowManager().getDefaultDisplay().getPixelFormat();

        if (px == PixelFormat.RGB_565)
            setEGLConfigChooser(5, 6, 5, 0, 0, 0);
        else setEGLConfigChooser(8, 8, 8, 8, 0, 0);

        setEGLContextClientVersion(2);
        mRenderer = new InternalRenderer(this);
        setRenderer(mRenderer);
        scheduleTimer();

        if (Build.VERSION.SDK_INT >= 11)
            setPreserveEGLContextOnPause(true);

        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        setScene(new IntroScene(this));
    }

    /**
     * Sets the background color of the loading screen.
     *
     * @param bgColor Background color
     */
    @Override
    public void setLoadingBackColor(RGB bgColor) {
        mRenderer.getLoadingGraphics().setBackColor(bgColor);
    }

    /**
     * Converts screen x to stage x coordination considering the scaling factor.
     *
     * @param x Screen x
     * @return Stage x
     */
    @Override
    public final float screenToStageX(float x) {
        return x * mRenderer.vw / mRenderer.screenWidth;
    }

    /**
     * Converts screen y to stage x coordination considering the scaling factor.
     *
     * @param y Screen y
     * @return Stage y
     */
    @Override
    public final float screenToStageY(float y) {
        return y * mRenderer.vh / mRenderer.screenHeight;
    }

    /**
     * Converts stage x to screen x coordination considering the scaling factor.
     *
     * @param x Stage x
     * @return Screen x
     */
    @Override
    public final float stageToScreenX(float x) {
        return x * mRenderer.screenWidth / mRenderer.vw;
    }

    /**
     * Converts stage y to screen y coordination considering the scaling factor.
     *
     * @param y Stage y
     * @return Screen y
     */
    @Override
    public final float stageToScreenY(float y) {
        return y * mRenderer.screenHeight / mRenderer.vh;
    }

    /**
     * Sets the next scene that should be shown on this {@code Stage}. The scene will not be shown
     * immediately and will go through a transition effect and possibly a local loading procedure.
     *
     * @param scene The new scene
     */
    @Override
    public final void setScene(Scene scene) {
        if (!(currentScene instanceof IntroScene)) {
            nextScene = scene;
        }
    }

    /**
     * Gets the current scene. If a new scene has been set, but the transition is not yet complete,
     * previous scene might be returned.
     *
     * @return The current scene
     */
    @Override
    public final Scene getScene() {
        return currentScene;
    }

    /**
     * Gets the width of the stage. If the device is in portrait mode, this value is always 600 and
     * otherwise it is the scaled version of screen width to match the height of 600.
     *
     * @return Stage width
     */
    @Override
    public final float getLogicalWidth() {
        return mRenderer.vw;
    }

    /**
     * Gets the width of the stage. If the device is in landscape mode, this value is always 600 and
     * otherwise it is the scaled version of screen height to match the width of 600.
     *
     * @return Stage width
     */
    @Override
    public final float getLogicalHeight() {
        return mRenderer.vh;
    }

    /**
     * Adds a filter to this stage's rendering pipeline. If the filter is already added, it will be
     * moved to the end of the pipeline.
     *
     * @param filter The filter to be added
     */
    @Override
    public void addFilter(PostProcessingFilter filter) {
        if (mRenderer.filters.contains(filter)) {
            mRenderer.filters.remove(filter);
        }
        mRenderer.filters.add(filter);
    }

    @Override
    public void removeFilter(PostProcessingFilter filter) {
        mRenderer.filters.remove(filter);
    }

    /**
     * This method handles the physical back button for this {@code Stage}.
     *
     * @return {@code true} if the back button has been handled by this {@code Stage} or the
     * currently active {@code Scene}, {@code false} otherwise
     * @see com.annahid.libs.artenus.core.Scene
     */
    public boolean onBackButton() {
        if (currentScene == null)
            return false;
        else {
            if (currentScene.getDialog() == null)
                return currentScene.onBackButton();
            else return currentScene.getDialog().onBackButton();
        }
    }

    /**
     * Gets the currently assigned stage manager. A stage manager handles basic events and
     * functionality for this {@code Stage}.
     *
     * @return The stage manager for this stage
     */
    public StageManager getManager() {
        return handler;
    }

    /**
     * Appoints a stage manager to handle required functionality for this {@code Stage}. You MUST
     * assign a stage manager before doing anything else with this {@code Stage}.
     *
     * @param stageManager The stage manager
     */
    public void setManager(StageManager stageManager) {
        handler = stageManager;
    }

    /**
     * Handles motion events for this {@code Stage} and passes them on to the input manager and
     * current scene or dialog.
     */
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        int action = event.getAction() & MotionEvent.ACTION_MASK;
        final int pointerIndex = event.getActionIndex();
        final float x = screenToStageX(event.getX(pointerIndex));
        final float y = screenToStageY(event.getY(pointerIndex));
        final int pointerId = event.getPointerId(pointerIndex);

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN)
            action = TouchEvent.EVENT_DOWN;
        else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP)
            action = TouchEvent.EVENT_UP;
        else action = TouchEvent.EVENT_MOVE;

        TouchEvent touchEvent = new TouchEvent(action, pointerId, x, y);

        if (currentScene != null) {
            final Dialog dialog = currentScene.getDialog();

            if (dialog != null) {
                if (dialog.getResult() == Dialog.RESULT_NONE
                        && dialog.isLoaded()) {
                    dialog.handleTouch(touchEvent);
                }
            } else if (currentScene.isLoaded()) {
                currentScene.handleTouch(touchEvent);
            }

            if (currentScene.getDialog() != null)
                currentScene.getDialog().getTouchMap().onTouchEvent(touchEvent);
            else currentScene.getTouchMap().onTouchEvent(touchEvent);
        }

        return true;
    }

    /**
     * This method is inherited from {@code GLSurfaceView}.
     */
    @Override
    public void onPause() {
        super.onPause();
        TextureManager.getLoadingTexture().destroy();
        advanceThread = null;

        if (handler != null)
            handler.onEvent(this, StageManager.EVENT_PAUSE);
    }

    /**
     * This method is inherited from {@code GLSurfaceView}.
     */
    @Override
    public void onResume() {
        super.onResume();
        scheduleTimer();

        if (handler != null)
            handler.onEvent(this, StageManager.EVENT_RESUME);
    }

    /**
     * Forces the new scene. This method is called from the intro scene.
     *
     * @param scene The new scene
     */
    final void forceScene(Scene scene) {
        nextScene = scene;
    }

    /**
     * Schedules the advance (frame) timer
     */
    private void scheduleTimer() {
        if (advanceThread == null) {
            advanceThread = new StageAdvanceTask();
            advanceThread.start();
        }
    }

    /**
     * Handles the main game loop.
     */
    private final class StageAdvanceTask extends Thread {
        private long mLastTime;
        private int tid;

        private StageAdvanceTask() {
            mLastTime = System.nanoTime();
            tid = advanceThreadId.incrementAndGet();
        }

        @Override
        public void run() {
            while (true) {
                if (advanceThread == null || tid != advanceThreadId.get())
                    return;

                final long time = System.nanoTime();
                final long diff = time - mLastTime;

                if (diff < 64000000) {
                    if (nextScene != null) {
                        stPhase = Math.min(1, stPhase + diff / 250000000.0f);

                        if (stPhase == 1) {
                            TextureManager.unloadLocal();
                            nextScene.onLocalLoad();
                            currentScene = nextScene;
                            nextScene = null;
                            mRenderer.getLoadingGraphics().renew();
                        }
                    } else if (stPhase > 0)
                        stPhase = Math.max(0, stPhase - diff / 250000000.0f);

                    if (currentScene != null)
                        if (currentScene.isLoaded())
                            currentScene.advance(diff / 1000000000.0f);

                    requestRender();
                }

                mLastTime = time;

                if (diff < 17000000)
                    try {
                        Thread.sleep(20 - diff / 1000000);
                    } catch (InterruptedException e) {
                        // Do nothing
                    }
            }
        }
    }
}
