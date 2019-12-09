package com.timandzach.stunningoctomemory

/**
 * SpeedListener registers with a SpeedNotifier to receive location and debug data
 */
interface SpeedListener {
    /**
     * Update the latitude and longitude of the listener
     *
     * @param latitude The new latitude to report
     * @param longitude The new longitude to report
     */
    fun updateLatLong(latitude: Double, longitude: Double)

    /**
     * Update the debug info for the listener
     *
     * @param latitude The new latitude to report
     * @param longitude The new longitude to report
     * @param speed The new speed to report
     * @param driving Whether the app is considered driving in this update
     * @param numBroadcasts The new number of broadcasts to report
     * @param numReceives The new number of receives to report
     */
    fun setDebugInfo(latitude: Double, longitude: Double, speed : Float,
                     driving : Boolean, numBroadcasts : Int, numReceives : Int)
}