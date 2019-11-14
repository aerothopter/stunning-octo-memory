package com.timandzach.stunningoctomemory

/*
SpeedListener registers with a SpeedNotifier
 */

interface SpeedListener {
    fun updateSpeed(latitude: Double, longitude: Double)

    fun getDebugInfo(latitude: Double, longitude: Double, speed : Float,
                     driving : Boolean, numBroadcasts : Int, numReceives : Int)
}