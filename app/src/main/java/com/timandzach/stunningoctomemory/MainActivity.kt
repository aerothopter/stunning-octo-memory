package com.timandzach.stunningoctomemory

import android.os.Bundle
import java.util.Formatter
import java.util.Locale
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.app.Activity
import android.content.Intent
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class MainActivity : Activity(), SpeedListener {

    val UNIQUE_REQUEST_FINE_LOCATION_ID = 780917890
    var service_running = false

    lateinit var speedNotifier : SpeedNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.updateSpeed(0.0, 0.0, 0.0f, false, 0, 0)

        //Check that we have permission to access the user's location. Request that permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), UNIQUE_REQUEST_FINE_LOCATION_ID)

            val txtCurrentSpeed = this.findViewById(R.id.txtCurrentSpeed) as TextView
            txtCurrentSpeed.text = "This application requires access to the user's location"
            return
        }

        initSpeedNotifications()
    }

    //If we get permission to access the uesr's location and background location, register to get location updates
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        var resultIndex = 0

        for (p in permissions) {
            if (p == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[resultIndex] == PackageManager.PERMISSION_GRANTED) {
                    initSpeedNotifications()

                    return
                } else {
                    val txtCurrentSpeed = this.findViewById(R.id.txtCurrentSpeed) as TextView
                    txtCurrentSpeed.text = "This application requires access to the user's location"
                }
            }
            else {
                resultIndex++
            }
        }
    }

    fun initSpeedNotifications () {
        if (!service_running) {
            val serviceIntent = Intent(this, LocationService::class.java)
            startService(serviceIntent)

            speedNotifier = SpeedNotifier(this)

            speedNotifier.register(this)

            service_running = true
        }
    }

    override fun finish() {
        super.finish()

        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)

        speedNotifier.unregister(this)

        service_running = false

        System.exit(0)
    }

    override fun updateSpeed(latitude: Double, longitude: Double, speed : Float, driving : Boolean, numBroadcasts : Int, numReceives : Int) {
        val fmt = Formatter(StringBuilder())
        fmt.format(Locale.US, "%5.6f,%5.6f", latitude, longitude)
        var strCurrentSpeed = fmt.toString()
        strCurrentSpeed = strCurrentSpeed.replace(' ', '0')

        var strUnits = "lat/long"

        val txtCurrentSpeed = this.findViewById(R.id.txtLatLong) as TextView
        txtCurrentSpeed.text = "$strCurrentSpeed $strUnits"

        val txtSpeed = this.findViewById(R.id.txtCurrentSpeed) as TextView
        txtSpeed.text = speed.toString()

        val txtDriving = this.findViewById(R.id.txtDriving) as TextView
        if (driving) {
            txtDriving.text = "Driving"
        }
        else {
            txtDriving.text = "Stopped"
        }

        val txtBroadcasts = this.findViewById(R.id.txtNumBroadcasts) as TextView
        txtBroadcasts.text = numBroadcasts.toString()

        val txtReceives = this.findViewById(R.id.txtNumReceives) as TextView
        txtReceives.text = numReceives.toString()
    }
}