package com.example.exercise1

import android.app.Application

class ReminderApplication: Application() {
    override fun onCreate(){
        super.onCreate()
        Graph.provide(this)
    }
}