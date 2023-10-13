package com.wm.dashdiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.google.firebase.FirebaseApp
import com.wm.dashdiary.data.database.ImageToUploadDao
import com.wm.dashdiary.data.repository.retryUploadingImageToFirebase
import com.wm.dashdiary.navigation.Screen
import com.wm.dashdiary.navigation.SetupNavGraph
import com.wm.dashdiary.ui.theme.DashDiaryAppTheme
import dagger.hilt.android.AndroidEntryPoint
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var imagesToUploadDao: ImageToUploadDao
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
                SetupNavGraph(startDestination = getStartDestination(),
                    navController = navController,
                    onDataLoaded = { keepSplashOpened = false })
            }
        }
        cleanupCheck(scope = lifecycleScope, imageToUploadDao = imagesToUploadDao)
    }
}

private fun cleanupCheck(scope: CoroutineScope, imageToUploadDao: ImageToUploadDao) {
    scope.launch(Dispatchers.IO) {
        val result = imageToUploadDao.getAllImages()
        result.forEach { imageToUpload ->
            retryUploadingImageToFirebase(imageToUpload = imageToUpload, onSuccess = {
                scope.launch(Dispatchers.IO) {
                    imageToUploadDao.cleanupImage(imageId = imageToUpload.id)
                }
            })

        }

    }

}

private fun getStartDestination(): String {
    val user = App.Companion.create(BuildConfig.AtlasAppId).currentUser
    return if (user != null && user.loggedIn) Screen.Home.route
    else Screen.Authentication.route
}
