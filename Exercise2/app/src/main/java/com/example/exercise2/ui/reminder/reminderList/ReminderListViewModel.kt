package com.example.exercise2.ui.reminder.reminderList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercise2.Graph
import com.example.exercise2.entities.Reminder
import com.example.exercise2.repository.ReminderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ReminderListViewModel(private val reminder_id:String, private val userid: String, private val reminderRepository: ReminderRepository = Graph.reminderRepository) : ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    init {
        viewModelScope.launch {
            reminderRepository.selectuserReminders(userid.toLong()).collect { list ->
                _state.value = ReminderViewState(reminders = list, reminder = reminderRepository.selectReminderfromid(reminder_id.toLong()))
            }
        }
    }
}

data class ReminderViewState(
    val reminders: List<Reminder> = emptyList(),
    val reminder: Reminder? = null
)