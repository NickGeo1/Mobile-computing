package com.example.exercise4.repository

import com.example.exercise4.entities.Reminder
import com.example.exercise4.room.ReminderDao
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ReminderRepository(private val reminderDao: ReminderDao) {

    suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder)

    suspend fun selectReminder(reminder: Reminder): Reminder? = reminderDao.selectReminder(reminder.creator_id, reminder.reminder_time, reminder.latitude, reminder.longitude, reminder.message)

    fun selectuserUnseenReminders(uid: Long): Flow<List<Reminder>>{
        return reminderDao.selectuserUnseenReminders(uid)
    }

    suspend fun selectuserReminders(uid: Long, showall:Boolean): List<Reminder>{
        return if(showall){
            reminderDao.selectuserReminders(uid)
        }else{
            reminderDao.selectuserSeenReminders(uid)
        }
    }

    suspend fun updateReminder(entity: Reminder) = reminderDao.updateReminder(entity)

    suspend fun deleteReminder(entity: Reminder){
        reminderDao.deleteReminder(entity)
    }

    suspend fun selectReminderfromid(id: Long) = reminderDao.selectReminderfromid(id)

}

private fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))

}