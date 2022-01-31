package com.example.exercise1.repository

import com.example.exercise1.entities.User
import com.example.exercise1.room.UserDao

class UserRepository(private val userDao: UserDao){

    suspend fun selectUser(un: String, pass: String) = userDao.selectUser(un, pass)

    suspend fun insertUser(user: User): String {
        return when(val temp = userDao.selectUser(user.username, user.password)){
            null -> userDao.insertUser(user)
            else -> "There is already a user with username ${user.username}"
        }
    }
}