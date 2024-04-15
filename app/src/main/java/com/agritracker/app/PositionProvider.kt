/*
 * Copyright 2013 - 2022 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.agritracker.app

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location
import android.os.BatteryManager
import androidx.preference.PreferenceManager
import android.util.Log
import kotlin.math.abs

abstract class PositionProvider(
    protected val context: Context,
    protected val listener: PositionListener,
) {

    interface PositionListener {
        fun onPositionUpdate(position: Position)
        fun onPositionError(error: Throwable)
    }

    protected var preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    protected var deviceId = preferences.getString(MainFragment.KEY_DEVICE, "undefined")!!
    protected var interval = preferences.getString(MainFragment.KEY_INTERVAL, "600")!!.toLong() * 1000
    protected var idleInterval = preferences.getString(MainFragment.KEY_IDLE_INTERVAL, "3000")!!.toLong() * 1000
    protected var distance: Double = preferences.getString(MainFragment.KEY_DISTANCE, "0")!!.toInt().toDouble()
    protected var angle: Double = preferences.getString(MainFragment.KEY_ANGLE, "0")!!.toInt().toDouble()
    private var lastLocation: Location? = null
    private var acceleration = null
    private var vibration = false

    abstract fun startUpdates()
    abstract fun stopUpdates()
    abstract fun requestSingleLocation()

    protected fun processLocation(location: Location?) {

        if (location == null) {
            Log.i(TAG, "Location is null")
            return
        }
        val lastLocation = this.lastLocation
        if (isMeaningfulUpdate(location, lastLocation)) {
            Log.i(TAG, "Location is new")
            this.lastLocation = location
            listener.onPositionUpdate(
                    Position(deviceId, location, getBatteryStatus(context), Acceleration(acceleration, vibration))
            )
        } else {
            Log.i(TAG, "Location ignored")
        }
    }

    private fun isMeaningfulUpdate(newLocation: Location, lastLocation: Location?): Boolean {
        if (lastLocation !== null && newLocation.time - lastLocation.time <= idleInterval && newLocation.speed == 0F) return false
        return lastLocation == null
                || newLocation.time - lastLocation.time >= interval
                || distance > 0 && newLocation.distanceTo(lastLocation) >= distance
                || angle > 0 && abs(newLocation.bearing - lastLocation.bearing) >= angle
    }

    protected fun getBatteryStatus(context: Context): BatteryStatus {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        if (batteryIntent != null) {
            val level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 1)
            val status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            return BatteryStatus(
                level = level * 100.0 / scale,
                charging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL,
            )
        }
        return BatteryStatus()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = event.values[0]
            val y = event.values[1]
            val z = event.values[2]
            acceleration = Math.sqrt((x * x + y * y + z * z).toDouble())
            vibration = acceleration > VIBRATION_THRESHOLD
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not needed
    }

    companion object {
        private val TAG = PositionProvider::class.java.simpleName
        const val MINIMUM_INTERVAL: Long = 1000
        const val VIBRATION_THRESHOLD = 5.0
    }

}
