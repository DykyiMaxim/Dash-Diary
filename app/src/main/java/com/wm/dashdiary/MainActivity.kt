package com.wm.dashdiary

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.wm.dashdiary.navigation.Screen
import com.wm.dashdiary.navigation.SetupNavGraph
import com.wm.dashdiary.ui.theme.DashDiaryAppTheme
import io.realm.kotlin.mongodb.App


class MainActivity : ComponentActivity() {
    private var keepSplashOpened = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        actionBar?.hide();



        setContent {
            DashDiaryAppTheme {
                val navController = rememberNavController()
                SetupNavGraph(
                    startDestinatio = getStartDestination(),
                    navController = navController
                )
            }
        }
    }
}

private fun getStartDestination():String{
    val user = App.Companion.create(BuildConfig.AtlasAppId).currentUser
    return if(user!=null&&user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}
