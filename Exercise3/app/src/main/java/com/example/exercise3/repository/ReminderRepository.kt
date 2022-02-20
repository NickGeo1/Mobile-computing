package com.example.exercise3.repository

import com.example.exercise3.entities.Reminder
import com.example.exercise3.room.ReminderDao
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.*

class ReminderRepository(private val reminderDao: ReminderDao) {

    suspend fun insertReminder(reminder: Reminder) = reminderDao.insertReminder(reminder)

    suspend fun selectReminder(reminder: Reminder): Reminder? = reminderDao.selectReminder(reminder.creator_id, reminder.reminder_time, reminder.location_x, reminder.location_y, reminder.message)

    suspend fun updateReminder(entity: Reminder) = reminderDao.updateReminder(entity)

    suspend fun deleteReminder(entity: Reminder) = reminderDao.deleteReminder(entity)

    fun selectuserUnseenReminders(uid: Long): Flow<List<Reminder>>{
        return reminderDao.selectuserUnseenReminders(uid)
    }

    suspend fun selectuserSeenReminders(uid: Long): List<Reminder>{
        return reminderDao.selectuserSeenReminders(uid)
    }

    suspend fun selectReminderfromid(id: Long) = reminderDao.selectReminderfromid(id)

}

private fun Long.toDateString(): String {
    return SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault()).format(Date(this))

}