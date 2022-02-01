package com.example.exercise1

import android.content.Context
import androidx.room.Room
import com.example.exercise1.repository.UserRepository
import com.example.exercise1.room.ReminderDatabase

object Graph {
    lateinit var database: ReminderDatabase
        private set

    val userRepository by lazy {
        UserRepository(
            userDao = database.userDao()
        )
    }

    fun provide(context: Context){
        database = Room.databaseBuilder(context, ReminderDatabase::class.java, "user-reminder.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}