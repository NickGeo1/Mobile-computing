package com.example.exercise4.ui.profile

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercise4.Graph
import com.example.exercise4.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel (private val userRepository: UserRepository = Graph.userRepository, username: String) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())

    val state: StateFlow<ProfileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            _state.value = ProfileViewState(Graph.userRepository.selectUser(username).img)
        }
    }

}

data class ProfileViewState(val userimage: ByteArray? = null)