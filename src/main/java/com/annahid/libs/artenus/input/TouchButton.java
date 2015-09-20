package com.annahid.libs.artenus.input;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.FilteredEntity;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.entities.behavior.Transformable;

/**
 * Handles touch events for the underlying entity.
 */
public class TouchButton extends FilteredEntity implements Touchable {

    public interface Listener {
        void onPress(float relativeX, float relativeY);
        void onClick(float relativeX, float relativeY);
        void onRelease();
    }

    public TouchButton(Entity target, TapRegion tapRegion) {
        super(target);
        this.tapRegion = tapRegion;

        if(!(target instanceof Transformable))
            throw new IllegalArgumentException("Target entity is not transformable.");
    }

    @Override
    public boolean handleTouch(int action, int pointerId, float x, float y) {
        if(action == InputManager.EVENT_DOWN) {
            final Point2D trans = ((Transformable)target).getPosition();

            if(!down && tapRegion.hitTest(trans.x, trans.y, x, y)) {
                down = true;
                downId = pointerId;

                if(listener != null) {
                    listener.onPress(x - trans.x, y - trans.y);
                }

                return true;
            }
        }
        else if(action == InputManager.EVENT_UP) {
            if(down) {
                final Point2D trans = ((Transformable)target).getPosition();

                if(tapRegion.hitTest(trans.x, trans.y, x, y) && pointerId == downId) {
                    if(listener != null) {
                        listener.onClick(x - trans.x, y - trans.y);
                    }
                }
                else {
                    if(listener != null) {
                        listener.onRelease();
                    }
                }

                if(pointerId == downId) {
                    down = false;
                    downId = -1;
                }
            }
        }

        return false;
    }

    public void setListener(TouchButton.Listener listener) {
        this.listener = listener;
    }

    public TouchButton.Listener getListener() {
        return listener;
    }

    private TapRegion tapRegion;
    private boolean down = false;
    private int downId = -1;
    private TouchButton.Listener listener = null;
}
