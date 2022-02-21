package com.example.exercise3.util

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.lang.Exception

class ReminderNotificationWorker(
    context: Context,
    userParameters: WorkerParameters
) : Worker(context, userParameters) {

    override fun doWork(): Result {

//        return try{
//        println("Interval completed, Work returning success")
//            Result.success()
//        }catch(e:Exception){
//            Result.failure()
//        }

        return Result.success()
    }

}
