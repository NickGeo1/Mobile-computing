package com.example.exercise2.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exercise2.entities.Reminder
import com.example.exercise2.entities.User

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
