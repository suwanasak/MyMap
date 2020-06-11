package com.example.mymap

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener

// top level
const val UPDATE_INTERVAL: Long = 5000  //sce
const val FASTEST_INTERVAL: Long = 1000  //sce

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap

    private var mCurrentLocation: Location? = null
    private var mLocationProvider: FusedLocationProviderClient? = null
    private var mCallback: LocationCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mLocationProvider = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            mMap = googleMap

            setupMap()  //mute be call after mMap = googleMap
            checkRuntimePermission()
            dummyLocation()
        }
    }

    private fun dummyLocation() {
        val myHouse = LatLng(0.0, 0.0)
        addMarker("My Home", "9999/99", myHouse)

    }

    private fun addMarker(title: String, subtitle: String, latLng: LatLng) {
        val marker = MarkerOptions()
        marker.position(latLng)
        marker.title(title)
        marker.position(subtitle)
        marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_test))

    }

    private fun setupMap() {
        val iniLocation = LatLng(13.9821187, 100.5582046)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(iniLocation, 14f))

       mMap.isTrafficEnabled = true

       mMap.uiSettings.isCompassEnabled = true
       mMap.uiSettings.isZoomGesturesEnabled = true

       mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
    }

    private fun checkRuntimePermission() = Dexter.withActivity(this)
        .withPermission(
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        .withListener(object : PermissionListener {
            @SuppressLint("MissingPermission")
            override fun onPermissionGranted(response: PermissionGrantedResponse) {
                mMap.isMyLocationEnabled = true
                //getLastLocation()
               tracking()

            }

            override fun onPermissionDenied(response: PermissionDeniedResponse) {
                finish()
            }

            override fun onPermissionRationaleShouldBeShown(
                permission: PermissionRequest,
                token: PermissionToken
            ) {
                token.continuePermissionRequest()
            }
        }).check()

    @SuppressLint("MissingPermission")
    private fun tracking() {
        val request = LocationRequest()
        request.interval = UPDATE_INTERVAL
        request.fastestInterval = FASTEST_INTERVAL
        request.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        mCallback = object : LocationCallback() {
            override fun onLocationResult(resut: LocationResult?) {
                super.onLocationResult(resut)
                val currentLocation = resut!!.lastLocation
                if (currentLocation != null) {
                    mCurrentLocation = currentLocation
                    animateCamera(LatLng(currentLocation.latitude, currentLocation.longitude), 15)
                }
            }
        }

        mLocationProvider!!.requestLocationUpdates(request, mCallback, Looper.myLooper())
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        mLocationProvider?.lastLocation
            ?.addOnSuccessListener { location: Location? ->
                mCurrentLocation = location
                if (location != null) {
                    animateCamera(LatLng(location.latitude, location.longitude), 15)
                }
            }
    }

    private fun animateCamera(latLng: LatLng, zoom: Int) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom.toFloat()))
    }

}

private fun MarkerOptions.position(subtitle: String) {
    
}
