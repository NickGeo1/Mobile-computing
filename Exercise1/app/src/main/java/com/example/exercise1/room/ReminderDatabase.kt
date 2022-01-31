package com.example.exercise1.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exercise1.entities.User


@Database(
    entities = [User::class],
    version = 1,
    exportSchema = false
)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
