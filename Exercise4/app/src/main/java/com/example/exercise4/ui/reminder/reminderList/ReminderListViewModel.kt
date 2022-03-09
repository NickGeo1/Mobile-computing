package com.example.exercise4.ui.reminder.reminderList

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.*
import com.example.exercise4.Graph
import com.example.exercise4.entities.Reminder
import com.example.exercise4.repository.ReminderRepository
import com.example.exercise4.repository.UserRepository
import com.example.exercise4.util.ReminderNotificationWorker
import com.example.exercise4.R
import com.example.exercise4.util.LocationReminderNotificationWorker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.lang.reflect.Array.get
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderListViewModel(private val reminder_id:String,
                            private val userid: String,
                            private val reminderRepository: ReminderRepository = Graph.reminderRepository,
                            private val userRepository: UserRepository = Graph.userRepository,
                            private val changeview: (List<Reminder>) -> Unit) : ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun changeShow(showall: Boolean): List<Reminder>{
        return reminderRepository.selectuserReminders(userid.toLong(), showall)
    }

    init {
        createNotificationChannel(context = Graph.appContext) //create the notification channel
                                //In the case of reminder list//
        //Initially, we provide a list of the seen reminders in the viewmodel state
        //We make a work for each unseen reminder and check if its time for notification

                                //In the case of reminder edit//
        //We provide the user's selected reminder on the viewmodelstate
        viewModelScope.launch {
            reminderRepository.selectuserUnseenReminders(userid.toLong()).collect { list ->
                _state.value = ReminderViewState(showreminders = reminderRepository.selectuserReminders(userid.toLong(),false), editreminder = reminderRepository.selectReminderfromid(reminder_id.toLong()))
                list.forEach{
                    checkUnseenReminderNotification(it, userRepository.selectUserFromId(userid.toLong()))
                }
            }
        }
    }

    fun checkUnseenReminderNotification(reminder: Reminder, username: String) {
        var interval: Long? = null
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")

        if(reminder.reminder_time != "")
        {
            val todaysdate = Date().time
            val reminderdate = formatter.parse(reminder.reminder_time).time //From string to Date as long
            interval = reminderdate - todaysdate
        }

        val current_calendar = Calendar.getInstance()
        current_calendar.time = Date() // Set your date object here

        val reminder_calendar = Calendar.getInstance()
        reminder_calendar.time = formatter.parse(reminder.reminder_time) // Set your date object here

        //if this unseen reminder has already pass(current minutes > reminder minutes) make the notification and update it as seen
        if(interval != null && interval < 0 && current_calendar.get(Calendar.MINUTE) > reminder_calendar.get(Calendar.MINUTE)){
            if(reminder.notification){
                createReminderDueNotification(reminder,
                    username, true,
                    reminder.latitude !="" && reminder.longitude !="",
                    "",
                    "")
            }
            viewModelScope.launch {
                reminderRepository.updateReminder(
                    Reminder(
                        reminder.id,
                        reminder.message,
                        reminder.latitude,
                        reminder.longitude,
                        reminder.reminder_time,
                        reminder.creation_time,
                        reminder.creator_id,
                        true,
                        reminder.notification))
                changeview(reminderRepository.selectuserReminders(userid.toLong(), false)) //we update the list view and we show again only the seen reminders
            }
            return
        }else if(current_calendar.get(Calendar.MINUTE) == reminder_calendar.get(Calendar.MINUTE)) //if the minutes are same make a work with interval 0
            interval = 0

        //val workManager = WorkManager.getInstance(Graph.appContext)
        val notificationWorker =
        //we use the doWork method on ReminderNotificationWorker or LocationReminderNotificationWorker class to do our work.
        //if we have only a location requirement we run the worker class that checks constantly for location
        if (reminder.longitude != "" && reminder.latitude != "" && reminder.reminder_time == "")
            OneTimeWorkRequestBuilder<LocationReminderNotificationWorker>()
                .setInputData(workDataOf("reminder_latitude" to reminder.latitude, "reminder_longitude" to reminder.longitude))
                .addTag(reminder.id.toString()) //set tag to location worker in order to cancel it later
        else
            //At this case we only have to run the worker when its time for notification(with or without location required). So we use ReminderNotificationWorker class
            OneTimeWorkRequestBuilder<ReminderNotificationWorker>()
            .setInitialDelay(interval?:0,TimeUnit.MILLISECONDS) //wait for the interval to make the notification

        val notificationWorkerbuilded = notificationWorker.build()
        Graph.listWorkmanager.enqueue(notificationWorkerbuilded)

        //Monitoring for state of work
        Graph.listWorkmanager.getWorkInfoByIdLiveData(notificationWorkerbuilded.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED)
                {
                    if(reminder.notification){
                        //get the worker's location return values.
                        //location worker(for reminders that require only location) returns the current location captured
                        //when virtual/real location are inside the reminder area

                        createReminderDueNotification(
                            reminder,
                            username,
                            false,
                            reminder.latitude !="" && reminder.longitude !="",
                            workInfo.outputData.getString("userlocation_lat")?:"",
                            workInfo.outputData.getString("userlocation_lon")?:""
                        )
                    }
                    //after notification shows up we update this reminder with seen = true at database so it can appear in the view model
                    viewModelScope.launch {
                        reminderRepository.updateReminder(
                            Reminder(
                                reminder.id,
                                reminder.message,
                                reminder.latitude,
                                reminder.longitude,
                                reminder.reminder_time,
                                reminder.creation_time,
                                reminder.creator_id,
                                true,
                                reminder.notification))
                        changeview(reminderRepository.selectuserReminders(userid.toLong(), false))
                    }
                }
            }
        }
    }

