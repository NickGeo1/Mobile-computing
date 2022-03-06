package com.example.exercise4

import android.app.Application
import android.location.Location
import com.google.android.gms.location.LocationServices

class ReminderApplication: Application() {
    override fun onCreate(){
        super.onCreate()
        Graph.provide(this)
    }
}