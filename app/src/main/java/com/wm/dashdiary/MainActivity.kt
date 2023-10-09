package com.wm.dashdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.wm.dashdiary.navigation.Screen
import com.wm.dashdiary.navigation.SetupNavGraph
import com.wm.dashdiary.ui.theme.DashDiaryAppTheme
import io.realm.kotlin.mongodb.App


class MainActivity : ComponentActivity() {
    private var keepSplashOpened = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen().setKeepOnScreenCondition { keepSplashOpened }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        actionBar?.hide()
        FirebaseApp.initializeApp(this)



        setContent {
            DashDiaryAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = { keepSplashOpened = false }
                )
            }
        }
    }
}

private fun getStartDestination(): String {
    val user = App.Companion.create(BuildConfig.AtlasAppId).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}
