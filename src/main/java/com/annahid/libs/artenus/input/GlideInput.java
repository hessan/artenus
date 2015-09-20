package com.annahid.libs.artenus.input;

import com.annahid.libs.artenus.data.Point2D;
import com.annahid.libs.artenus.entities.behavior.Touchable;
import com.annahid.libs.artenus.ui.Scene;
import com.annahid.libs.artenus.ui.Stage;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>A subclass of {@link InputManager} that uses slide motions
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
public final class GlideInput extends InputManager implements Touchable {
	private final Point2D reference = new Point2D(0, 0);
	private final Map<Integer, TouchButton> buttons = new HashMap<>();
	private int startId;
	private int threshold;
	private boolean keysPressed;
	private Stage stage;

	/**
	 * Assigns a touch button to an action key for this input manager.
	 *
	 * @param key The action key to associate with
	 * @param button The touch button to be added (or null to disassociate)
	 * @see com.annahid.libs.artenus.ui.Stage
	 */
	public void setButton(final int key, TouchButton button) {
		if(button == null) {
			button = buttons.get(key);

			if(button != null) {
				button.setListener(null);
				buttons.remove(key);
			}

			return;
		}

		button.setListener(new TouchButton.Listener() {
			@Override
			public void onPress(float relativeX, float relativeY) {
				holdKeyMap();
				pressKeys(key);
				releaseKeyMap();
				keysPressed = true;
			}

			@Override
			public void onClick(float relativeX, float relativeY) {
				onRelease();
			}

			@Override
			public void onRelease() {
				holdKeyMap();
				releaseKeys(key);
				releaseKeyMap();
				keysPressed = false;
			}
		});

		buttons.put(key, button);
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
	public void onAttach(Scene scene) {
		stage = scene.getStage();
		threshold = (int) (10
				* stage.getContext().getResources().getDisplayMetrics().density + 0.5f);
	}

	@Override
	public void onDetach(Scene scene) {
		stage = null;
	}

	/**
	 * Handles touch events for {@code GlideInput}.
	 */
	@Override
	public boolean handleTouch(int action, int pointerId, float x, float y) {
		if (action == InputManager.EVENT_DOWN) {
			if (startId < 0 && !keysPressed) {
				startId = pointerId;
				reference.x = x;
				reference.y = y;
			}
		} else if (action == InputManager.EVENT_UP) {
			checkRelease(pointerId);
		} else {
			if (startId >= 0) {
				holdKeyMap();
				direction.x = x - reference.x;
				direction.y = y - reference.y;
				if (Math.abs(direction.x) < threshold) direction.x = 0;
				if (Math.abs(direction.y) < threshold) direction.y = 0;

				if (direction.x != 0 || direction.y != 0) {
					float size = (float)
							Math.sqrt(direction.x * direction.x + direction.y * direction.y);
					direction.x /= size;
					direction.y /= size;
				}
				releaseKeyMap();
			}
		}

		try {
			Thread.sleep(5);
		} catch (InterruptedException ex) {
			// Do nothing
		}

		return false;
	}

	private void checkRelease(int pointerId) {
		if (pointerId == startId) {
			startId = -1;
			holdKeyMap();
			direction.setZero();
			releaseKeyMap();
		}
	}
}
