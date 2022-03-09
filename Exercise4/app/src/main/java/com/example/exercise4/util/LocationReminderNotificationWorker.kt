package com.example.exercise4.util

import android.content.Context
import android.util.Log
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.exercise4.Graph
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception

class LocationReminderNotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {
    //0.002 latitude/longitude point is 222 meters
    //we check if current/virtual location is inside the
    //rectangle with points referenced bellow(we assume that (0,0) is the reminder_location):

    //when we go north the latitude value raises
    //when we go east the longitude value raises
    //upper-left: (reminder_location_lon - 0,002, reminder_location_lat + 0.002)
    //upper-right: (reminder_location_lon + 0,002, reminder_location_lat + 0.002)
    //bottom:left (reminder_location_lon - 0,002, reminder_location_lat - 0.002)
    //bottom-right: (reminder_location_lon + 0,002, reminder_location_lat - 0.002)
    override fun doWork(): Result {
        return try{

            var currentinsidesquare: Boolean = false
            var virtualinsidesquare: Boolean = false
            val reminder_lat  = inputData.getString("reminder_latitude")!!.toDouble()
            val reminder_lon = inputData.getString("reminder_longitude")!!.toDouble()
            val returnLocation: LatLng?

            while(true)
            {
                if(isStopped){
                    return Result.failure()
                }

                if(Graph.currentLocation != null)
                {
                    currentinsidesquare =
                        reminder_lon - 0.002 <= Graph.currentLocation!!.longitude && Graph.currentLocation!!.longitude <= reminder_lon + 0.002 &&
                        reminder_lat - 0.002 <= Graph.currentLocation!!.latitude && Graph.currentLocation!!.latitude <= reminder_lat + 0.002
                }
                if(Graph.virtualLocation != null)
                {
                    virtualinsidesquare =
                        reminder_lon - 0.002 <= Graph.virtualLocation!!.longitude && Graph.virtualLocation!!.longitude <= reminder_lon + 0.002 &&
                        reminder_lat - 0.002 <= Graph.virtualLocation!!.latitude && Graph.virtualLocation!!.latitude <= reminder_lat + 0.002
                }
                if(Graph.currentLocation == null && Graph.virtualLocation == null)
                {
                    continue
                }

                if(currentinsidesquare)
                {
                    returnLocation = Graph.currentLocation
                    break
                }else if(virtualinsidesquare)
                {
                    returnLocation = Graph.virtualLocation
                    break
                }

            }

            val outputLocation: Data = workDataOf("userlocation_lat" to returnLocation!!.latitude.toString(), "userlocation_lon" to returnLocation!!.longitude.toString())
            Result.success(outputLocation)
        }catch(e: Exception){
            println(e.toString())
            Result.failure()
        }

    }

}