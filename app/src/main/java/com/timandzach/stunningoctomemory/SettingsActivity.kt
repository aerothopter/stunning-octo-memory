package com.timandzach.stunningoctomemory

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.NumberPicker
import androidx.appcompat.app.AppCompatActivity
import android.widget.Toast
import androidx.core.app.ComponentActivity
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.view.View


class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)


        val apply_button = this.findViewById(R.id.apply_settings_button) as Button
        val update_speed_text = this.findViewById(R.id.update_speed_editText) as NumberPicker
        val speed_threshold_text = this.findViewById(R.id.speed_threshold_editText) as NumberPicker

        if (update_speed_text != null) {
            update_speed_text.setMinValue(1)
            update_speed_text.setMaxValue(10)
            update_speed_text.setWrapSelectorWheel(true)
            update_speed_text.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
                val text = "Changed from $oldVal to $newVal"
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            })
        }

        if (speed_threshold_text != null) {
            speed_threshold_text.setMinValue(6)
            speed_threshold_text.setMaxValue(25)
            speed_threshold_text.setWrapSelectorWheel(true)
            speed_threshold_text.setOnValueChangedListener(NumberPicker.OnValueChangeListener { picker, oldVal, newVal ->
                val text = "Changed from $oldVal to $newVal"
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
            })
        }

        apply_button.setOnClickListener(View.OnClickListener { Toast.makeText( this, "Apply clicked", Toast.LENGTH_SHORT).show() })
    }
}