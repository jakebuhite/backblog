package com.tabka.backblogapp

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp


class BackBlog : Application() {
    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
        FirebaseApp.initializeApp(this)

    }

    companion object {
        var appContext: Context? = null
    }
}