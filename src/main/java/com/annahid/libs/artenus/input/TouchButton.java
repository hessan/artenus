package com.annahid.libs.artenus.input;

import com.annahid.libs.artenus.core.RenderingContext;
import com.annahid.libs.artenus.core.Scene;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.FilteredEntity;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.entities.behavior.Transformable;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Handles touch events for the underlying entity.
 */
public class TouchButton extends FilteredEntity implements Button, Touchable {
    private static AtomicInteger idStore = new AtomicInteger(100);

    @SuppressWarnings("unused")
    public static final int INVALID_ID = 0;

    public TouchButton(Entity target) {
        super(target);
        objectId = (int) (byte) (idStore.incrementAndGet() % 256);

        if (objectId == 0)
            objectId++;

        if (!(target instanceof Transformable))
            throw new IllegalArgumentException("Target entity is not transformable.");

        if (!(target instanceof Renderable))
            throw new IllegalArgumentException(
                    "Target entity is not renderable. Artenus uses graphical" +
                            "representations of objects to track touch events."
            );

        if (target instanceof TouchButton)
            throw new IllegalArgumentException(
                    "Nested touch buttons are not supported by the framework."
            );
    }

    @Override
    public void onAttach(Scene scene) {
        super.onAttach(scene);
        scene.getTouchMap().registerButton(this);
    }

    @Override
    public void onDetach(Scene scene) {
        super.onDetach(scene);
        scene.getTouchMap().unregisterButton(this);
    }

    void internalTouch(int action, int pointerId) {
        if (action == TouchEvent.EVENT_DOWN) {
            if (!down) {
                down = true;
                downId = pointerId;

                if (listener != null) {
                    listener.onPress(this);
                }
            }
        } else if(action != TouchEvent.EVENT_MOVE && down && pointerId == downId) {
            if (listener != null) {
                listener.onRelease(this, action == TouchEvent.EVENT_LEAVE);
            }
            down = false;
            downId = -1;
        }
    }

    /**
     * This methods always returns false. Touch buttons do not participate in the normal touch event
     * pipeline. Their touch event is handled through the touch map associated with the scene.
     *
     * @param event Event information
     * @return       {@code false}
     */
    @Override
    public final boolean handleTouch(TouchEvent event) {
        return false;
    }

    private float[] latestMatrix = null;

    float[] popLatestMatrix() {
        float[] ret = latestMatrix;
        latestMatrix = null;
        return ret;
    }

    @Override
    public void render(RenderingContext ctx, int flags) {
        latestMatrix = ctx.getMatrix();
        if (target instanceof Renderable) {
            ((Renderable) target).render(ctx, flags);
        }
    }

    public int getId() {
        return objectId;
    }

    @Override
    public void setListener(ButtonListener listener) {
        this.listener = listener;
    }

    @Override
    public ButtonListener getListener() {
        return listener;
    }

    private int objectId;
    private boolean down = false;
    private int downId = -1;
    private ButtonListener listener = null;
}
