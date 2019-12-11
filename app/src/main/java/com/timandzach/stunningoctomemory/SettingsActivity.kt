package com.timandzach.stunningoctomemory

import android.app.Activity
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import androidx.preference.*


class SettingsActivity : AppCompatActivity() {

    val PREFS_FILENAME = "com.timandzach.stunningoctomemory.prefs"

    var prefs: SharedPreferences? = null

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied in
     * OnSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (supportFragmentManager.findFragmentById(android.R.id.content) == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, MySettingsFragment())
                .commit()
        }

    }

    /**
     * Stop the location service, unregister for location updates, and exit the app
     *
     */
    override fun finish() {
        super.finish()

    }
}

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.screen_preferences, rootKey)
    }
}
