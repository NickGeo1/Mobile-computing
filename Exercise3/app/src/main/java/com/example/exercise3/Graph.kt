package com.example.exercise3

import android.content.Context
import androidx.room.Room
import com.example.exercise3.repository.ReminderRepository
import com.example.exercise3.repository.UserRepository
import com.example.exercise3.room.ReminderDatabase

object Graph {
    lateinit var database: ReminderDatabase

    lateinit var appContext: Context

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