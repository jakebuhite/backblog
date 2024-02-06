package com.tabka.backblogapp

import android.app.Application
import android.content.Context

class BackBlog : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
    }
}