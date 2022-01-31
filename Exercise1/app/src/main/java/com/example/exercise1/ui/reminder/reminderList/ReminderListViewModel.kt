package com.example.exercise1.ui.reminder.reminderList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercise1.entities.Reminder
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReminderListViewModel : ViewModel() {
    private val _state = MutableStateFlow(ReminderViewState())

    val state: StateFlow<ReminderViewState>
        get() = _state

    init {
        val list = mutableListOf<Reminder>()
        list.addAll(listOf(
            Reminder(1, title = "Go shopping", Date()),
            Reminder(2,title = "Do laundry",Date()),
            Reminder(3, title = "Study",Date()),
            Reminder(4,title = "Meet friends",Date()),
            Reminder(5,title = "Take out dog",Date())
        ))
        viewModelScope.launch {
            _state.value = ReminderViewState(
                reminders = list
            )
        }
    }
}

data class ReminderViewState(
    val reminders: List<Reminder> = emptyList()
)