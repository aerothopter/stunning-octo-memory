package com.timandzach.stunningoctomemory

import java.util.*
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/**
 * SpeedNotifier registers listeners, and notifies those listeners with relevant speed change info
 *
 */
class SpeedNotifier : BroadcastReceiver {

    constructor(act : Activity) : super()  {
        val filter : IntentFilter = IntentFilter(LocationService.BROADCAST_ACTION)
        act.registerReceiver(this, filter)
    }

    //Counts the number of broadcasts received
    var numReceives = 0

    /**
     * This method is called when the BroadcastReceiver is receiving an Intent
     * broadcast.  During this time you can use the other methods on
     * BroadcastReceiver to view/modify the current result values.  This method
     * is always called within the main thread of its process, unless you
     * explicitly asked for it to be scheduled on a different thread using
     * [android.content.Context.registerReceiver]. When it runs on the main
     * thread you should
     * never perform long-running operations in it (there is a timeout of
     * 10 seconds that the system allows before considering the receiver to
     * be blocked and a candidate to be killed). You cannot launch a popup dialog
     * in your implementation of onReceive().
     *
     *
     * **If this BroadcastReceiver was launched through a &lt;receiver&gt; tag,
     * then the object is no longer alive after returning from this
     * function.** This means you should not perform any operations that
     * return a result to you asynchronously. If you need to perform any follow up
     * background work, schedule a [android.app.job.JobService] with
     * [android.app.job.JobScheduler].
     *
     * If you wish to interact with a service that is already running and previously
     * bound using [bindService()][android.content.Context.bindService],
     * you can use [.peekService].
     *
     *
     * The Intent filters used in [android.content.Context.registerReceiver]
     * and in application manifests are *not* guaranteed to be exclusive. They
     * are hints to the operating system about how to find suitable recipients. It is
     * possible for senders to force delivery to specific recipients, bypassing filter
     * resolution.  For this reason, [onReceive()][.onReceive]
     * implementations should respond only to known actions, ignoring any unexpected
     * Intents that they may receive.
     *
     * @param context The Context in which the receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {

        if (intent != null) {
            if(intent.action == LocationService.BROADCAST_ACTION ) {
                val lat = intent.getDoubleExtra("Latitude", 0.0)
                val lon = intent.getDoubleExtra("Longitude", 0.0)
                val speed = intent.getFloatExtra("Speed", 0.0f)
                val numBroadcasts = intent.getIntExtra("NumBroadcasts", 0)
                numReceives++

                onLocationChanged(lat, lon, speed, numBroadcasts)
            }
        }
    }


    //Tracks if the car is driving or stopped
    var carIsDriving = false

    //If speed is higher than this threshold we consider the car to be driving
    val SPEED_THRESHOLD = 6f

    //If the speed is lower than this threshold we consider the car to be stopped
    val STOPPED_SPEED = 0.05f

    /**
     * Called when a location update is received. Updates debug info always,
     * and location info in the car changes from driving to stopped.
     *
     * @param latitude The new position's latitude
     * @param longitude The new position's longitude
     * @param speed The speed of the app when the position was calculated
     * @param numBroadcasts The number of broadcasts the location service has made. Used for
     *                      debugging
     */
    fun onLocationChanged(latitude : Double, longitude : Double, speed : Float, numBroadcasts : Int) {
            //If the vehicle was driving and has stopped, report the location
            if(carIsDriving && speed <= STOPPED_SPEED) {
                updateLocation(latitude, longitude)
                carIsDriving = false
            }

            //If the vehicle was stopped, but has started driving
            if (!carIsDriving && speed > SPEED_THRESHOLD) {
                carIsDriving = true
            }

        updateDebugInfo(latitude, longitude, speed, carIsDriving, numBroadcasts)
    }

    //The list of listeners that have registered for location updates
    val listeners = LinkedList<SpeedListener>()

    /**
     * Add a new listener to receive location updates
     *
     * @param listener The listener to register
     */
    fun register(listener : SpeedListener) {
        listeners.add(listener)
    }

    /**
     * Remove a registered listener to no longer receive updates
     *
     * @param listener The listener to remove
     */
    fun unregister(listener: SpeedListener) {
        listeners.remove(listener)
    }

    /**
     * Updates the debug info for each registered listener
     *
     * @param lat The new latitude to report
     * @param long The new longitude to report
     * @param speed The new speed to report
     * @param driving Whether the application is considered to be driving
     * @param numBroadcasts The number of broadcasts to report
     */
    fun updateDebugInfo(lat : Double, long : Double, speed : Float, driving : Boolean, numBroadcasts: Int) {
        for(l in listeners) {
            l.setDebugInfo(lat, long, speed, driving, numBroadcasts, numReceives)
        }
    }

    /**
     * Updates the location info for each registered listener
     *
     * @param lat The new latitude to report
     * @param long The new longitude to report
     */
    fun updateLocation(lat : Double, long : Double) {
        for(l in listeners) {
            l.updateLatLong(lat, long)
        }
    }
}