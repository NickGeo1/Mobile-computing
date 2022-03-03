package com.example.exercise4.entities

import androidx.room.*

//Reminder data class(Not used at the moment)
@Entity(
    tableName="reminders",
    indices = [
    Index("id", unique = true),
    Index("creator_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["creator_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)


data class Reminder(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "message") val message: String,
    @ColumnInfo(name = "location_x") val location_x: Int,
    @ColumnInfo(name = "location_y") val location_y: Int,
    @ColumnInfo(name = "reminder_time") val reminder_time: String,
    @ColumnInfo(name = "creation_time") val creation_time: Long,
    @ColumnInfo(name = "creator_id") val creator_id: Long,
    @ColumnInfo(name = "reminder_seen") val reminder_seen: Boolean,
    @ColumnInfo(name = "notification") val notification: Boolean
)

