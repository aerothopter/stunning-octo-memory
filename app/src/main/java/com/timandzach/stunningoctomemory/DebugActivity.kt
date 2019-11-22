package com.timandzach.stunningoctomemory

import android.os.Bundle
import java.util.Formatter
import java.util.Locale
import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.app.Activity
import android.content.SharedPreferences
import android.content.Intent
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat


class DebugActivity : Activity(), SpeedListener {

    val UNIQUE_REQUEST_FINE_LOCATION_ID = 780917890
    val PREFS_FILENAME = "com.timandzach.stunningoctomemory.prefs"

    var prefs: SharedPreferences? = null

    var latitude = 0.0
    var longitude = 0.0


    lateinit var speedNotifier : SpeedNotifier

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        val quit_button = this.findViewById(R.id.quitButton) as Button

        quit_button.setOnClickListener( {
            finish()
        })

        // setting up the shared preferences file and pulling old values
        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        this.latitude = Double.fromBits(prefs!!.getLong("latitude",(0.0).toBits()))
        this.longitude = Double.fromBits(prefs!!.getLong("longitude",(0.0).toBits()))

        this.updateSpeed(this.latitude, this.longitude)

        // we don't want to restart the service but just register
        speedNotifier = SpeedNotifier(this)
        speedNotifier.register(this)
    }

    override fun finish() {
        super.finish()

        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)

        speedNotifier.unregister(this)

        System.exit(0)
    }

    override fun updateSpeed(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude

        val editor = this.prefs!!.edit()
        editor.putLong("latitude", this.latitude.toBits())
        editor.putLong("longitude", this.longitude.toBits())
        editor.apply()
    }

    override fun getDebugInfo(latitude: Double, longitude: Double, speed : Float, driving : Boolean, numBroadcasts : Int, numReceives : Int) {
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