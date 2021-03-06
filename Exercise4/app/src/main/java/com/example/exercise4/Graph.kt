package com.example.exercise4

import android.content.Context
import android.location.Location
import androidx.room.Room
import androidx.work.WorkManager
import com.example.exercise4.repository.ReminderRepository
import com.example.exercise4.repository.UserRepository
import com.example.exercise4.room.ReminderDatabase
import com.google.android.gms.maps.model.LatLng
import kotlin.properties.Delegates

object Graph {
    lateinit var database: ReminderDatabase

    lateinit var appContext: Context

    lateinit var currentactivity: String //At each composable, we change this value to the name of that composable

    var currentLocation: LatLng? = null //Current map location of user

    var virtualLocation: LatLng? = null //Virtual map location of user. We set this manually during our app

    var markeradded by Delegates.notNull<Boolean>() //a boolean to indicate if a marker was added. Google map coroutine adds two markers and we want to remove the second

    lateinit var listWorkmanager: WorkManager //a global work manager object we are going to use in order to cancel works when we need to

    val userRepository by lazy {
        UserRepository(
            userDao = database.userDao()
        )
    }

    val reminderRepository by lazy {
        ReminderRepository(
            reminderDao = database.reminderDao()
        )
    }

    fun provide(context: Context){
        appContext = context
        listWorkmanager = WorkManager.getInstance(context)
        database = Room.databaseBuilder(context, ReminderDatabase::class.java, "user-reminder.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}