package com.timandzach.stunningoctomemory

class LocationUpdater : SpeedListener {

    var latitude: Number? = 127
    var longitude: Number? = 111

    var latitudeString: String = "127"
        private set

    var longitudeString: String = "111"
        private set

    override fun updateSpeed(latitude: Double, longitude: Double, speed : Float, driving : Boolean) {
        this.latitude = latitude
        this.longitude = longitude
    }


}