package com.example.exercise3.util

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class ReminderNotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val reminderdate = formatter.parse(inputData.getString("Date")) //get the reminder date string and parse it to Date
        var currentdate: Date
        while (true){
            currentdate = formatter.parse(formatter.format(Date())) //format the current date to the correct string form and then parse to Date
            if(currentdate.compareTo(reminderdate) > 0) { //in this case we return failure for the specific notification because it's time has passed
                return Result.failure()
            }
            if(currentdate.compareTo(reminderdate) == 0) { //when the current date is equal to the reminder date
                return Result.success()
            }
        }
    }

}
