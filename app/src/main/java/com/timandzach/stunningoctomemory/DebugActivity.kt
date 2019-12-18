package com.timandzach.stunningoctomemory

import android.app.Activity
import android.content.SharedPreferences
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.util.*


class DebugActivity : Activity(), SpeedListener {

    val PREFS_FILENAME = "com.timandzach.stunningoctomemory.prefs"

    var prefs: SharedPreferences? = null

    var latitude = 0.0
    var longitude = 0.0


    lateinit var speedNotifier : SpeedNotifier

    /**
     * Called when the activity is starting.
     *
     * @param savedInstanceState If the activity is being re-initialized after previously
     * being shut down then this Bundle contains the data it most recently supplied in
     * OnSaveInstanceState(Bundle). Note: Otherwise it is null.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_debug)

        val quit_button = this.findViewById(R.id.quitButton) as Button

        quit_button.setOnClickListener( {
            finish(true)
        })

        // setting up the shared preferences file and pulling old values
        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        this.latitude = Double.fromBits(prefs!!.getLong("latitude",(0.0).toBits()))
        this.longitude = Double.fromBits(prefs!!.getLong("longitude",(0.0).toBits()))

        this.updateLatLong(this.latitude, this.longitude)

        // we don't want to restart the service but just register
        speedNotifier = SpeedNotifier(this)
        speedNotifier.register(this)
    }

    /**
     * Called when the activity is exited. Since this is likely not the root activity,
     * We instead switch to the MapsActivity with a specific flag to tell it to close.
     *
     * @param exit Whether or not to trigger the app exit.
     */
    fun finish(exit: Boolean) {
        super.finish()

        speedNotifier.unregister(this)

        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("EXIT", exit)
        startActivity(intent)
    }

    /**
     * Callback function provided to SpeedNotifier when there is an update available.
     * Updates the stored latitude and longitude and writes to the SharedPreferences file.
     *
     * @param latitude The new position's latitude
     * @param longitude The new position's longitude
     */
    override fun updateLatLong(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude

        val editor = this.prefs!!.edit()
        editor.putLong("latitude", this.latitude.toBits())
        editor.putLong("longitude", this.longitude.toBits())
        editor.apply()
    }

    /**
     * Called alongside updateLatLong(Double, Double). Prints debug information to the screen.
     *
     * @param latitude The new position's latitude.
     * @param longitude The new position's longitude.
     * @param speed The device's new speed, in miles per hour.
     * @param driving Whether or not the device is considered "driving," based on speed thresholds
     * @param numBroadcasts The number of service broadcasts to report
     * @param numReceives The number of received broadcasts from this SpeedNotifier
     */
    override fun setDebugInfo(latitude: Double, longitude: Double, speed : Float, driving : Boolean, numBroadcasts : Int, numReceives : Int) {
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