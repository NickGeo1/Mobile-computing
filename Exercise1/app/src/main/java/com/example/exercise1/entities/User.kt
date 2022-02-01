package com.example.exercise1.entities

import android.media.Image
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.versionedparcelable.VersionedParcelize

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
