package com.timandzach.stunningoctomemory

import android.Manifest
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

class SpeedNotifier : Activity(), LocationListener {

    val UNIQUE_REQUEST_FINE_LOCATION_ID = 780917890
    val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderEnabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProviderDisabled(p0: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    var carWasDriving = false
    val SPEED_THRESHOLD = 20
    val STOPPED_SPEED = 1.0f

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //Check that we have permission to access the user's location. Request that permission if needed
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), UNIQUE_REQUEST_FINE_LOCATION_ID)

            return
        }

        //Register to get location updates
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)
    }

    //If we get permission to access the uesr's location, register to get location updates
    //TODO: Could be made more robust, but we shouldn't ever have more than one permission being granted
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == UNIQUE_REQUEST_FINE_LOCATION_ID) {
            if (permissions.size > 0 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,
                        0f, this)
                }
            }
        }
    }

    val listeners = LinkedList<SpeedListener>()

    fun register(listener : SpeedListener) {
        listeners.add(listener)
    }

    fun updateLocation(lat : Double, long : Double) {
        listeners.forEach {updateLocation(lat, long)}
    }
}