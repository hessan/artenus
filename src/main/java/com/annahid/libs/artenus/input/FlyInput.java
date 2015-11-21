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

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.annahid.libs.artenus.Artenus;
import com.annahid.libs.artenus.core.Scene;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>A subclass of {@link GameInput} that uses device tilting
 * as the direction knob, and {@code TouchButton} objects as the the action buttons.</p>
 *
 * @author Hessan Feghhi
 */
@SuppressWarnings("UnusedDeclaration")
public class FlyInput extends GameInput {
    private static float vectorLength(float[] v) {
        return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    private float[] angle = new float[]{0, 0, 0};
    private SensorManager sensorManager;
    private double lastTime;
    private int startId;
    private final List<Integer> keyIds = new ArrayList<>(5);

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
                    pressKeys(GameInput.KEY_ACTION1);
            }

            releaseKeyMap();
        }
    };

    /**
     * Registers this {@code FlyInput} manager to the given context and begins listening for sensor data.
     */
    @Override
    public void onAttach(Scene scene) {
        sensorManager = (SensorManager)
                Artenus.getInstance().getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(
                myListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME
        );
        sensorManager.registerListener(
                myListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME
        );
        lastTime = (float) System.currentTimeMillis() / 1000.0f;
    }

    @Override
    public void onDetach(Scene scene) {
        sensorManager.unregisterListener(myListener);
    }

    public void cancelX() {
        angle[0] = 0.0f;
    }

    public void cancelY() {
        angle[1] = 0.0f;
    }
}