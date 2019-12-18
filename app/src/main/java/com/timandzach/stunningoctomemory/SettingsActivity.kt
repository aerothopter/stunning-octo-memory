package com.timandzach.stunningoctomemory

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.view.View


class SettingsActivity : AppCompatActivity() {
    val ONE_MINUTE = 60000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)


        val apply_button = this.findViewById(R.id.apply_settings_button) as Button
        val update_speed_picker = this.findViewById(R.id.update_speed_editText) as NumberPicker
        val speed_threshold_picker = this.findViewById(R.id.speed_threshold_editText) as NumberPicker
        val stop_threshold_picker = this.findViewById(R.id.stop_threshold_editText) as NumberPicker

        if (update_speed_picker != null) {
            update_speed_picker.setMinValue(1)
            update_speed_picker.setMaxValue(20)
            update_speed_picker.setWrapSelectorWheel(true)
        }

        if (speed_threshold_picker != null) {
            speed_threshold_picker.setMinValue(6)
            speed_threshold_picker.setMaxValue(35)
            speed_threshold_picker.setWrapSelectorWheel(true)
        }

        if (stop_threshold_picker != null) {
            stop_threshold_picker.setMinValue(1)
            stop_threshold_picker.setMaxValue(30)
            stop_threshold_picker.setWrapSelectorWheel(true)
        }

        apply_button.setOnClickListener(View.OnClickListener {
            Toast.makeText( this, "Apply clicked", Toast.LENGTH_SHORT).show()
            if (speed_threshold_picker.value <= stop_threshold_picker.value) {
                stop_threshold_picker.value = speed_threshold_picker.value - 1
            }

            SpeedNotifier.instance.speedThreshold = speed_threshold_picker.value
            SpeedNotifier.instance.stoppedSpeed = stop_threshold_picker.value

            restartSpeedNotifications(ONE_MINUTE / update_speed_picker.value)
        })
    }

    fun restartSpeedNotifications(updateInterval : Int) {
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)

        serviceIntent.putExtra("UpdateSpeed", updateInterval)
        startService(serviceIntent)
    }
}