package com.example.exercise3.ui.reminder.reminderList

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.work.*
import com.example.exercise3.Graph
import com.example.exercise3.UserInitialisaton
import com.example.exercise3.entities.Reminder
import com.example.exercise3.repository.ReminderRepository
import com.example.exercise3.repository.UserRepository
import com.example.exercise3.util.ReminderNotificationWorker
import exercise2.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class ReminderListViewModel(private val reminder_id:String,
                            private val userid: String,
                            private val navController: NavController,
                            private val reminderRepository: ReminderRepository = Graph.reminderRepository,
                            private val userRepository: UserRepository = Graph.userRepository) : ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    suspend fun changeShow(showall: Boolean): List<Reminder>{
        return reminderRepository.selectuserReminders(userid.toLong(), showall)
    }

    init {
        createNotificationChannel(context = Graph.appContext) //create the notification channel
                                //In the case of reminder list//
        //Initialy, we provide a list of the seen reminders in the viewmodel state
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

    private fun checkUnseenReminderNotification(reminder: Reminder, username: String) {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val today:Long = Date().time
        val reminderdate = formatter.parse(reminder.reminder_time).time //From string to Date as long
        val interval = reminderdate - today
        if(interval < 0){ //if this unseen reminder has already pass, make the notification and update it as seen
            if(reminder.notification){
                createReminderDueNotification(reminder, username)
            }
            viewModelScope.launch {
                reminderRepository.updateReminder(
                    Reminder(
                        reminder.id,
                        reminder.message,
                        reminder.location_x,
                        reminder.location_y,
                        reminder.reminder_time,
                        reminder.creation_time,
                        reminder.creator_id,
                        true,
                        reminder.notification))
            }
            navController.navigate("main/$username/${reminder.creator_id}") //navigate again to activity to see the results
            return
        }
        val workManager = WorkManager.getInstance(Graph.appContext)
        val notificationWorker = OneTimeWorkRequestBuilder<ReminderNotificationWorker>() //we use the doWork method on ReminderNotificationWorker class to do our work
            .setInitialDelay(interval,TimeUnit.MILLISECONDS) //wait for the interval to make the notification

        println(interval.toString())
        val notificationWorkerbuilded = notificationWorker.build()
        workManager.enqueue(notificationWorkerbuilded)
        println(reminder.message)
        //Monitoring for state of work
        workManager.getWorkInfoByIdLiveData(notificationWorkerbuilded.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED)
                {
                    if(reminder.notification){
                        createReminderDueNotification(reminder, username)
                    }
                    //after notification shows up we update this reminder with seen = true at database so it can appear in the view model
                    viewModelScope.launch {
                        reminderRepository.updateReminder(
                            Reminder(
                                reminder.id,
                                reminder.message,
                                reminder.location_x,
                                reminder.location_y,
                                reminder.reminder_time,
                                reminder.creation_time,
                                reminder.creator_id,
                                true,
                                reminder.notification))
                    }
                    navController.navigate("main/$username/${reminder.creator_id}") //navigate again to activity to see the results
                }
            }
        }
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

fun createReminderDueNotification(reminder: Reminder, username: String){
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Reminder occurring for user $username")
        .setStyle(NotificationCompat.BigTextStyle().bigText("Reminder ${reminder.message} is occurring now at location (${reminder.location_x},${reminder.location_y})"))
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