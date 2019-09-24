package com.timandzach.stunningoctomemory

class LocationUpdater : SpeedListener {

    var latitude: Number? = 127
    var longitude: Number? = 111

    var latitudeString: String = "127"
        private set

    var longitudeString: String = "111"
        private set

    override fun updateSpeed(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude
    }

    override fun getDebugInfo(
        latitude: Double,
        longitude: Double,
        speed: Float,
        driving: Boolean,
        numBroadcasts: Int,
        numReceives: Int
    ) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}