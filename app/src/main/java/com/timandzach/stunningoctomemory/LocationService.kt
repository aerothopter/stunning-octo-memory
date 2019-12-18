package com.timandzach.stunningoctomemory

import android.Manifest
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
import android.app.*
import android.graphics.Color
import androidx.core.app.NotificationCompat
import android.os.Build
import androidx.annotation.RequiresApi


class LocationService: Service() {

     // A unique string for broadcasts to match with receivers
    companion object {
        val BROADCAST_ACTION = "com.timandzach.stunningoctomemory.BROADCAST_ACTION"
    }

    // A random number used when starting the foreground service
    val UNIQUE_NOTIFICATION_ID = 541784

    // The default speed to receive location updates
    val DEFAULT_UPDATE_SPEED = 10000


    lateinit var locationManager: LocationManager
    lateinit var listener: MyLocationListener

    lateinit internal var intent: Intent

    /**
     * Creates the service and sets it as a foreground service
     */
    override fun onCreate() {
        super.onCreate()

        intent = Intent(BROADCAST_ACTION)

        val notificationIntent = Intent(this, MapsActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            notificationIntent, 0
        )

        val b = NotificationCompat.Builder(this)

        b.setOngoing(true)
            .setContentTitle("StunningOctoMemory")
            .setContentText("Tap to see your last parking spot")
            .setSmallIcon(R.drawable.directions_car_24px)
            .setTicker("Ticker")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            b.setChannelId(createNotificationChannel("zachandtim", "timandzach"))
        }

        val notification = b.build()

        startForeground(UNIQUE_NOTIFICATION_ID, notification)
    }

    /**
     * Creates a notification channel with some default parameters
     *
     * @param channelId The notification channel's channel ID
     * @param channelName The notification channel's name
     * @return Returns the notification channel's ID after creating the channel
     */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String{
        val chan = NotificationChannel(channelId,
            channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    /**
     * Called when the service starts. Creates a MyLocationListener to request location
     * updates from the location manager.
     *
     * @param intent This parameter is not used
     * @param startId This parameter is not used
     */
    override fun onStart(intent: Intent, startId: Int) {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listener = MyLocationListener()

        /* Check that we have permission to access the user's location data. If so, request
         * location updates.
         */
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

        val updateSpeed = intent.getIntExtra("UpdateSpeed", DEFAULT_UPDATE_SPEED)
        Log.i("*****", "Update Speed = " + updateSpeed)

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, updateSpeed.toLong(),
            0f, listener)
    }

    /**
     * This service does not use binding, so return null if binding is requested
     *
     * @param intent This parameter is not used
     * @return Always returns null to report that binding is not available
     */
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    /**
     * When the service is destroyed it also unregisters from receiving location updates
     *
     */
    override fun onDestroy() {
        super.onDestroy()
        Log.v("STOP_SERVICE", "DONE")
        locationManager.removeUpdates(listener)
    }

    /**
     * A LocationListener that contains the information needed to run the app
     *
     */
    inner class MyLocationListener : LocationListener {
        var numBroadcasts = 0

        override fun onLocationChanged(loc: Location) {
            //Log.i("*****", "Location changed")

             intent.putExtra("Latitude", loc.latitude)
             intent.putExtra("Longitude", loc.longitude)
             intent.putExtra("Speed", loc.speed)
             numBroadcasts += 1
             intent.putExtra("NumBroadcasts", numBroadcasts)
             sendBroadcast(intent)

        }

        /**
         * This function is not used
         *
         * @param provider
         * @param status
         * @param extras
         */
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {

        }

        /**
         * Display the disabled message
         *
         * @param provider This parameter is not used
         */
        override fun onProviderDisabled(provider: String) {
            Toast.makeText(applicationContext, "Gps Disabled", Toast.LENGTH_SHORT).show()
        }


        /**
         * Display the enabled message
         *
         * @param provider This parameter is not used
         */
        override fun onProviderEnabled(provider: String) {
            Toast.makeText(applicationContext, "Gps Enabled", Toast.LENGTH_SHORT).show()
        }
    }
}