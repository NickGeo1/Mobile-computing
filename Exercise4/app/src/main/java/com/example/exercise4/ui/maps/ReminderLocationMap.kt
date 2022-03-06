package com.example.exercise4.ui.maps

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.override
import androidx.navigation.NavController
import com.example.exercise4.Graph
import com.example.exercise4.util.rememberMapViewWithLifecycle
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.ktx.awaitMap
import java.util.*
import kotlinx.coroutines.launch

@Composable
fun ReminderLocationMap(
    navController: NavController,
    position: String
) {
    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val latitude = position.split(",")[0]
    val longitude = position.split(",")[1]

    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
        .padding(bottom = 36.dp)
    ) {
        AndroidView({mapView}) { mapView ->
            //this coroutine runs two times. We want to restrict it from adding two markers when its time
            //to change the location of current marker. We make Graph.markeradded true when the coroutine
            //runs for the first time so at the second time no marker is added
            coroutineScope.launch {
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true
                val location = LatLng(65.06, 25.47)

                map.moveCamera(
                    CameraUpdateFactory.newLatLngZoom(location, 10f)
                )
                if(latitude != "" && longitude != "" && !Graph.markeradded)
                {
                    val snippet = String.format(
                        Locale.getDefault(),
                        "Lat: %1$.2f, Lng: %2$.2f",
                        latitude.toDouble(),
                        longitude.toDouble()
                    )

                    map.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude.toDouble(), longitude.toDouble()))
                            .title("Reminder location")
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .draggable(true)
                    )?.showInfoWindow()
                    Graph.markeradded = true
                }else if(!Graph.markeradded)
                {
                    setMapLongClick(map = map, navController = navController)
                }

                setMarkerDrag(map = map, navController = navController)
            }
        }
    }
}

private fun setMapLongClick(
    map: GoogleMap,
    navController: NavController,
) {
    //when we long click to add the marker we set
    //Graph.markeradded true to prevent this action
    //from repeating
    map.setOnMapLongClickListener { latlng ->
        if(!Graph.markeradded){
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.2f, Lng: %2$.2f",
                latlng.latitude,
                latlng.longitude
            )

            map.addMarker(
                MarkerOptions()
                    .position(latlng)
                    .title("Reminder location")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                    .draggable(true)

            )?.showInfoWindow().apply {
                navController.previousBackStackEntry
                    ?.savedStateHandle
                    ?.set("location_data", latlng)
            }
            Graph.markeradded = true
        }

    }
}


@SuppressLint("PotentialBehaviorOverride")
private fun setMarkerDrag(
    map: GoogleMap,
    navController: NavController,
){
    //we execute a marker drag listener and we provide an interface to it with the methods we want to execute
    //at drag/drag end/drag start
    map.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener {
        override fun onMarkerDrag(p0: Marker) {

        }

        override fun onMarkerDragEnd(p0: Marker) {

            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1$.2f, Lng: %2$.2f",
                p0.position.latitude,
                p0.position.longitude
            )

            p0.snippet = snippet

            p0.showInfoWindow()

            navController.previousBackStackEntry
                ?.savedStateHandle
                ?.set("location_data", p0.position)
        }

        override fun onMarkerDragStart(p0: Marker) {
            p0.hideInfoWindow()
        }
    })
}

