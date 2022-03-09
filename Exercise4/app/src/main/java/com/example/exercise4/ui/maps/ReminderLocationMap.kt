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
    position: String, //a length 2 list with current reminder lat lon. If new reminder, lat = lon = ""
    setVirtual: String //this is true if we want to set the virtual location
) {
    Graph.currentactivity = "Map"

    val setVirtualbool = setVirtual.toBoolean()
    val mapView = rememberMapViewWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val latitude = rememberSaveable {mutableStateOf(position.split(",")[0])}
    val longitude = rememberSaveable {mutableStateOf(position.split(",")[1])}
    val virtallocation = rememberSaveable {mutableStateOf(Graph.virtualLocation)}
    val camerazoom = rememberSaveable {mutableStateOf(10f)}
    //if we accepted the location permissions, we move the camera to current location if we want to add
    //a new reminder marker. At this case if we didn't accept location permissions(or app couldn't find our location) we move the camera
    //to Oulu
    //At the case we want to change a marker position, we move the camera at this marker
    //Similar case is for virtual location marker
    val location = when {
        virtallocation.value != null && setVirtualbool -> virtallocation.value

        latitude.value == "" && longitude.value == "" ->
            LatLng(
            Graph.currentLocation?.latitude ?: 65.06,
            Graph.currentLocation?.longitude ?: 25.47
            )

        else -> LatLng(latitude.value.toDouble(), longitude.value.toDouble())
    }
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

                //We enable our location in the map if we had accepted the location permitions
                map.isMyLocationEnabled = when(Graph.currentLocation){
                    null -> false
                    else -> true
                }

                location?.let {
                    CameraUpdateFactory.newLatLngZoom(it, camerazoom.value) }?.let {
                    map.moveCamera(
                        it
                    )
                }
                //Similar to reminder marker (see bellow)
                if(virtallocation.value != null && !Graph.markeradded)
                {
                    val virtualsnippet = String.format(
                        Locale.getDefault(),
                        "Lat: %1$.2f, Lng: %2$.2f",
                        virtallocation.value!!.latitude,
                        virtallocation.value!!.longitude
                    )

                    map.addMarker(
                        MarkerOptions()
                            .position(LatLng(virtallocation.value!!.latitude, virtallocation.value!!.longitude))
                            .title("Virtual location")
                            .snippet(virtualsnippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .draggable(setVirtualbool)
                    )?.showInfoWindow()
                }

                //this if block is executed:
                //1//During recomposition after we add the first reminder marker
                //2//After dropping a reminder marker during dragging
                //3//At the initial point of reminder marker position change
                if(latitude.value != "" && longitude.value != "" && !Graph.markeradded)
                {
                    val snippet = String.format(
                        Locale.getDefault(),
                        "Lat: %1$.2f, Lng: %2$.2f",
                        latitude.value.toDouble(),
                        longitude.value.toDouble()
                    )

                    map.addMarker(
                        MarkerOptions()
                            .position(LatLng(latitude.value.toDouble(), longitude.value.toDouble()))
                            .title("Reminder location")
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                            .draggable(true)
                    )?.showInfoWindow().apply {
                        navController.previousBackStackEntry
                            ?.savedStateHandle
                            ?.set(
                                "location_data",
                                LatLng(latitude.value.toDouble(), longitude.value.toDouble())
                            )
                    }
                }
                //this block runs when we add the first reminder marker
                if (!Graph.markeradded && latitude.value == "" && longitude.value == "" && !setVirtualbool)
                {
                        setMapLongClick(
                        map = map,
                        //after we change the lat, lon values map is going to recompose and we get at if block
                        changelocation = {lat,lon ->latitude.value = lat; longitude.value = lon
                        camerazoom.value = map.cameraPosition.zoom})
                //this block runs when we add the first virtual location marker
                }else if(!Graph.markeradded && Graph.virtualLocation == null && setVirtualbool)
                {
                    setMapLongClick(
                        map = map,
                        //after we change the lat, lon values map is going to recompose and we get at if block
                        changelocation = {vlat,vlon -> virtallocation.value = LatLng(vlat.toDouble(), vlon.toDouble())
                        Graph.virtualLocation = virtallocation.value
                        camerazoom.value = map.cameraPosition.zoom})
                }
                //if we are setting a virtual marker
                if(setVirtualbool)
                    setMarkerDrag(map = map, changelocation = {vlat,vlon -> virtallocation.value = LatLng(vlat.toDouble(), vlon.toDouble())
                    Graph.virtualLocation = virtallocation.value
                    camerazoom.value = map.cameraPosition.zoom})
                //if we are setting a reminder marker
                else
                    setMarkerDrag(map = map, changelocation = {lat,lon -> latitude.value = lat; longitude.value = lon
                    camerazoom.value = map.cameraPosition.zoom})
                //if we already added a marker one of these booleans is true so we change markeradded to true
                if(latitude.value != "" && longitude.value != "" || virtallocation.value != null && setVirtualbool)
                    Graph.markeradded = true //during case 3, next time coroutine runs, is not going to add another marker
            }
        }
    }
}


private fun setMapLongClick(
    map: GoogleMap,
    changelocation: (String, String) -> Unit
){
    //when we long click to add the marker we set
    //markeradded true to prevent this action
    //from repeating
    var markeradded = false
    map.setOnMapLongClickListener { latlng ->
        if(!markeradded){
            markeradded = true
            changelocation(latlng.latitude.toString(), latlng.longitude.toString())
        }

    }
}


@SuppressLint("PotentialBehaviorOverride")
private fun setMarkerDrag(
    map: GoogleMap,
    changelocation: (String, String) -> Unit,
){
    //we execute a marker drag listener and we provide an interface to it with the methods we want to execute
    //at drag/drag end/drag start
    map.setOnMarkerDragListener(object: GoogleMap.OnMarkerDragListener {
        override fun onMarkerDrag(p0: Marker) {

        }

        override fun onMarkerDragEnd(p0: Marker) {
            //on drag drop we remove this marker. During lat and lon position changes
            //Map is being recomposed and a new marker with the new position is being made
            p0.remove()
            Graph.markeradded = false
            changelocation(p0.position.latitude.toString(), p0.position.longitude.toString())
        }

        override fun onMarkerDragStart(p0: Marker) {
            p0.hideInfoWindow()
        }
    })
}
