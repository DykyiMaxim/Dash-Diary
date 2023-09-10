package com.wm.dashdiary.navigation

import DisplayAlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.stevdzasan.messagebar.rememberMessageBarState
import com.stevdzasan.onetap.rememberOneTapSignInState
import com.wm.dashdiary.BuildConfig
import com.wm.dashdiary.data.repository.RequestState
import com.wm.dashdiary.presentation.screens.auth.AuthenticationScreen
import com.wm.dashdiary.presentation.screens.auth.AuthenticationViewModel
import com.wm.dashdiary.presentation.screens.home.HomeScreen
import com.wm.dashdiary.presentation.screens.home.HomeViewModel
import com.wm.dashdiary.presentation.screens.write.WriteScreen
import io.realm.kotlin.mongodb.App
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun SetupNavGraph(
    startDestination: String,
    navController: NavHostController,
    onDataLoaded: () -> Unit
) {
    NavHost(navController = navController, startDestination) {
        authenticationRout(
            NavigateToHome = {
                navController.popBackStack()
                navController.navigate(Screen.Home.route)
            },
            onDataLoaded = onDataLoaded
        )
        HomeScreenRout(
            NavigateToWrite = {
                navController.navigate(Screen.Write.route)
            },
            NavigateToAuth = {
                navController.popBackStack()
                navController.navigate(Screen.Authentication.route)
            },
            onDataLoaded = onDataLoaded
        )
        WriteRout(
            onBackPressed = { navController.popBackStack() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
fun NavGraphBuilder.authenticationRout(
    NavigateToHome: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Authentication.route) {
        val viewModel: AuthenticationViewModel = viewModel()
        val authenticated by viewModel.Authenticated
        val loadingState by viewModel.LoadingState
        val oneTapState = rememberOneTapSignInState()
        val messageBarState = rememberMessageBarState()

        LaunchedEffect(key1 = Unit) { onDataLoaded() }

        AuthenticationScreen(
            authenticated = authenticated,
            loadingState = loadingState,
            oneTapState = oneTapState,
            messageBarState = messageBarState,
            onButtonClicked = {
                oneTapState.open()
                viewModel.setLoading(true)
            },
            onTokenIdReceives = { tokenId ->
                viewModel.SignInWithAtlas(
                    tokenId = tokenId,
                    onSuccess = {
                        messageBarState.addSuccess("Successfully authenticated")
                        viewModel.setLoading(false)
                    },

                    onError = { messageBarState.addError(it) })
                messageBarState.addSuccess("Successfully authenticated")
            },
            onDialogDismast = { message ->
                messageBarState.addError(Exception(message))
                viewModel.setLoading(false)
            },
            navigateToHome = NavigateToHome

        )
    }
}


fun NavGraphBuilder.HomeScreenRout(
    NavigateToWrite: () -> Unit,
    NavigateToAuth: () -> Unit,
    onDataLoaded: () -> Unit
) {
    composable(route = Screen.Home.route) {
        val viewModel: HomeViewModel = viewModel()
        val diaries by viewModel.diaries
        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        var signOutDialogOpen by remember { mutableStateOf(false) }
        LaunchedEffect(key1 = diaries) {
            if (diaries !is RequestState.Loading) {
                onDataLoaded()
            }
        }
        HomeScreen(
            diaries = diaries,
            drawerState = drawerState,
            onMenuClicked = { scope.launch { drawerState.open() } },
            onSignOutClicked = { signOutDialogOpen = true },
            navigateToWrite = NavigateToWrite
        )
        DisplayAlertDialog(
            title = "Sign Out",
            message = "Are you sure you want to Sign Out from your Google Account?",
            dialogOpened = signOutDialogOpen,
            onDialogClosed = { signOutDialogOpen = false },
            onYesClicked = {
                scope.launch(Dispatchers.IO) {
                    val user = App.create(BuildConfig.AtlasAppId).currentUser
                    if (user != null) {
                        user.logOut()
                        withContext(Dispatchers.Main) {
                            NavigateToAuth()
                        }
                    }
                }
            }
        )

    }
}

fun NavGraphBuilder.WriteRout(onBackPressed: () -> Unit) {
    composable(
        route = Screen.Write.route,
        arguments = listOf(navArgument(name = "WritingScreenIdNav") {
            type = NavType.StringType
            nullable = true
            defaultValue = null
        })

    ) {
        WriteScreen(onBackPressed)
    }
}
