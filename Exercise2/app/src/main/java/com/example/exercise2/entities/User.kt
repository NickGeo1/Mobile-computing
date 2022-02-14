package com.example.exercise2.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

//Our database users table. We choose to autogenerate id's and set unique values to usernames

@Entity(
    tableName = "users",
    indices = [
        Index("username", unique = true)
    ]
)

data class User(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
    @ColumnInfo(name = "username") var username: String,
    @ColumnInfo(name = "password") val password: String,
    @ColumnInfo(name = "img") var img: String?
)
