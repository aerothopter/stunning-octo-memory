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
import android.view.Menu
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.TextView

class MainActivity : Activity(), SpeedListener {

    val UNIQUE_REQUEST_FINE_LOCATION_ID = 780917890

    lateinit var speedNotifier : SpeedNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.updateSpeed(0.0, 0.0)

        //Check that we have permission to access the user's location. Request that permission if needed
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), UNIQUE_REQUEST_FINE_LOCATION_ID)

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
