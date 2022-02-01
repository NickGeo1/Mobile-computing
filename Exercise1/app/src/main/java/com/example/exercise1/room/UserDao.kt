package com.example.exercise1.room

import androidx.room.*
import com.example.exercise1.entities.User
import kotlinx.coroutines.flow.Flow


@Dao
abstract class UserDao {
    @Query(value = "SELECT * FROM users WHERE username = :un")
    abstract suspend fun selectUser(un: String): User //When we want to select a user by his unique username

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(entity: User) //For user insert at the beginning

    @Update(onConflict = OnConflictStrategy.REPLACE) //When user wants to change username/picture
    abstract suspend fun updateUser(entity: User)
}