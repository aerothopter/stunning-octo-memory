/*
SpeedListener registers with a SpeedNotifier
 */

interface SpeedListener {
    fun updateSpeed(latitude: Double, longitude: Double)
}