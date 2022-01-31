package com.example.exercise1.entities

import android.media.Image
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [
        Index("username", unique = true)
    ]
)
data class User(
    @PrimaryKey @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "img") var img: Image?
)
