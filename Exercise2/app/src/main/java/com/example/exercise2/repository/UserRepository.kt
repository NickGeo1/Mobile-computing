package com.example.exercise2.repository

import com.example.exercise2.entities.User
import com.example.exercise2.room.UserDao

class UserRepository(private val userDao: UserDao){

    suspend fun selectUser(un: String) = userDao.selectUser(un)

    suspend fun insertUser(user: User) { //in case we insert a user that exists, do nothing, else insert
       when(val temp = userDao.selectUser(user.username)){
            null -> userDao.insertUser(user)
            else -> return
        }
    }

    //in case there is already a user with the new username, do nothing else update the old username
    //(see UserInitialisaton)
    suspend fun updateUser(newuser: User) = userDao.updateUser(newuser)


}