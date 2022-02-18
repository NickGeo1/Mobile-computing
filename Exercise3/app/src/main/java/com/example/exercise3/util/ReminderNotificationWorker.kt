package com.example.exercise3.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception
import java.util.*

class ReminderNotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {
        val whenreminder = inputData.getString("Date")
        return try {
            Log.i("Dates ", "Today ${Date()} reminder $whenreminder")
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}
