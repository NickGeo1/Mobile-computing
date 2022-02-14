package com.example.exercise2

import android.content.Context
import androidx.room.Room
import com.example.exercise2.repository.ReminderRepository
import com.example.exercise2.repository.UserRepository
import com.example.exercise2.room.ReminderDatabase

object Graph {
    lateinit var database: ReminderDatabase
        private set

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
        database = Room.databaseBuilder(context, ReminderDatabase::class.java, "user-reminder.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}