package com.timandzach.stunningoctomemory

import android.Manifest
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.Toast

import androidx.core.app.ActivityCompat

class LocationService : Service() {
    lateinit var locationManager: LocationManager
    lateinit var listener: MyLocationListener
    var previousBestLocation: Location? = null

    lateinit internal var intent: Intent
    internal var counter = 0

    override fun onCreate() {
        super.onCreate()
        intent = Intent(BROADCAST_ACTION)
    }

    override fun onStart(intent: Intent, startId: Int) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = MyLocationListener()
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        locationManager.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            4000,
            0f,
            listener as LocationListener
        )
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0f, listener)
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    protected fun isBetterLocation(location: Location, currentBestLocation: Location?): Boolean {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true
        }

        // Check whether the new location fix is newer or older
        val timeDelta = location.time - currentBestLocation.time
        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false
        }

        // Check whether the new location fix is more or less accurate
        val accuracyDelta = (location.accuracy - currentBestLocation.accuracy).toInt()
        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200

        // Check if the old and new location are from the same provider
        val isFromSameProvider = isSameProvider(
            location.provider,
            currentBestLocation.provider
        )

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }


    /**
     * Checks whether two providers are the same
     */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean {
        return if (provider1 == null) {
            provider2 == null
        } else provider1 == provider2
    }


    override fun onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy()
        Log.v("STOP_SERVICE", "DONE")
        locationManager.removeUpdates(listener)
    }

    inner class MyLocationListener : LocationListener {

        override fun onLocationChanged(loc: Location) {
            Log.i("*****", "Location changed")
            if (isBetterLocation(loc, previousBestLocation)) {
                loc.latitude
                loc.longitude
                intent.putExtra("Latitude", loc.latitude)
                intent.putExtra("Longitude", loc.longitude)
                intent.putExtra("Speed", loc.speed)
                sendBroadcast(intent)
            }
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }

        override fun onProviderDisabled(provider: String) {
            Toast.makeText(applicationContext, "Gps Disabled", Toast.LENGTH_SHORT).show()
        }


        override fun onProviderEnabled(provider: String) {
            Toast.makeText(applicationContext, "Gps Enabled", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        val BROADCAST_ACTION = "com.timandzach.stunningoctomemory.BROADCAST_ACTION"
        private val TWO_MINUTES = 1000 * 60 * 2

        fun performOnBackgroundThread(runnable: Runnable): Thread {
            val t = object : Thread() {
                override fun run() {
                    try {
                        runnable.run()
                    } finally {

                    }
                }
            }
            t.start()
            return t
        }
    }
}