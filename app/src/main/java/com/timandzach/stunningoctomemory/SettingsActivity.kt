package com.timandzach.stunningoctomemory

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import android.view.View


class SettingsActivity : AppCompatActivity() {
    //One minute in MS
    val ONE_MINUTE = 60000

    val DEFAULT_SPEED_THRESHOLD = 6
    val DEFAULT_STOP_THRESHOLD = 1
    val DEFAULT_UPDATES_PER_MINUTE = 6

    //The filename for our SharedPreferences file
    val PREFS_FILENAME = "com.timandzach.stunningoctomemory.prefs"

    var prefs: SharedPreferences? = null

    var speedThreshold : Int = 6
    var stopThreshold : Int = 1
    var updatesPerMinue : Int = 6

    val SPEED_THRESHOLD_KEY = "speedThreshold"
    val STOP_THRESHOLD_KEY = "stopThreshold"
    val UPDATES_PER_MINUTE_KEY = "updatesPerMinute"

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

        /* Set the NumberPickers to their value stored in the SharedPreferences or to their default
         * values
         */
        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        speedThreshold = prefs!!.getInt(SPEED_THRESHOLD_KEY,(DEFAULT_SPEED_THRESHOLD))
        stopThreshold = prefs!!.getInt(STOP_THRESHOLD_KEY,(DEFAULT_STOP_THRESHOLD))
        updatesPerMinue = prefs!!.getInt(UPDATES_PER_MINUTE_KEY,(DEFAULT_UPDATES_PER_MINUTE))

        update_speed_picker.value = updatesPerMinue
        speed_threshold_picker.value = speedThreshold
        stop_threshold_picker.value = stopThreshold


        /* When the apply button is pressed apply all the settings and record the values in
         * the SharedPreferences file
         */
        apply_button.setOnClickListener(View.OnClickListener {
            Toast.makeText( this, "Apply clicked", Toast.LENGTH_SHORT).show()
            if (speed_threshold_picker.value <= stop_threshold_picker.value) {
                stop_threshold_picker.value = speed_threshold_picker.value - 1
            }

            SpeedNotifier.instance.speedThreshold = speed_threshold_picker.value
            SpeedNotifier.instance.stoppedSpeed = stop_threshold_picker.value

            restartSpeedNotifications(ONE_MINUTE / update_speed_picker.value)

            val editor = this.prefs!!.edit()
            editor.putInt(SPEED_THRESHOLD_KEY, speed_threshold_picker.value)
            editor.putInt(STOP_THRESHOLD_KEY, stop_threshold_picker.value)
            editor.putInt(UPDATES_PER_MINUTE_KEY, update_speed_picker.value)

            editor.apply()
        })
    }

    fun restartSpeedNotifications(updateInterval : Int) {
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)

        serviceIntent.putExtra("UpdateSpeed", updateInterval)
        startService(serviceIntent)
    }
}