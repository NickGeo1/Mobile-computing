package com.example.exercise3.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exercise3.entities.Reminder
import com.example.exercise3.entities.User

//Database schema of application

@Database(
    entities = [User::class, Reminder::class],
    version = 7,
    exportSchema = false
)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun reminderDao(): ReminderDao
}
