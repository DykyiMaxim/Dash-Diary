package com.wm.dashdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.wm.dashdiary.navigation.Screen
import com.wm.dashdiary.navigation.SetupNavGraph


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            SetupNavGraph(
                startDestinatio = Screen.Authentication.route,
                navController = navController)

        }
    }
}

