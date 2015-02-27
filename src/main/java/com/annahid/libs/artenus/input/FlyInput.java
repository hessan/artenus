package com.annahid.libs.artenus.input;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.MotionEvent;

import com.annahid.libs.artenus.ui.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A subclass of {@link InputManager} that uses device tilting
 * as the direction knob, and {@link TapRegion} objects as the the action buttons.
 * Quick shaking of the device (as if one is hitting the air) also triggers the first
 * action button. Note that {@link TapRegion} is used in its isolated form in this
 * class and is not attached to the scene. The reason for this application is the
 * flexibility {@code TapRegion} objects provide for touch processing and their ease
 * of use. This class is still in development stage.</p>
 * @author Hessan Feghhi
 *
 */
@SuppressWarnings("UnusedDeclaration")
public class FlyInput extends InputManager {
	private static float vectorLength(float[] v) {
		return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
	}

	private final List<TapRegion> buttons = new ArrayList<>();
	private float[] angle = new float[]{0, 0, 0};
	private SensorManager sensorManager;
	private double lastTime;
	private int startId;
	private final List<Integer> keyIds = new ArrayList<>();

	private final SensorEventListener myListener = new SensorEventListener() {
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}

		public void onSensorChanged(SensorEvent event) {
			float[] v = event.values;

			holdKeyMap();

			if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
				releaseKeys(0xF);
				double curTime = ((double) System.currentTimeMillis()) / 1000.0f;

				for (int i = 0; i < 3; i++) {
					float delta = (float) Math.toDegrees(v[i]);
					if (Math.abs(delta) > 15.75) {
						angle[i] = angle[i] + (float) Math.toDegrees(v[i]) * (float) (curTime - lastTime);
						if (angle[i] > 12) angle[i] = 12f;
						if (angle[i] < -12) angle[i] = -12f;
					}
				}

				lastTime = curTime;

				if (angle[0] > 4) {
					direction.x = 1;
					direction.y = 0;
				} else if (angle[0] < -4) {
					direction.x = -1;
					direction.y = 0;
				}

				if (angle[1] > 4) {
					direction.x = 0;
					direction.y = -1;
				} else if (angle[1] < -4) {
					direction.x = 0;
					direction.y = 1;
				}

				angle[2] = 0.0f;
			} else {
				releaseKeys(0x10);

				if (vectorLength(v) > 13)
					pressKeys(InputManager.KEY_ACTION1);
			}

			releaseKeyMap();
		}
	};

	/**
	 * Registers this {@code FlyInput} manager to the given context and begins listening for sensor data.
	 */
	@Override
	public void register(Context context) {
		sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		sensorManager.registerListener(myListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_GAME);
		sensorManager.registerListener(myListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		lastTime = (float) System.currentTimeMillis() / 1000.0f;
	}

	@Override
	public void unregister() {
		sensorManager.unregisterListener(myListener);
	}

	public void cancelX() {
		angle[0] = 0.0f;
	}

	public void cancelY() {
		angle[1] = 0.0f;
	}

	/**
	 * Adds a new action button defined by a {@link TapRegion}. The buttons will be
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
	 * Handles touch events for {@code FlyInput}.
	 */
	@Override
	public void onTouchEvent(Stage stage, MotionEvent event) {
		final int action = event.getAction() & MotionEvent.ACTION_MASK;

		if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
			int pointerId;
			float x, y;

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
					break;
				}
			}

			releaseKeyMap();
		} else if (action == MotionEvent.ACTION_UP)
			checkRelease(event.getPointerId(0));
		else if (action == MotionEvent.ACTION_POINTER_UP)
			checkRelease(event.getPointerId(event.getActionIndex()));
	}

	private void checkRelease(int pointerId) {
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