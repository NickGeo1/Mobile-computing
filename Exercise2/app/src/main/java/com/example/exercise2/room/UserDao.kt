package com.example.exercise2.room

import androidx.room.*
import com.example.exercise2.entities.User


@Dao
abstract class UserDao {
    @Query(value = "SELECT * FROM users WHERE username = :un")
    abstract suspend fun selectUser(un: String): User //When we want to select a user by his unique username

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(entity: User) //For user insert at the beginning

    @Update(onConflict = OnConflictStrategy.REPLACE) //When user wants to change username/picture
    abstract suspend fun updateUser(entity: User)

    @Delete
    abstract suspend fun deleteUser(entity: User) //For user delete
}