package com.timandzach.stunningoctomemory

import android.location.GpsStatus
import android.Manifest
import android.annotation.SuppressLint
import java.util.*
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle

/*
SpeedNotifier registers listeners, and notifies those listeners with relevant speed change info
 */

class SpeedNotifier(val act : Activity) : LocationListener, GpsStatus.Listener {

    lateinit var locationManager : LocationManager

    @SuppressLint("MissingPermission")
    fun onCreate() {
        locationManager = act.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        //Register to get location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    init {
        onCreate()
    }

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
    }

    override fun onProviderEnabled(p0: String?) {
    }

    override fun onProviderDisabled(p0: String?) {
    }

    override fun onGpsStatusChanged(p0: Int) {
    }

    var carWasDriving = false
    val SPEED_THRESHOLD = 0.5f
    val STOPPED_SPEED = 0.25f

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            //If the vehicle was driving and has stopped, report the location
            if(carWasDriving && location.speed <= STOPPED_SPEED) {
                updateLocation(location.latitude, location.longitude)
                carWasDriving = false
            }

            //If the vehicle was stopped, but has started driving
            if (!carWasDriving && location.speed > SPEED_THRESHOLD) {
                carWasDriving = true
            }
        }
    }

    val listeners = LinkedList<SpeedListener>()

    fun register(listener : SpeedListener) {
        listeners.add(listener)
    }

    fun updateLocation(lat : Double, long : Double) {
        for(l in listeners) {
            l.updateSpeed(lat, long)
        }
    }
}