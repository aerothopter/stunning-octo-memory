package com.timandzach.stunningoctomemory

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import java.util.Formatter
import java.util.Locale

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.Menu
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : Activity(), SpeedListener {

    val UNIQUE_REQUEST_FINE_LOCATION_ID = 780917890
    val PREFS_FILENAME = "com.timandzach.stunningoctomemory.prefs"
    var prefs: SharedPreferences? = null

    var latitude = 0.0
    var longitude = 0.0

    lateinit var speedNotifier : SpeedNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setting up the shared preferences file and pulling old values
        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        this.latitude = Double.fromBits(prefs!!.getLong("latitude",(0.0).toBits()))
        this.longitude = Double.fromBits(prefs!!.getLong("longitude",(0.0).toBits()))


        this.updateSpeed(this.latitude,this.longitude)

        //Check that we have permission to access the user's location. Request that permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), UNIQUE_REQUEST_FINE_LOCATION_ID)

            return
        }

        speedNotifier = SpeedNotifier(this)

        speedNotifier.register(this)
    }

    //If we get permission to access the uesr's location, register to get location updates
    //TODO: Could be made more robust, but we shouldn't ever have more than one permission being granted
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == UNIQUE_REQUEST_FINE_LOCATION_ID) {
            if (permissions.size > 0 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    speedNotifier = SpeedNotifier(this)

                    speedNotifier.register(this)
                }
            }
        }
    }

    override fun finish() {
        super.finish()
        System.exit(0)
    }

    override fun updateSpeed(latitude: Double, longitude: Double) {

        // first we store the value locally in case
        this.latitude = latitude
        this.longitude = longitude

        // we write to shared prefs for later
        val editor = this.prefs!!.edit()
        editor.putLong("latitude", this.latitude.toBits())
        editor.putLong("longitude", this.longitude.toBits())
        editor.apply()

        // then we format to write to screen
        val fmt = Formatter(StringBuilder())
        fmt.format(Locale.US, "%5.1f,%5.1f", latitude, longitude)
        var strCurrentSpeed = fmt.toString()
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0')

        var strUnits = "lat/long"

        val txtCurrentSpeed = this.findViewById(R.id.txtCurrentSpeed) as TextView
        txtCurrentSpeed.text = "$strCurrentSpeed $strUnits"
    }

    companion object {

        internal var UNIQUE_PERMISSIONS_ID = 75213
    }


}
