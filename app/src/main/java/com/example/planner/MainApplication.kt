package com.example.planner

import android.app.Application
import com.example.planner.data.di.MainServiceLocator

class MainApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        MainServiceLocator.initialize(application = this)
    }
}