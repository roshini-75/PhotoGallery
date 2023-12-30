package com.example.myproject


import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class MapActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var tvLatitude: TextView
    private lateinit var tvLongitude: TextView

    private var homeLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.current_location)

        // Initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Initialize UI elements
        tvLatitude = findViewById(R.id.tvLatitude)
        tvLongitude = findViewById(R.id.tvLongitude)

        // Assuming you have latitude and longitude values available
        val specificLatitude = 11.9686243
        val specificLongitude = 79.2079277

        // Check for location permission
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission granted, get last known location
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        // Update UI with latitude and longitude values
                        if (homeLocation == null) {
                            // Set home location for the first time
                            homeLocation = Location("")
                            homeLocation?.latitude = it.latitude
                            homeLocation?.longitude = it.longitude
                        }

                        // Check if the current location is equal to the specific coordinates
                        if (it.latitude == specificLatitude && it.longitude == specificLongitude) {
                            updateLocationUI(it.latitude, it.longitude)
                        } else {
                            // Check if the current location is within 10 meters of the specific coordinates
                            val distance = calculateDistance(
                                it.latitude,
                                it.longitude,
                                specificLatitude,
                                specificLongitude
                            )
                            if (distance <= 10) {
                                updateLocationUI(it.latitude, it.longitude)
                            } else {
                                Toast.makeText(
                                    this,
                                    "You are away from the specific location.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
        } else {
            // Permission is not granted, request it.
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun updateLocationUI(latitude: Double, longitude: Double) {
        // Display latitude and longitude values in TextViews
        tvLatitude.text = "Latitude: $latitude"
        tvLongitude.text = "Longitude: $longitude"
    }

    // Function to calculate the distance between two sets of coordinates
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0]
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, refresh the location
                recreate()
            } else {
                // Permission denied, show a message
                Toast.makeText(
                    this,
                    "Location permission denied. Cannot show current location.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
