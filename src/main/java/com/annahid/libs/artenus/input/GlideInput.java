package com.annahid.libs.artenus.input;

import android.content.Context;
import android.view.MotionEvent;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.ui.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>This class is an implementation of {@link InputManager} that uses slide motions
 * as the direction knob and {@link com.annahid.libs.artenus.input.TapRegion} objects as action buttons. Note that
 * {@link com.annahid.libs.artenus.input.TapRegion} is used in its isolated form in this class and is not attached
 * to the scene. The reason for this application is the flexibility {@code TapRegion}
 * objects provide for touch processing and their ease of use.</p>
 * <p>The way the directional knob works in {@code GlideInput} is that whenever the
 * user touches a point in the screen where there is no action button, that point
 * becomes the center of the knob, or the reference point. Now sliding away from the
 * reference point indicates the direction of choice. If the user slides their finger
 * to the left side of the reference point (the first point they touched), the knob
 * will report a left direction and same holds for every other direction. The knob
 * goes back to neutral as soon as the gesture is finished.</p>
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("unused")
public final class GlideInput extends InputManager {
	private final Point2D reference = new Point2D(0, 0);
	private final List<TapRegion> buttons = new ArrayList<>();
	private final List<Integer> keyIds = new ArrayList<>();
	private int startId;
	private int threshold;

	/**
	 * Adds a new action button defined by a {@link com.annahid.libs.artenus.input.TapRegion}. The buttons will be
	 * assigned to the button indices in the order of addition. Buttons are effective
	 * throughout the {@link com.annahid.libs.artenus.ui.Stage}, so you should be careful to clear the buttons
	 * whenever they are no longer needed and add them back when they are needed again
	 * (for example add them in the game scene and remove them when the game is over).
	 *
	 * @param button The button to be added.
	 * @see com.annahid.libs.artenus.ui.Stage
	 */
	public void addButton(TapRegion button) {
		buttons.add(button);
		keyIds.add(-1);
	}

	/**
	 * Clears all the buttons currently added to this input manager.
	 */
	public void clearButtons() {
		buttons.clear();
		keyIds.clear();
	}

	/**
	 * Gets the x coordinate of the reference point, which is the first
	 * point the user touched to trigger the direction knob. This point
	 * is maintained until the next gesture.
	 *
	 * @return The x component of the reference point.
	 */
	public float getRefX() {
		return reference.x;
	}

	/**
	 * Gets the y coordinate of the reference point, which is the first
	 * point the user touched to trigger the direction knob. This point
	 * is maintained until the next gesture.
	 *
	 * @return The y component of the reference point.
	 */
	public float getRefY() {
		return reference.y;
	}

	/**
	 * Registers this {@code GlideInput} manager to the given context.
	 */
	@Override
	public void register(Context context) {
		threshold = (int) (10 * context.getResources().getDisplayMetrics().density + 0.5f);
	}

	@Override
	public void unregister() { }

	/**
	 * Handles touch events for {@code GlideInput}.
	 */
	@Override
	public void onTouchEvent(Stage stage, MotionEvent event) {
		final int action = event.getAction() & MotionEvent.ACTION_MASK;

		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			int pointerId;
			float x, y;
			boolean keysPressed = false;

			if (action == MotionEvent.ACTION_DOWN) {
				pointerId = event.getPointerId(0);
				x = stage.screenToStageX(event.getX());
				y = stage.screenToStageX(event.getY());
			} else {

				final int pointerIndex = event.getActionIndex();
				pointerId = event.getPointerId(pointerIndex);
				x = stage.screenToStageX(event.getX(pointerIndex));
				y = stage.screenToStageY(event.getY(pointerIndex));
			}

			holdKeyMap();

			for (int i = 0; i < buttons.size(); i++) {
				final TapRegion rct = buttons.get(i);

				if (rct.hitTest(x, y)) {
					pressKeys(1 << (4 + i));
					keyIds.set(i, pointerId);
					keysPressed = true;
					break;
				}
			}

			releaseKeyMap();

			if (startId < 0 && !keysPressed) {
				startId = pointerId;
				reference.x = x;
				reference.y = y;
			}
		} else if (action == MotionEvent.ACTION_UP)
			checkRelease(event.getPointerId(0));
		else if (action == MotionEvent.ACTION_POINTER_UP)
			checkRelease(event.getPointerId(event.getActionIndex()));
		else {
			if (startId >= 0) {
				final int pointerIndex = event.findPointerIndex(startId);

				if (pointerIndex >= 0) {
					holdKeyMap();
					direction.x = stage.screenToStageX(event.getX(pointerIndex)) - reference.x;
					direction.y = stage.screenToStageY(event.getY(pointerIndex)) - reference.y;
					if (Math.abs(direction.x) < threshold) direction.x = 0;
					if (Math.abs(direction.y) < threshold) direction.y = 0;

					if (direction.x != 0 || direction.y != 0) {
						float size = (float) Math.sqrt(direction.x * direction.x + direction.y * direction.y);
						direction.x /= size;
						direction.y /= size;
					}

					releaseKeyMap();
				}
			}
		}
	}

	private void checkRelease(int pointerId) {
		if (pointerId == startId) {
			startId = -1;
			holdKeyMap();
			direction.setZero();
			releaseKeyMap();
		} else {
			holdKeyMap();

			for (int i = 0; i < buttons.size(); i++) {
				if (keyIds.get(i) == pointerId) {
					keyIds.set(i, -1);
					releaseKeys(1 << (4 + i));
					break;
				}
			}

			releaseKeyMap();
		}
	}
}
