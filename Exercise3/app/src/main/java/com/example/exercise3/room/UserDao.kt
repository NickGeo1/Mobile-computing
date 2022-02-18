package com.example.exercise3.room

import androidx.room.*
import com.example.exercise3.entities.User


@Dao
abstract class UserDao {
    @Query(value = "SELECT * FROM users WHERE username = :un")
    abstract suspend fun selectUser(un: String): User //When we want to select a user by his unique username

    @Query(value = "SELECT username FROM users WHERE id = :uid")
    abstract suspend fun selectUserFromId(uid: Long): String //When we want to select the username of user by his unique id

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(entity: User) //For user insert at the beginning

    @Update(onConflict = OnConflictStrategy.REPLACE) //When user wants to change username/picture
    abstract suspend fun updateUser(entity: User)

    @Delete
    abstract suspend fun deleteUser(entity: User) //For user delete
}