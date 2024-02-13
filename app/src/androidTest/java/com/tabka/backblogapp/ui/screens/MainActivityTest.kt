package com.tabka.backblogapp.ui.screens

import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.tabka.backblogapp.MainActivity
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Test
    fun appLaunchesSuccessfully() {
        ActivityScenario.launch(MainActivity::class.java)
        // You can add assertions here to verify the behavior of your MainActivity
    }
}
