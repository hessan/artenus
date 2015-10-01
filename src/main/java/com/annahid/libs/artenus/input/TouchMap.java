package com.annahid.libs.artenus.input;

import android.opengl.GLES20;
import android.os.Debug;
import android.support.annotation.NonNull;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.core.RenderingContext;
import com.annahid.libs.artenus.core.ShaderProgram;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.graphics.TextureManager;
import com.annahid.libs.artenus.graphics.TextureShaderProgram;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This class is used internally by the framework to handle bitmapped hit testing for touch buttons.
 * It is highly recommended not to use this class directly, as it might interfere with the default
 * rendering pipeline. The only member of this class that can be used from outside the framework is
 * {@link TouchMap#showMap(boolean)}.
 */
public class TouchMap {
    /**
     * The buffer used to read pixels from the touch map.
     */
    private ByteBuffer pixelBuffer =
            ByteBuffer.allocateDirect(4).order(ByteOrder.nativeOrder());

    /**
     * Indicates whether to show the touch map.
     */
    private boolean show = true;

    /**
     * The touch event queue.
     */
    private Queue<TouchEvent> events = new ConcurrentLinkedQueue<>();

    /**
     * The buttons registered with this touch map.
     */
    private Map<Integer, TouchButton> buttons = new ConcurrentHashMap<>(24);

    /**
     * Holds the width of the frame buffer used for the touch map.
     */
    private static int width;

    /**
     * Holds the height of the frame buffer used for the touch map.
     */
    private static int height;

    /**
     * A value indicating whether this instance has been initialized.
     */
    private static boolean inited = false;

    /**
     * Holds the shader program used to draw the touch map from sprites.
     */
    private static TouchMapShaderProgram shader = null;

    /**
     * Holds the OpenGL ES handle to the texture on which the map is rendered.
     */
    private static int textureHandle;
    /**
     * Holds the OpenGL ES handle for the render buffer used to render the map.
     */
    private static int renderBufferHandle;
    /**
     * Holds the OpenGL ES handle for the frame buffer used to render the map.
     */
    private static int frameBufferHandle;

    /**
     * Called internally bu the stage to queue touch events.
     *
     * @param event The touch event to queue
     */
    public void onTouchEvent(TouchEvent event) {
        events.offer(event);
    }

    /**
     * Processes a single touch event.
     *
     * @param event The touch event to handle
     */
    private void handleTouchEvent(TouchEvent event) {
        pixelBuffer.position(0);
        GLES20.glReadPixels(
                (int) event.x >> 1, height - ((int) event.y >> 1), 1, 1,
                GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer
        );

        TouchButton button = buttons.get((int) pixelBuffer.get(0));

        if (button != null)
            button.internalTouch(event.action, event.pointerId);

        if (event.action == TouchEvent.EVENT_UP) {
            for (Map.Entry<Integer, TouchButton> entry : buttons.entrySet()) {
                final TouchButton item = entry.getValue();
                entry.getValue().internalTouch(
                        item == button ? TouchEvent.EVENT_UP : TouchEvent.EVENT_LEAVE,
                        event.pointerId
                );
            }
        }
    }

    /**
     * Called internally by the stage to render the touch map and process queued touch events.
     *
     * @param context The rendering context
     */
    public void process(RenderingContext context) {
        if (width == 0 || height == 0)
            return;
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);
        GLES20.glFramebufferTexture2D(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_COLOR_ATTACHMENT0,
                GLES20.GL_TEXTURE_2D,
                textureHandle,
                0
        );
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 1, 1, 1);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        ShaderProgram shaderBackup = context.getShader();
        context.setShader(shader);
        for (Map.Entry<Integer, TouchButton> entry : buttons.entrySet()) {
            final TouchButton button = entry.getValue();
            shader.feedObjectId((entry.getKey() + 256) % 256);
            context.pushMatrix(button.popLatestMatrix());
            button.render(context, Renderable.FLAG_IGNORE_EFFECTS);
            context.popMatrix();
        }
        while (!events.isEmpty()) {
            handleTouchEvent(events.poll());
        }

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
        if (show && Debug.isDebuggerConnected()) {
            TextureShaderProgram program = (TextureShaderProgram) TextureManager.getShaderProgram();
            context.setShader(program);
            context.setColorFilter(0.75f, 0.75f, 0.75f, 0.75f);
            context.pushMatrix();
            context.identity();
            program.feed(textureHandle);
            program.feedTexCoords(TextureShaderProgram.defaultTextureBuffer);
            context.translate(520, 280);
            context.rotate(0);
            context.scale(1000, -600);
            context.rect();
            context.popMatrix();
        }
        GLES20.glViewport(0, 0, context.getScreenWidth(), context.getScreenHeight());
        context.setShader(shaderBackup);
    }

    /**
     * Registers a button with this touch map.
     *
     * @param button The button
     */
    void registerButton(TouchButton button) {
        if (button == null)
            throw new IllegalArgumentException("Button cannot be null.");

        buttons.put(button.id, button);
    }

    /**
     * Shows or hides the touch map. By default, Artenus shows the touch map on the corner of the
     * screen when a debugger is attached. You can use this method to override this behavior. Note
     * that the map is not displayed in the absence of a debugger session, even when running a debug
     * build.
     *
     * @param visible A value indicating whether to show the touch map
     */
    @SuppressWarnings("unused")
    public void showMap(boolean visible) {
        show = visible;
    }

    /**
     * Removes a button from this touch map
     *
     * @param button The button
     */
    void unregisterButton(@NonNull TouchButton button) {
        buttons.remove(button.id);
    }

    /**
     * Initializes the touch map and creates necessary resources. If the touch map is already
     * initialized, the old resources are freed first.
     *
     * @param fboWidth  The logical width of the screen
     * @param fboHeight The logical height of the screen
     */
    public static void update(int fboWidth, int fboHeight) {
        fboWidth >>= 1;
        fboHeight >>= 1;

        if (inited) {
            final int[] temp = new int[1];

            temp[0] = renderBufferHandle;
            GLES20.glDeleteRenderbuffers(1, temp, 0);

            temp[0] = frameBufferHandle;
            GLES20.glDeleteFramebuffers(1, temp, 0);

            temp[0] = textureHandle;
            GLES20.glDeleteTextures(1, temp, 0);
        }

        inited = true;

        final int[] temp = new int[1];
        GLES20.glGenFramebuffers(1, temp, 0);
        frameBufferHandle = temp[0];

        GLES20.glGenTextures(1, temp, 0);
        textureHandle = temp[0];

        GLES20.glGenRenderbuffers(1, temp, 0);
        renderBufferHandle = temp[0];

        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, frameBufferHandle);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle);
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR
        );
        GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR
        );
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                fboWidth,
                fboHeight,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                null
        );
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, renderBufferHandle);
        GLES20.glFramebufferRenderbuffer(
                GLES20.GL_FRAMEBUFFER,
                GLES20.GL_DEPTH_ATTACHMENT,
                GLES20.GL_RENDERBUFFER,
                renderBufferHandle
        );

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0);
        GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, 0);
        GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);

        width = fboWidth;
        height = fboHeight;

        shader = new TouchMapShaderProgram();
        Artenus.getInstance().getStage().registerShader(shader);
        shader.compile();
    }
}
