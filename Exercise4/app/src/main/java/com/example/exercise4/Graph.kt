package com.example.exercise4

import android.content.Context
import androidx.room.Room
import com.example.exercise4.repository.ReminderRepository
import com.example.exercise4.repository.UserRepository
import com.example.exercise4.room.ReminderDatabase

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