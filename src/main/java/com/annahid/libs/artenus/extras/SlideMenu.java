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

package com.annahid.libs.artenus.extras;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.EntityCollection;
import com.annahid.libs.artenus.entities.behavior.Behaviors;
import com.annahid.libs.artenus.entities.behavior.Renderable;
import com.annahid.libs.artenus.input.TouchEvent;
import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.behavior.Transformable;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Provides the user interface for horizontal menus that use slide action
 * to navigate. It takes sprites as menu items and handles scaling and moving them
 * as necessary. At any point you can get the currently focused item.
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public final class SlideMenu extends EntityCollection {

    private float dotV[] = { 0, 0 };

    private float dist;

    private float h;

    private float th;

    /**
     * Hold the first touched x position that initiated sliding.
     */
    private float startX;

    /**
     * Hold the location value when sliding began.
     */
    private float startPos;

    /**
     * Holds current scroll location.
     */
    private float currentLoc;

    /**
     * Holds the center position for the selected item.
     */
    private float centerX;

    /**
     * Holds selected item index.
     */
    private int sel;

    /**
     * Contains event handlers currently listening to events from this slide menu.
     */
    private List<WeakReference<Listener>> itemChangeListeners = new LinkedList<>();

    /**
     * Creates a {@code SlideMenu} with the given information for the given scene.
     *
     * @param center       The center location of the menu
     * @param itemDistance The distance between items
     * @param height       The height of the menu. This is used for touch handling
     */
    public SlideMenu(Point2D center, float itemDistance, float height) {
        dist = itemDistance;
        h = height;
        th = height;
        startX = -1;
        startPos = -1;
        centerX = center.x;
        currentLoc = 0;
        sel = -1;
        super.setPosition(0, center.y);
    }

    /**
     * Assigns a new event listener to listen to item selection events.
     *
     * @param listener Event listener to be added
     */
    public void addListener(Listener listener) {
        itemChangeListeners.add(new WeakReference<>(listener));
    }

    /**
     * Removes an event listener currently assigned to this slide menu.
     *
     * @param listener Event listener to be removed
     */
    public void removeListener(@NonNull Listener listener) {
        for (Iterator<WeakReference<Listener>> iterator = itemChangeListeners.iterator();
             iterator.hasNext(); ) {
            WeakReference<Listener> weakRef = iterator.next();

            if (weakRef.get() == listener) {
                iterator.remove();
                return;
            }
        }
    }

    /**
     * Adjusts the tap focus for this {@code SlideMenu}. Tap focus is the vertical
     * distance inside which a touch can be considered as a "tap" if there is not
     * much horizontal motion.
     *
     * @param tapHeight The height of tap focus
     */
    public void setTapFocus(float tapHeight) {
        th = tapHeight;
    }

    /**
     * Adds a menu item in the form of an entity.
     *
     * @param entity The menu item
     */
    @Override
    public final boolean add(Entity entity) {
        if (!entity.hasBehavior(Behaviors.TRANSFORMABLE))
            throw new IllegalArgumentException("Menu items must be transformable.");

        final boolean ret = super.add(entity);

        if (ret) {
            ((Transformable) entity).setPosition(centerX + currentLoc + dist * (size() - 1), 0);

            if (sel < 0) sel = 0;
            if (dotV.length < size()) {
                float[] newDotV = new float[size()];
                System.arraycopy(dotV, 0, newDotV, 0, dotV.length);
                dotV = newDotV;
            }
        }

        return ret;
    }

    /**
     * Clears all menu items from this slide menu.
     */
    @Override
    public void clear() {
        super.clear();
        sel = -1;
        currentLoc = 0;
        super.setPosition(currentLoc, getPosition().y);
    }

    /**
     * Advances animations for the amount of time specified.
     *
     * @param elapsedTime Elapsed time since last frame
     */
    @Override
    public final void advance(float elapsedTime) {
        super.advance(elapsedTime);

        float targetPos = -sel * dist;

        if (currentLoc != targetPos && startX < 0) {
            float speed = targetPos - currentLoc;
            currentLoc += speed * elapsedTime * 4;
        }

        super.setPosition(currentLoc, getPosition().y);

        int i = 0;
        for (Entity entity : this) {
            final float alpha = ((Renderable) entity).getAlpha();

            if (i == sel) {
                ((Renderable) entity).setAlpha(Math.min(1, alpha + elapsedTime * 2));
                dotV[i] = Math.min(1, dotV[i] + elapsedTime * 2);
            } else {
                ((Renderable) entity).setAlpha(Math.max(0.75f, alpha - elapsedTime * 2));
                dotV[i] = Math.max(0, dotV[i] - elapsedTime * 2);
            }

            ((Transformable) entity).setScale(alpha, alpha);
            i++;
        }
    }

    /**
     * Provides information required to set up indicators. It provides transparency information for
     * each indicator. An indicator has full transparency if the corresponding menu item is
     * completely in view and starts to gradually decrease to zero when it goes out of focus.
     *
     * @param index Menu item index
     *
     * @return Indicator transparency
     */
    public final float getIndicatorAlpha(int index) {
        return dotV[index];
    }

    /**
     * Handles a touch event for this {@code SlideMenu}. It also determines whether
     * and item has been tapped.
     *
     * @param event Event information
     *
     * @return {@code true} if a menu item has been tapped, {@code false} otherwise
     */
    @Override
    public final boolean handleTouch(TouchEvent event) {
        final float x = event.getX(), y = event.getY();
        final float centerY = getPosition().y;

        if (event.getAction() == TouchEvent.EVENT_DOWN) {
            if (y > centerY - h / 2 && y < centerY + h / 2) {
                startX = x;
                startPos = currentLoc;
            }
        } else if (event.getAction() == TouchEvent.EVENT_MOVE) {
            if (Math.abs(x - startX) > 12 && startX > 0)
                currentLoc = startPos + x - startX;
        } else if (event.getAction() == MotionEvent.ACTION_UP && startX > 0) {
            if (Math.abs(x - startX) < 18 && y > centerY - th / 2 && y < centerY + th / 2) {
                publishItemClick(sel);
                return true;
            } else {
                final int prevSel = sel;

                sel = Math.min(
                        size() - 1,
                        Math.max(0, (int) (-currentLoc + dist / 2) / (int) dist)
                );

                if (sel != prevSel)
                    publishItemChange(sel, prevSel);
            }

            startX = -1;
        }

        return false;
    }

    /**
     * Gets the currently selected (focused) menu item.
     *
     * @return Item index
     */
    public final int getSelectedItem() {
        return sel;
    }

    /**
     * Navigates to the menu item specified and focuses that item.
     *
     * @param index Item index
     */
    public final void setSelectedItem(int index) {
        sel = index;
    }

    /**
     * Does nothing. Changing position is not supported by slide menu.
     *
     * @param position Position (not used)
     */
    @Override
    public void setPosition(Point2D position) {
        // Not supported
    }

    /**
     * Does nothing. Changing position is not supported by slide menu.
     *
     * @param x x value (not used)
     * @param y y value (not used)
     */
    @Override
    public void setPosition(float x, float y) {
        // Not supported
    }

    private void publishItemChange(int newItem, int prevItem) {
        for (WeakReference<Listener> listener : itemChangeListeners)
            if (listener.get() != null)
                listener.get().onItemChanged(newItem, prevItem);
    }

    private void publishItemClick(int item) {
        for (WeakReference<Listener> listener : itemChangeListeners)
            if (listener.get() != null)
                listener.get().onItemClicked(item);
    }

    /**
     * Interface for event handlers that listen to slide menu events.
     */
    public interface Listener {
        /**
         * Called when an item has been clicked.
         *
         * @param itemIndex The zero-based index of the clicked item
         */
        void onItemClicked(int itemIndex);

        /**
         * Called when a new item is slid into selection.
         *
         * @param newItem  Newly selected item
         * @param prevItem Previously selected item
         */
        void onItemChanged(int newItem, int prevItem);
    }
}
