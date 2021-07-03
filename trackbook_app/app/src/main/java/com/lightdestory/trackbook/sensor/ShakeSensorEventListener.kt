package com.lightdestory.trackbook.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.EditText
import android.widget.Toast
import kotlin.math.sqrt

class ShakeSensorEventListener(val context: Context, var counter: Int, val editText: EditText) : SensorEventListener {
    private var mAccel: Float = 0.00f// Device acceleration without gravity
    private var mAccelCurrent : Float = SensorManager.GRAVITY_EARTH // Device acceleration with gravity included
    private var mAccelLast : Float = SensorManager.GRAVITY_EARTH // Last detected acceleration with gravity included
    private val SHAKE_TRIGGER: Int = 8
    override fun onSensorChanged(event: SensorEvent?) {
        if(event!=null){
            val x: Float = event.values[0]
            val y: Float = event.values[1]
            val z: Float = event.values[2]
            mAccelLast = mAccelCurrent
            mAccelCurrent = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
            val delta: Float = mAccelCurrent - mAccelLast
            mAccel = mAccel * 0.9f + delta // perform low-cut filter
            if(mAccel > SHAKE_TRIGGER) {
                editText.setText((++counter).toString())
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}