package com.timandzach.stunningoctomemory

import java.util.*
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

/*
SpeedNotifier registers listeners, and notifies those listeners with relevant speed change info
 */

//class SpeedNotifier(val act : Activity) : LocationListener {
class SpeedNotifier : BroadcastReceiver {


    constructor(act : Activity) : super()  {
        val filter : IntentFilter = IntentFilter(LocationService.BROADCAST_ACTION)
        act.registerReceiver(this, filter)
    }
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

    var numReceives = 0

    override fun onReceive(context: Context?, intent: Intent?) {
        var lat : Double = 0.0
        var lon : Double = 0.0
        var speed : Float = 0.0f
        var numBroadcasts =  0

        if (intent != null) {
            if(intent.action == LocationService.BROADCAST_ACTION ) {
                lat = intent.getDoubleExtra("Latitude", 0.0)
                lon = intent.getDoubleExtra("Longitude", 0.0)
                speed = intent.getFloatExtra("Speed", 0.0f)
                numBroadcasts = intent.getIntExtra("NumBroadcasts", 0)
                numReceives++

                onLocationChanged(lat, lon, speed, numBroadcasts)
            }
        }
    }


    var carIsDriving = false
    val SPEED_THRESHOLD = 0.2f
    val STOPPED_SPEED = 0.05f

    fun onLocationChanged(latitude : Double, longitude : Double, speed : Float, numBroadcasts : Int) {

        //TODO Delete this
        //updateLocation(latitude, longitude)

            //If the vehicle was driving and has stopped, report the location
            if(carIsDriving && speed <= STOPPED_SPEED) {
//                updateLocation(latitude, longitude)
                carIsDriving = false
            }

            //If the vehicle was stopped, but has started driving
            if (!carIsDriving && speed > SPEED_THRESHOLD) {
                carIsDriving = true
            }

        updateLocation(latitude, longitude, speed, carIsDriving, numBroadcasts)

    }

    val listeners = LinkedList<SpeedListener>()

    fun register(listener : SpeedListener) {
        listeners.add(listener)
    }

    fun unregister(listener: SpeedListener) {
        listeners.remove(listener)
    }

//    fun updateLocation(lat : Double, long : Double) {
//        for(l in listeners) {
//            l.updateSpeed(lat, long)
//        }
//    }

    fun updateLocation(lat : Double, long : Double, speed : Float, driving : Boolean, numBroadcasts: Int) {
        for(l in listeners) {
            l.updateSpeed(lat, long, speed, driving, numBroadcasts, numReceives)
        }
    }
}