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

package com.annahid.libs.artenus.input;

import android.opengl.GLES20;
import android.os.Debug;
import android.util.Pair;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.graphics.rendering.RenderTarget;
import com.annahid.libs.artenus.graphics.rendering.RenderingContext;
import com.annahid.libs.artenus.graphics.rendering.ShaderManager;
import com.annahid.libs.artenus.graphics.rendering.ShaderProgram;
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
 *
 * @author Hessan Feghhi
 */
public final class TouchMap {
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
     * Queue for events that need to be processed.
     */
    private Queue<TouchEvent> processQueue = new ConcurrentLinkedQueue<>();

    /**
     * Queue for processed events that need to be dispatched.
     */
    private Queue<Pair<TouchButton, TouchEvent>> dispatchQueue = new ConcurrentLinkedQueue<>();

    /**
     * The buttons registered with this touch map.
     */
    private Map<Integer, TouchButton> buttons = new ConcurrentHashMap<>(24);

    /**
     * Holds the shader program used to draw the touch map from sprites.
     */
    private static TouchMapShaderProgram shader = null;

    /**
     * The render target for the touch map.
     */
    private static RenderTarget target;

    /**
     * Initializes the touch map and creates necessary resources. If the touch map is already
     * initialized, the old resources are freed first.
     *
     * @param fboWidth  The logical width of the screen
     * @param fboHeight The logical height of the screen
     */
    public static void update(int fboWidth, int fboHeight) {
        if (target != null) {
            target.dispose();
        }
        target = RenderTarget.create(fboWidth >> 1, fboHeight >> 1);
        shader = new TouchMapShaderProgram();
        ShaderManager.register(shader);
        shader.compile();
    }

    /**
     * Called internally by the stage to render the touch map and process queued touch events.
     *
     * @param context The rendering context
     */
    public void process(RenderingContext context) {
        if (target == null) {
            return;
        }
        context.bindTarget(target);
        context.clear(0, 0, 1);
        ShaderProgram shaderBackup = context.getShader();
        context.setShader(shader);
        for (Map.Entry<Integer, TouchButton> entry : buttons.entrySet()) {
            final TouchButton button = entry.getValue();
            shader.feedObjectId((entry.getKey() + 256) % 256);
            context.pushMatrix(button.popLatestMatrix());
            button.render(context, Renderable.FLAG_IGNORE_EFFECTS);
            context.popMatrix();
        }
        while (!processQueue.isEmpty()) {
            final TouchEvent event = processQueue.poll();
            pixelBuffer.position(0);
            GLES20.glReadPixels(
                    (int) event.x >> 1, target.getHeight() - ((int) event.y >> 1), 1, 1,
                    GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, pixelBuffer
            );
            dispatchQueue.offer(new Pair<>(buttons.get((int) pixelBuffer.get(0)), event));
        }
        context.bindTarget(null);
        if (show && Debug.isDebuggerConnected()) {
            TextureShaderProgram program = (TextureShaderProgram) TextureManager.getShaderProgram();
            context.setShader(program);
            context.setColorFilter(0.75f, 0.75f, 0.75f, 0.75f);
            context.pushMatrix();
            context.identity();
            program.feed(target.getTextureHandle());
            program.feedTexCoords(TextureShaderProgram.getDefaultTextureBuffer());
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
     * Called internally to dispatch all processed touch events to their corresponding buttons.
     */
    public void dispatch() {
        while (!dispatchQueue.isEmpty()) {
            Pair<TouchButton, TouchEvent> item = dispatchQueue.poll();
            TouchEvent event = item.second;

            if (item.first != null)
                item.first.internalTouch(event.action, event.pointerId);

            if (event.action == TouchEvent.EVENT_UP) {
                for (Map.Entry<Integer, TouchButton> entry : buttons.entrySet()) {
                    final TouchButton btn = entry.getValue();
                    entry.getValue().internalTouch(
                            btn == item.first ? TouchEvent.EVENT_UP : TouchEvent.EVENT_LEAVE,
                            event.pointerId
                    );
                }
            }
        }
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
     * Called internally to queue touch events.
     *
     * @param event The touch event to queue
     */
    public void onTouchEvent(TouchEvent event) {
        processQueue.offer(event);
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
     * Removes a button from this touch map
     *
     * @param button The button
     */
    void unregisterButton(TouchButton button) {
        if (button != null)
            buttons.remove(button.id);
    }
}
