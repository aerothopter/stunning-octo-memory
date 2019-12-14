package com.timandzach.stunningoctomemory

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, SpeedListener {

    private lateinit var mMap: GoogleMap

    val UNIQUE_REQUEST_FINE_LOCATION_ID = 7809
    val PREFS_FILENAME = "com.timandzach.stunningoctomemory.prefs"

    var service_running = false
    var prefs: SharedPreferences? = null

    var latitude = 0.0
    var longitude = 0.0

    private lateinit var parkingMarker: Marker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setting up the map and toolbar
        setContentView(R.layout.activity_maps)
        setSupportActionBar(findViewById(R.id.menu_toolbar))

        // setting up the shared preferences file and pulling old values
        prefs = this.getSharedPreferences(PREFS_FILENAME,0)
        this.latitude = Double.fromBits(prefs!!.getLong("latitude",(0.0).toBits()))
        this.longitude = Double.fromBits(prefs!!.getLong("longitude",(0.0).toBits()))

        //Check that we have permission to access the user's location. Request that permission if needed
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), UNIQUE_REQUEST_FINE_LOCATION_ID)

            val txtCurrentSpeed = this.findViewById(R.id.txtCurrentSpeed) as TextView?
            if (txtCurrentSpeed != null) {
                txtCurrentSpeed.text = "This application requires access to the user's location"
            }
            return
        }

        //begin the service
        initSpeedNotifications()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (intent.getBooleanExtra("EXIT", false)) {
            finish()
        }
    }

    //If we get permission to access the uesr's location and background location, register to get location updates
    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        var resultIndex = 0

        for (p in permissions) {
            if (p == Manifest.permission.ACCESS_FINE_LOCATION) {
                if (grantResults[resultIndex] == PackageManager.PERMISSION_GRANTED) {
                    initSpeedNotifications()

                    return
                } else {
                    val txtCurrentSpeed = this.findViewById(R.id.txtCurrentSpeed) as TextView
                    txtCurrentSpeed.text = "This application requires access to the user's location"
                }
            }
            else {
                resultIndex++
            }
        }
    }

    fun initSpeedNotifications () {
        if (!service_running) {
            val serviceIntent = Intent(this, LocationService::class.java)
            serviceIntent.putExtra("UpdateSpeed", 1000)
            startService(serviceIntent)

            //speedNotifier = SpeedNotifier(this)
            val filter = IntentFilter(LocationService.BROADCAST_ACTION)
            this.registerReceiver(SpeedNotifier.instance, filter)

            SpeedNotifier.instance.register(this)

            service_running = true
        }
    }

    override fun finish() {
        super.finish()

        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)

        service_running = false

        SpeedNotifier.instance.unregister(this)

        System.exit(0)
    }

    override fun updateLatLong(latitude: Double, longitude: Double) {
        this.latitude = latitude
        this.longitude = longitude

        val editor = this.prefs!!.edit()
        editor.putLong("latitude", this.latitude.toBits())
        editor.putLong("longitude", this.longitude.toBits())

        editor.apply()

        val parkingLatLng = LatLng(latitude,longitude)

        // if someone has moved, move the marker
        if(::parkingMarker.isInitialized && parkingMarker.position != parkingLatLng ) {
            parkingMarker.position = parkingLatLng
        }

    }

    override fun setDebugInfo(latitude: Double, longitude: Double, speed : Float, driving : Boolean, numBroadcasts : Int, numReceives : Int) {

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val parkingLatLng = LatLng(this.latitude,this.longitude)

        parkingMarker = mMap.addMarker(MarkerOptions().position(parkingLatLng).title("Your Parking Spot"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(parkingLatLng,18.0f))

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_debug -> {
            this.startActivity(Intent(this,DebugActivity::class.java))
            true
        }

        R.id.action_settings -> {
            this.startActivity(Intent(this, SettingsActivity::class.java))
            Toast.makeText(this,"Go To Settings",Toast.LENGTH_SHORT).show()
            true
        }

        else ->  super.onOptionsItemSelected(item)
    }
}
