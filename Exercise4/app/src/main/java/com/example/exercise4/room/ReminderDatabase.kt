package com.example.exercise4.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exercise4.entities.Reminder
import com.example.exercise4.entities.User

//Database schema of application

@Database(
    entities = [User::class, Reminder::class],
    version = 11,
    exportSchema = false
)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reminderDao(): ReminderDao
}
