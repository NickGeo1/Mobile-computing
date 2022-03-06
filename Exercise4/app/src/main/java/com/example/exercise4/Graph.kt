package com.example.exercise4

import android.content.Context
import android.content.ContextWrapper
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.exercise4.entities.Reminder
import com.example.exercise4.repository.ReminderRepository
import com.example.exercise4.repository.UserRepository
import com.example.exercise4.room.ReminderDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import kotlin.properties.Delegates

object Graph {
    lateinit var database: ReminderDatabase

    lateinit var appContext: Context

    lateinit var currentactivity: String //At each composable, we change this value to the name of that composable

    var currentLocation: Location? = null //Current map location of user

    var markeradded by Delegates.notNull<Boolean>() //a boolean to indicate if a marker was added. Google map coroutine adds two markers and we want to remove the second

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
        database = Room.databaseBuilder(context, ReminderDatabase::class.java, "user-reminder.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}