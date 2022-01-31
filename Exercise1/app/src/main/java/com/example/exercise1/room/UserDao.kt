package com.example.exercise1.room

import androidx.room.*
import com.example.exercise1.entities.User



@Dao
abstract class UserDao {
    @Query(value = "SELECT * FROM users WHERE username = :un AND password = :pass")
    abstract suspend fun selectUser(un: String, pass: String): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertUser(entity: User): String //For user insert at the beginning

    @Update(onConflict = OnConflictStrategy.REPLACE) //When user wants to change username/picture
    abstract suspend fun updateUser(entity: User)
}