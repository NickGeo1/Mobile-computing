package com.example.exercise4

import android.Manifest
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.example.exercise4.ui.theme.Exercise1Theme
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserInitialisaton.Initialisaton() //insert predefined users in db
        Graph.markeradded = false //every time map recomposes after screen change we have to set this value to false so it can add the marker again
        //Ask for location permissions at app start
        val locationPermissionRequest = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                    // Precise location access granted.
                    Log.i("Permision","Precise location access granted")
                }
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                    // Only approximate location access granted.
                    Log.i("Permision","Only approximate location access granted")
                } else -> {
                // No location access granted.
                    Log.i("Permision","No location access granted")
                }
            }
        }

        locationPermissionRequest.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

        //If we accept the location permitions we set the current location value
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        fusedLocationClient.lastLocation.addOnSuccessListener {
            location: Location? -> Graph.currentLocation = location
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
}

