package com.example.exercise2

import android.app.Application

class ReminderApplication: Application() {
    override fun onCreate(){
        super.onCreate()
        Graph.provide(this)
    }
}