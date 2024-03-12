//
//  Test.kt
//  backblog
//
//  Created by Jake Buhite on 1/24/24.
//
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