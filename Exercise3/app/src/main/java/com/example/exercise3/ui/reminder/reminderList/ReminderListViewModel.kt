package com.example.exercise3.ui.reminder.reminderList

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.example.exercise3.Graph
import com.example.exercise3.entities.Reminder
import com.example.exercise3.repository.ReminderRepository
import com.example.exercise3.util.ReminderNotificationWorker
import exercise2.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class ReminderListViewModel(private val reminder_id:String, private val userid: String, private val reminderRepository: ReminderRepository = Graph.reminderRepository) : ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    init {
        viewModelScope.launch {
            createNotificationChannel(context = Graph.appContext)
            reminderRepository.selectuserReminders(userid.toLong()).collect { list ->
                _state.value = ReminderViewState(reminders = list, reminder = reminderRepository.selectReminderfromid(reminder_id.toLong()))
            }
            _state.value.reminders.forEach{
                checkReminderNotification(it)
            }

        }
    }

    private fun checkReminderNotification(reminder: Reminder) {
        val workManager = WorkManager.getInstance(Graph.appContext)
        val notificationWorker = OneTimeWorkRequestBuilder<ReminderNotificationWorker>() //we use the doWork method on ReminderNotificationWorker class to do our work
        val data = Data.Builder() //for parameter pass at ReminderNotificationWorker class constructor
        //data.putAll(mapOf("Reminder" to reminder)) //set the reminder parameter
        data.putString("Date", reminder.reminder_time)
        notificationWorker.setInputData(data.build()) //pass the parameters
        val notificationWorkerbuilded = notificationWorker.build()
        workManager.enqueue(notificationWorkerbuilded)

        //Monitoring for state of work
        workManager.getWorkInfoByIdLiveData(notificationWorkerbuilded.id)
            .observeForever { workInfo ->
                if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                    createReminderDueNotification(reminder, userid)
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

private fun createReminderDueNotification(reminder: Reminder, userid: String){
    val notificationId = 1
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Reminder occurring for user $userid")
        .setContentText("Reminder ${reminder.message} is occurring now at location (${reminder.location_x},${reminder.location_y})")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}
data class ReminderViewState(
    val reminders: List<Reminder> = emptyList(),
    val reminder: Reminder? = null
)