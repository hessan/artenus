package com.annahid.libs.artenus.ui;

import android.support.annotation.NonNull;
import android.view.MotionEvent;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.Entity;
import com.annahid.libs.artenus.entities.EntityCollection;
import com.annahid.libs.artenus.entities.behavior.Transformable;
import com.annahid.libs.artenus.graphics.sprites.SpriteEntity;
import com.annahid.libs.artenus.input.InputManager;

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
 *
 */
@SuppressWarnings("UnusedDeclaration")
public final class SlideMenu extends EntityCollection {
    public interface Listener {
        void onItemClicked(int itemIndex);
        void onItemChanged(int newItem, int prevItem);
    }
	
	/**
	 * Constructs a {@code SlideMenu} with the given information for the given scene.
	 *
	 * @param scene	The scene to add this menu to
	 * @param center	The center location of the menu
	 * @param itemDistance	The distance between items
	 * @param height	The height of the menu. This is used for touch handling
	 */
	public SlideMenu(Scene scene, Point2D center, float itemDistance, float height) {
        super(scene);
		dist = itemDistance;
		h = height;
		th = height;
		startX = -1;
		startPos = -1;
		currentPos = 0;
		sel = -1;
		centerX = center.x;
		centerY = center.y;
	}

	/**
	 * Assigns a new event listener to listen to item selection events.
	 *
	 * @param listener	Event listener to be added
	 */
    public void addListener(Listener listener) {
        itemChangeListeners.add(new WeakReference<>(listener));
    }

	/**
	 * Removes an event listener currently assigned to this slide menu.
	 *
	 * @param listener	Event listener to be removed
	 */
    public void removeListener(@NonNull Listener listener) {
        for (Iterator<WeakReference<Listener>> iterator = itemChangeListeners.iterator();
             iterator.hasNext(); ) {
            WeakReference<Listener> weakRef = iterator.next();

            if (weakRef.get() == listener)
            {
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
	 * @param tapHeight	The height of tap focus
	 */
	public void setTapFocus(float tapHeight) {
		th = tapHeight;
	}
	
	/**
	 * Adds a menu item in the form of an entity.
	 *
	 * @param entity	The menu item
	 */
	@Override
	public final boolean add(Entity entity) {
		if(!(entity instanceof Transformable))
			throw new IllegalArgumentException("Menu items must be transformable.");

		final boolean ret = super.add(entity);

		if(ret) {
			((Transformable)entity).setPosition(
					centerX + currentPos + dist * (size() - 1), centerY
			);

			if (sel < 0)
				sel = 0;

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
		currentPos = 0;
	}
	
	/**
	 * Advances animations for the amount of time specified.
	 *
	 * @param elapsedTime	Elapsed time since last frame
	 */
    @Override
	public final void advance(float elapsedTime) {
        super.advance(elapsedTime);

		float targetPos = -sel * dist;
		
		if(currentPos != targetPos && startX < 0) {
			float speed = targetPos - currentPos;
			currentPos += speed * elapsedTime * 4;
		}

		int i = 0;

		for(Entity entity : this) {
			final float alpha = ((SpriteEntity)entity).getAlpha();

			((Transformable)entity).setPosition(centerX + currentPos + dist * i, centerY);
			
			if(i == sel) {
				((SpriteEntity)entity).setAlpha(Math.min(1, alpha + elapsedTime * 2));
				dotV[i] = Math.min(1, dotV[i] + elapsedTime * 2);
			}
			else {
				((SpriteEntity)entity).setAlpha(Math.max(0.75f, alpha - elapsedTime * 2));
				dotV[i] = Math.max(0, dotV[i] - elapsedTime * 2);
			}

			((Transformable)entity).setScale(alpha, alpha);
			i++;
		}
	}
	
	/**
	 * This method gives you information you require to set up indicators. It provides
	 * transparency information for each indicator. An indicator has full transparency
	 * if the corresponding menu item is completely in view and starts to gradually
	 * decrease to zero when it goes out of focus.
	 *
	 * @param index	Menu item index
	 * @return	Indicator transparency
	 */
	public final float getIndicatorAlpha(int index) {
		return dotV[index];
	}
	
	/**
	 * Handles a touch event for this {@code SlideMenu}. It also determines whether
	 * and item has been tapped.
	 *
	 * @param action	The touch event that has been triggered
	 * @param x	The x coordinate of the touch event
	 * @param y	The y coordinate of the touch event
	 * @return	{@code true} if a menu item has been tapped, {@code false} otherwise
	 */
	@Override
	public final boolean handleTouch(int action, int pointerId, float x, float y) {
		if(action == InputManager.EVENT_DOWN) {
			if(y > centerY - h / 2 && y < centerY + h / 2) {
				startX = x;
				startPos = currentPos;
			}
		}
		else if(action == InputManager.EVENT_MOVE) {
			if(Math.abs(x - startX) > 12 && startX > 0)
				currentPos = startPos + x - startX;
		} else if(action == MotionEvent.ACTION_UP && startX > 0) {
			if(Math.abs(x - startX) < 18 && y > centerY - th / 2 && y < centerY + th / 2) {
                publishItemClick(sel);
                return true;
            }
            else {
                final int prevSel = sel;

				sel = Math.min(size() - 1, Math.max(0,
                        (int)(-currentPos + dist / 2) / (int)dist)
                );

                if(sel != prevSel)
                    publishItemChange(sel, prevSel);
			}
			
			startX = -1;
		}
		
		return false;
	}
	
	/**
	 * Navigates to the menu item specified and focuses that item.
	 *
	 * @param index	Item index
	 */
	public final void setSelectedItem(int index) {
		sel = index;
	}
	
	/**
	 * Gets the currently selected (focused) menu item.
	 *
	 * @return	Item index
	 */
	public final int getSelectedItem() {
		return sel;
	}

    private void publishItemChange(int newItem, int prevItem) {
        for(WeakReference<Listener> listener : itemChangeListeners)
            if(listener.get() != null)
                listener.get().onItemChanged(newItem, prevItem);
    }

    private void publishItemClick(int item) {
        for(WeakReference<Listener> listener : itemChangeListeners)
            if(listener.get() != null)
                listener.get().onItemClicked(item);
    }

	private float dotV[] = {0, 0};
	private float centerX, centerY;
	private float dist, h, th;
	private float startX, startPos, currentPos;
	private int sel;

	private List<WeakReference<Listener>> itemChangeListeners = new LinkedList<>();
}
