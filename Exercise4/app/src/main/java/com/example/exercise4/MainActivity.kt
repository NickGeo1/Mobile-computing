package com.example.exercise4

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.core.content.ContextCompat
import com.example.exercise4.ui.theme.Exercise1Theme
import com.google.android.gms.location.*

class MainActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationpermission: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserInitialisaton.Initialisaton() //insert predefined users in db
        Graph.markeradded =
            false //every time map recomposes after screen rotate we have to set this value to false so it can add the marker again

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //If we accept the location permissions we set the current location value
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            Graph.currentLocation = location
        }

        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when { //set locationpermition true if we accept the permissions in order to let update location function run. In other case, dont let update location function run
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    locationpermission = true
                    Log.i("Permision", "Precise location access granted")
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    locationpermission = true
                    Log.i("Permision", "Only approximate location access granted")
                }
                else -> {
                    // No location access granted.
                    locationpermission = false
                    Log.i("Permision", "No location access granted")
                }
            }
        }

        when //when the permissions are already accepted set locationpermition true to let update location function run
        {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED -> {
                locationpermission = true
            }
            else -> //else ask user for permissions
            {
                locationPermissionRequest.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

        }
        //location update request. We update the request every 2 seconds
        locationRequest = LocationRequest.create().apply {
            interval = 2000
            fastestInterval = 2000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        //location callback. Here we change every 2 seconds the Graph.currentLocation
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    Graph.currentLocation = location
                    Log.i("Location", "Location changed")
                }
            }
        }

        setContent {
            Exercise1Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    ApplicationActivities()
                }
            }
        }
    }

    override fun onResume() { //start location update if permissions are accepted
        super.onResume()
        if(locationpermission) startLocationUpdates()
    }

    fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }
}