fun insideArea(reminder_latitude: Double, reminder_longitude: Double, userLocation_lat: Double?, userLocation_lon: Double?): Boolean
{
    if(userLocation_lat == null || userLocation_lon == null) //we pass the current or the virtual location. If either is null we return false
        return false
    return reminder_longitude - 0.002 <= userLocation_lon && userLocation_lon <= reminder_longitude + 0.002 &&
           reminder_latitude - 0.002 <= userLocation_lat && userLocation_lat <= reminder_latitude
}

private fun createNotificationChannel(context: Context) {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelDescriptionText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun createReminderDueNotification(reminder: Reminder, username: String, passedreminder: Boolean, haslocation: Boolean, userlocation_lat: String, userlocation_lon: String){
    val notificationId = 1

                                                        //setting the reminder title//
    val reminder_title =
    if(reminder.reminder_time != "" && passedreminder)
        "Reminder occurred for user $username" //if we assigned time to reminder and is passed when we are in app
    else if(reminder.reminder_time != "" && !passedreminder)
        "Reminder occurring now for user $username" //if we assigned time to reminder and is occurring when we are in app
    else
        "Reminder occurring at a location \narea for user $username" //else at this case we assigned a location to reminder without time


                                                                //setting the reminder text//
    val reminder_text =
    //passed reminder time without location
    if(reminder.reminder_time != "" && passedreminder && !haslocation)
        "Reminder already occurred at ${reminder.reminder_time} without location area set"

    //occurring reminder time without location
    else if(reminder.reminder_time != "" && !haslocation)
        "Reminder occurring now without location area set"

    //passed reminder time with location
    else if(reminder.reminder_time != "" && passedreminder && haslocation)
        "Reminder already occurred at ${reminder.reminder_time}." +
        when {
            insideArea(reminder.latitude.toDouble(), reminder.longitude.toDouble(), Graph.currentLocation?.latitude, Graph.currentLocation?.longitude) ->
                "\nLast captured location \nLat: ${Graph.currentLocation?.latitude} \nLon: ${Graph.currentLocation?.longitude} " +
                "\nis inside the occurring area \nLat: ${reminder.latitude} \nLon: ${reminder.longitude} " +
                "\nbut it didn't captured at the required time"

            Graph.currentLocation != null ->
            "Last captured location \nLat: ${Graph.currentLocation?.latitude} \nLon: ${Graph.currentLocation?.longitude} \nis not inside the occurring area with center \nLat: ${reminder.latitude} and \nLon: ${reminder.longitude} "

            else -> "Last location captured is null"
        }
    //occurring reminder time with location
    else if(reminder.reminder_time != "" && !passedreminder && haslocation)
        "Reminder occurring now." +
        when {
            //check first virtual location details and then real location's. If either are inside the reminder area notify the user with last captured location the location
            //which is inside the area. insideArea reuturns false if any location is null
            insideArea(reminder.latitude.toDouble(), reminder.longitude.toDouble(), Graph.virtualLocation?.latitude, Graph.virtualLocation?.longitude) ->
            "\nLast captured location \nLat: ${Graph.virtualLocation?.latitude.toString()} \nLon: ${Graph.virtualLocation?.longitude.toString()} \nis inside the occurring area with center \nLat: ${reminder.latitude} \nLon: ${reminder.longitude}"

            insideArea(reminder.latitude.toDouble(), reminder.longitude.toDouble(), Graph.currentLocation?.latitude, Graph.currentLocation?.longitude) ->
            "\nLast captured location \nLat: ${Graph.currentLocation?.latitude.toString()} \nLon: ${Graph.currentLocation?.longitude.toString()} \nis inside the occurring area with center \nLat: ${reminder.latitude} \nLon: ${reminder.longitude}"

            Graph.virtualLocation != null -> "\nLast captured location \nLat: ${Graph.virtualLocation!!.latitude} \nLon: ${Graph.virtualLocation!!.longitude} \nis not inside the occurring area \nLat: ${reminder.latitude} \nLon: ${reminder.longitude}"

            Graph.currentLocation != null -> "\nLast captured location \nLat: ${Graph.currentLocation!!.latitude} \nLon: ${Graph.currentLocation!!.longitude} \nis not inside the occurring area Lat: ${reminder.latitude} \nLon: ${reminder.longitude}"

            else -> "Both virtual and current user location found null"
        }
    //at this case the reminder is occurring because it requires only location, and we are currently in the required area
    //either with virtual location or real location
    else
        "Reminder occurring now at \nLat: $userlocation_lat \nLon: $userlocation_lon"

    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle(reminder_title)
        .setStyle(NotificationCompat.BigTextStyle().bigText(reminder_text))
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}
data class ReminderViewState(
    var showreminders: List<Reminder> = emptyList(),
    val editreminder: Reminder? = null
)