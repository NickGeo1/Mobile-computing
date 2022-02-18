package com.example.exercise3.room

import androidx.room.*
import com.example.exercise3.entities.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
abstract class ReminderDao {
    @Query(value = "SELECT * FROM reminders WHERE creator_id = :uid")
    abstract fun selectuserReminders(uid: Long): Flow<List<Reminder>> //When we want to select all the reminders of a user

    @Query(value = "SELECT * FROM reminders WHERE id = :id")
    abstract suspend fun selectReminderfromid(id: Long): Reminder //When we want to select a reminder by id

    @Query(value = "SELECT * FROM reminders WHERE creator_id=:uid and reminder_time = :time and location_x = :x and location_y = :y and message = :m")
    abstract suspend fun selectReminder(uid:Long, time: String, x:Int, y:Int, m:String): Reminder? //When we want to check if theres already a reminder with the same message location and date

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertReminder(entity: Reminder): Long //For reminder insert

    @Update(onConflict = OnConflictStrategy.REPLACE) //When user wants to change a reminder
    abstract suspend fun updateReminder(entity: Reminder)

    @Delete
    abstract suspend fun deleteReminder(entity: Reminder) //For reminder delete
}